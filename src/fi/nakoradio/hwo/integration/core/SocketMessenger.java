package fi.nakoradio.hwo.integration.core;

import java.io.BufferedReader;

import fi.nakoradio.hwo.util.SizedStack;

public class SocketMessenger  implements Messenger {

	private SizedStack<InputMessage> controlMessages;
	private SizedStack<InputMessage> positionMessages;
	private InputMessage latestPositionMessage;
	private BotSocket socket;
	
	private long lastMoveMessageTimestamp;
	
	public SocketMessenger(String host, int port){
		this.socket = new BotSocket(host, port);
		this.controlMessages = new SizedStack<InputMessage>(50);
		this.positionMessages = new SizedStack<InputMessage>(50);
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
						System.out.println("Difference: " + (System.currentTimeMillis() - message.getTime()));
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
	// TODO: we need something much more clever both here and in the caller
	public void sendPaddleMovementMessage(float paddleDirection) {
		
		if(System.currentTimeMillis() - lastMoveMessageTimestamp > 100){
			String message = "{\"msgType\":\"changeDir\",\"data\":"+paddleDirection+"}";
			socket.getOut().println(message);
			System.out.println(message);
			lastMoveMessageTimestamp = System.currentTimeMillis();
		}
		
		
		
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
	
	
}
