package fi.nakoradio.hwo.main;

import java.util.Random;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import fi.nakoradio.hwo.ai.Nostradamus;
import fi.nakoradio.hwo.ai.PaddleMover;
import fi.nakoradio.hwo.ai.ServerClone;
import fi.nakoradio.hwo.integration.core.BadInputMessageException;
import fi.nakoradio.hwo.integration.core.BotSocket;
import fi.nakoradio.hwo.integration.core.InputMessage;
import fi.nakoradio.hwo.integration.core.Messenger;
import fi.nakoradio.hwo.integration.core.SimulatedMessenger;
import fi.nakoradio.hwo.integration.core.SocketMessenger;
import fi.nakoradio.hwo.model.objects.Blueprint;
import fi.nakoradio.hwo.physics.DeathPointListener;
import fi.nakoradio.hwo.physics.PhysicsUtil;
import fi.nakoradio.hwo.physics.PhysicsWorld;
import fi.nakoradio.hwo.physics.visualization.Box2dTest;
import fi.nakoradio.hwo.physics.visualization.Box2dTestbed;
import fi.nakoradio.hwo.physics.visualization.GameVisualizer;

public class HWOBot {

	public static void main(String[] args){
		
		String botname = args[0];
		String host = args[1];
		String port = args[2];
		
		//Messenger messenger = new SocketMessenger(host, new Integer(port));
		Messenger messenger = new SimulatedMessenger();
		messenger.start();
		//DeathPointListener list = new DeathPointListener(((SimulatedMessenger)messenger).getSimulation(), "Simulation");
		
		
		int playCount = 20;
		boolean running = true;
		
		GameVisualizer visualizer = new GameVisualizer();
		visualizer.start();
		GameVisualizer visualizer2 = new GameVisualizer();
		visualizer2.start();
		try { Thread.sleep(500); } catch(Exception e){}
		
		messenger.sendJoinMessage(botname);
		//PaddleMover paddleMover = new PaddleMover(messenger);
		
		
		// Wait for the first position message
		while(messenger.peekLatestPositionMessage() == null) { try { Thread.sleep(10); } catch(Exception e){} }
		
		// Set up the initial situation
		Blueprint blueprint = new Blueprint(messenger.peekLatestPositionMessage().getStateInTime());
		//Nostradamus nostradamus = new Nostradamus(blueprint);
		ServerClone serverClone = new ServerClone(blueprint);
		
		visualizer.update(blueprint);
		visualizer2.update(blueprint);
		
		long lastTimestamp = System.currentTimeMillis();
		int counter = 0;
		while(running){
			
			try { Thread.sleep(20); } catch(Exception e){ System.err.println("Error in main program sleep");}
			
			if(messenger.peekLatestPositionMessage() != null){
				InputMessage positionMessage = messenger.popLatestPositionMessage();
				blueprint = new Blueprint(positionMessage.getStateInTime());
				visualizer.update(blueprint);
				
				serverClone.update(blueprint);
			}
			
			/*long forward = System.currentTimeMillis() - lastTimestamp;
			lastTimestamp = System.currentTimeMillis();
			serverClone.forward((int)(forward*serverClone.getSpeedMultiplier()));
			*/
			serverClone.forwardToPresent();
			
			visualizer2.update(serverClone.getSimulation().getCurrentState());
			
			/*if(System.currentTimeMillis() - lastTimestamp > 2000000){
				lastTimestamp = System.currentTimeMillis();
				nostradamus.update(blueprint); 
				
			//	System.out.println("Nostradamus is predicting deathpoints");
				Vec2 firstPoint = nostradamus.getNextDeathPoint();
				Vec2 secondPoint = nostradamus.getNextDeathPoint();
				System.out.println("And the prophesy is first hit " + firstPoint + " and then " + secondPoint);
				
			//	paddleMover.move(firstPoint);
				
				// This is just cheating ;)
				PhysicsUtil.alterY(((SimulatedMessenger)messenger).getSimulation().getMyPaddle(),firstPoint);
			}*/
			
		}
		
		messenger.shutdown();
		
	}
	
	
}
