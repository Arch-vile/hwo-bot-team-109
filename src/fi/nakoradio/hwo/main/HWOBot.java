package fi.nakoradio.hwo.main;

import java.util.Random;

import fi.nakoradio.hwo.integration.core.BotSocket;
import fi.nakoradio.hwo.integration.core.InputMessage;
import fi.nakoradio.hwo.integration.core.MessageSender;
import fi.nakoradio.hwo.integration.core.StackedMessageReader;

public class HWOBot {

	public static void main(String[] args){
		
		BotSocket newSocket = new BotSocket("boris.helloworldopen.fi", 9090);
		StackedMessageReader messageReader = StackedMessageReader.startReading(50, newSocket);
		
		MessageSender messageSender = new MessageSender(newSocket);
		messageSender.sendJoinMessage();
		
		Random random = new Random();
		long lastTimestamp = System.currentTimeMillis();
		int playCount = 30;
		float paddleDirection = 0;
		boolean running = true;
		while(running){
			
			if(!messageReader.getControlMessages().empty()){
				InputMessage controlMessage = messageReader.getControlMessages().pop();
				
				System.out.println(controlMessage.getMessage());
				
				if(controlMessage.isGameOverMessage()){
					playCount--;
					if(playCount <= 0)
						running = false;
				}
			}
			
			if(System.currentTimeMillis()-lastTimestamp >= 1500){
				lastTimestamp = System.currentTimeMillis();
				if(random.nextInt(100) < 50)
					paddleDirection = 1;
				else
					paddleDirection = -1;
				System.out.println("Sending paddle move message: " + paddleDirection);
				messageSender.sendPaddleMovementMessage(paddleDirection);
				
			}
			
		}
		
		newSocket.close();
		
	}
	
	
}
