package fi.nakoradio.hwo.integration.core;

import java.io.BufferedReader;
import java.util.Vector;

import fi.nakoradio.hwo.physics.Constants;
import fi.nakoradio.hwo.util.SizedStack;

public class SocketMessenger  implements Messenger {

	private SizedStack<InputMessage> controlMessages;
	private SizedStack<InputMessage> positionMessages;
	private InputMessage latestPositionMessage;
	private BotSocket socket;
	
	private long lastMoveMessageTimestamp;
	
	private Vector<Long> outputMessageTimestamps;
	
	public SocketMessenger(){
		this.outputMessageTimestamps = new Vector<Long>();
	}
	
	public SocketMessenger(String host, int port){
		this.socket = new BotSocket(host, port);
		this.controlMessages = new SizedStack<InputMessage>(50);
		this.positionMessages = new SizedStack<InputMessage>(50);
		this.outputMessageTimestamps = new Vector<Long>();
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
				long timestamp = System.currentTimeMillis();
				try {
					InputMessage message = new InputMessage(messageData);
					if(message.isGameOverMessage() || message.isGameStartedMessage() || message.isJoinedMessage()) controlMessages.push(message);
					if(message.isGameIsOnMessage()){ 
						positionMessages.push(message);
						this.latestPositionMessage = message;
						//System.out.println("Difference: " + (System.currentTimeMillis() - message.getTime()));
					}
					
				}catch(BadInputMessageException e){
					System.err.println("Failed to parse input message: " + messageData);
				}
			}
		}catch(Exception e){
			System.err.println("Failed to read data from bot socket");
		}
		
		System.out.println("Socket listening thread stopped");
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
		socket.getOut().println("{\"msgType\":\"join\",\"data\":\""+name+"\"}");
	}

	@Override
	public void sendPaddleMovementMessage(float paddleDirection) {
		
		// TODO: how to recover nicely
		if(recordMessageAndCheckExceed()){
			return;
		}
		
		
		
		
		
	}

	
	private boolean recordMessageAndCheckExceed() {
		
		long currentTimestamp = System.currentTimeMillis();
		this.outputMessageTimestamps.add(currentTimestamp);
		
		if(this.outputMessageTimestamps.size() >= Constants.OUTPUT_MESSAGE_COUNT_LIMIT+1){ // you can send the max. so use +1 here
			long oldestTimestamp = this.outputMessageTimestamps.get(0);
			if(currentTimestamp - oldestTimestamp < (Constants.OUTPUT_MESSAGE_SPEED_LIMIT + Constants.OUTPUT_MESSAGE_SPEED_SAFE_FACTOR * Constants.OUTPUT_MESSAGE_COUNT_LIMIT) ){
				System.err.println("Exceeded the output message limit");
				System.exit(1);
				// return true;
			}
			this.outputMessageTimestamps.remove(0);
			
		}
	
		return false;
	}

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
			
			m.recordMessageAndCheckExceed();
			try{ Thread.sleep(109); } catch(Exception e){ }
			
		}
		
	}
	
	
}
