package fi.nakoradio.hwo.integration.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import org.apache.log4j.Logger;

import fi.nakoradio.hwo.physics.Constants;
import fi.nakoradio.hwo.util.SizedStack;
import fi.nakoradio.hwo.util.Utils;

public class SocketMessenger  implements Messenger {

	private static Logger logger = Logger.getLogger(SocketMessenger.class);
	
	private SizedStack<InputMessage> controlMessages;
	private SizedStack<InputMessage> positionMessages;
	private InputMessage latestPositionMessage;
	private BotSocket socket;
	private Vector<Long> outputMessageTimestamps;
	private BufferedWriter fileOut;
	
	
	
	public SocketMessenger(){
		this.outputMessageTimestamps = new Vector<Long>();
	}
	
	public SocketMessenger(String host, int port){
		this.socket = new BotSocket(host, port);
		this.controlMessages = new SizedStack<InputMessage>(50);
		this.positionMessages = new SizedStack<InputMessage>(50);
		this.outputMessageTimestamps = new Vector<Long>();
		
		try {
			long time = System.currentTimeMillis();
			FileWriter fstream;
			fstream = new FileWriter("log/io_" + time + ".log");
			this.fileOut = new BufferedWriter(fstream);
		} catch (IOException e) {
			logger.error("Could not open file for writing");
			e.printStackTrace();
		}
		
	}
	
	private void logToFile(String message, boolean out){
		if(fileOut != null){
			try {
				fileOut.write(System.currentTimeMillis() + " " + out +  " - " + message + "\n");
				fileOut.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void start() {
		(new Thread(this)).start();
	}
	
	@Override
	public void run() {
		
		try {
			BufferedReader in = new BufferedReader(this.socket.getIn());
			String messageData = "";
			while (!Thread.interrupted() && (messageData = in.readLine()) != null) {
				try {
					logToFile(messageData,false);
					InputMessage message = new InputMessage(messageData);
					logger.info(messageData);
					
					if(message.isGameOverMessage() || message.isGameStartedMessage() || message.isJoinedMessage()){
						controlMessages.push(message);
					}
					
					if(message.isJoinedMessage())
						System.out.println(messageData);
					
					if(message.isGameIsOnMessage()){ 
						positionMessages.push(message);
						this.latestPositionMessage = message;
						System.out.print(".");
					}
					
				}catch(BadInputMessageException e){
					logger.error("Failed to parse input message: " + messageData);
				}
			}
		}catch(Exception e){
			logger.error("Failed to read data from bot socket");
		}
		
		logger.info("Socket listening thread stopped");
	}

	@Override
	public SizedStack<InputMessage> getControlMessages() {
		return controlMessages;
	}

	@Override
	public SizedStack<InputMessage> getPositionMessages() {
		return positionMessages;
	}

	@Override
	public void shutdown() {
		this.socket.close();
	}

	@Override
	public void sendJoinMessage(String name){
		sendMessage("{\"msgType\":\"join\",\"data\":\""+name+"\"}");
	}
	
	@Override
	public void sendJoinMessage(String botname, String dueler) {
		// TODO: how to recover nicely
		if(!canMessageBeSent()){
			return;
		}
		
		sendMessage("{\"msgType\":\"requestDuel\",\"data\":[\""+botname+"\", \""+dueler+"\"]}");
	}

	@Override
	public boolean sendPaddleMovementMessage(float paddleDirection) {
		
		if(!canMessageBeSent()){
			logger.error("Exceeded the message limit");
			return false;
		}
		
		sendMessage("{\"msgType\":\"changeDir\",\"data\":"+Utils.toStringFormat(paddleDirection)+"}");
		return true;
	}
	
	private void sendMessage(String message){
		logger.debug("Sending message to socket: " + message);
		this.outputMessageTimestamps.add(System.currentTimeMillis());
		logToFile(message, true);
		socket.getOut().println(message);
	}

	
	public boolean canMessageBeSent(){
		long currentTimeStamp = System.currentTimeMillis();
		
		boolean loop = true;
		while(this.outputMessageTimestamps.size() != 0 && loop){
			long oldestTimeStamp = this.outputMessageTimestamps.get(0);
			if(currentTimeStamp - oldestTimeStamp > 1100){
				this.outputMessageTimestamps.remove(0);
			} else {
				loop = false;
			}
		}
		
		return this.outputMessageTimestamps.size() < 10;
	}
	
	
	
	
	
	
	/*public boolean messageStackAvailable(){
		long currentTimestamp = System.currentTimeMillis();
		if(this.outputMessageTimestamps.size() >= Constants.OUTPUT_MESSAGE_COUNT_LIMIT+1){
			long oldestTimestamp = this.outputMessageTimestamps.get(0);
			if(currentTimestamp - oldestTimestamp < (Constants.OUTPUT_MESSAGE_SPEED_LIMIT + Constants.OUTPUT_MESSAGE_SPEED_SAFE_FACTOR * Constants.OUTPUT_MESSAGE_COUNT_LIMIT) ){
				return false;
			}
		}
		return true;
	}
	
	
	
	private boolean recordMessageAndCheckExceed() {
		
		long currentTimestamp = System.currentTimeMillis();
		
		
		if(this.outputMessageTimestamps.size() >= Constants.OUTPUT_MESSAGE_COUNT_LIMIT){ // you can send the max. so use +1 here
			long oldestTimestamp = this.outputMessageTimestamps.get(0);
			if(currentTimestamp - oldestTimestamp < (Constants.OUTPUT_MESSAGE_SPEED_LIMIT + Constants.OUTPUT_MESSAGE_SPEED_SAFE_FACTOR * Constants.OUTPUT_MESSAGE_COUNT_LIMIT) ){
				logger.error("Exceeded the output message limit");
				//this.outputMessageTimestamps.remove(0);
				//System.exit(1);
				return true;
			}
			this.outputMessageTimestamps.add(currentTimestamp);
			this.outputMessageTimestamps.remove(0);
			
		}
	
		return false;
	}*/
	
	/*private boolean recordMessageAndCheckExceed() {
		
		long currentTimestamp = System.currentTimeMillis();
		this.outputMessageTimestamps.add(currentTimestamp);
		
		if(this.outputMessageTimestamps.size() >= Constants.OUTPUT_MESSAGE_COUNT_LIMIT+1){ // you can send the max. so use +1 here
			long oldestTimestamp = this.outputMessageTimestamps.get(0);
			if(currentTimestamp - oldestTimestamp < (Constants.OUTPUT_MESSAGE_SPEED_LIMIT + Constants.OUTPUT_MESSAGE_SPEED_SAFE_FACTOR * Constants.OUTPUT_MESSAGE_COUNT_LIMIT) ){
				logger.error("Exceeded the output message limit");
				this.outputMessageTimestamps.remove(0);
				//System.exit(1);
				return true;
			}
			this.outputMessageTimestamps.remove(0);
			
		}
	
		return false;
	}
	*/

	@Override
	public InputMessage peekLatestPositionMessage() {
		return this.latestPositionMessage;
	}


	@Override
	public InputMessage popLatestPositionMessage() {
		InputMessage toReturn = this.latestPositionMessage;
		this.latestPositionMessage = null;
		return toReturn;
	}
	
	
	public static void main(String[] args){
		
		float a = ((float)((int)1)) / ((int)60);
		System.out.println(a);
		
		
		SocketMessenger m = new SocketMessenger();
		
		
		for(int i = 0; i < 36; i++){
			
			//m.recordMessageAndCheckExceed();
			try{ Thread.sleep(200); } catch(Exception e){ }
			
		}
		
	}

	
	
	
}
