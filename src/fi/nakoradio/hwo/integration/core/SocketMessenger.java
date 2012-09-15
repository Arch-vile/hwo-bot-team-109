package fi.nakoradio.hwo.integration.core;

import java.io.BufferedReader;

import fi.nakoradio.hwo.util.SizedStack;

public class SocketMessenger  implements Messenger {

	private SizedStack<InputMessage> controlMessages;
	private SizedStack<InputMessage> positionMessages;
	private BotSocket socket;
	
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
				try {
					InputMessage message = new InputMessage(messageData);
					if(message.isGameOverMessage() || message.isGameStartedMessage() || message.isJoinedMessage()) controlMessages.push(message);
					if(message.isGameIsOnMessage()) positionMessages.push(message);
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
		socket.getOut().println("{\"msgType\":\"changeDir\",\"data\":"+paddleDirection+"}");
		
	}

	
	
	
	
}
