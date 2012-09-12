package fi.nakoradio.hwo.integration.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Stack;

import fi.nakoradio.hwo.util.SizedStack;

public class StackedMessageReader  implements Runnable{

	private SizedStack<InputMessage> controlMessages;
	private SizedStack<InputMessage> positionMessages;
	private BotSocket socket;
	
	private StackedMessageReader(BotSocket socket){
		this.socket = socket;
		this.controlMessages = new SizedStack<InputMessage>(50);
		this.positionMessages = new SizedStack<InputMessage>(50);
	}
	
	public static StackedMessageReader startReading(int size, BotSocket socket){
		StackedMessageReader reader = new StackedMessageReader(socket);
		(new Thread(reader)).start();
		return reader;
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

	public SizedStack<InputMessage> getControlMessages() {
		return controlMessages;
	}

	public SizedStack<InputMessage> getPositionMessages() {
		return positionMessages;
	}
	
	
	
}
