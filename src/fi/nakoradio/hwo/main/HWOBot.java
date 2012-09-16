package fi.nakoradio.hwo.main;

import java.util.Random;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import fi.nakoradio.hwo.ai.Nostradamus;
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
		try { Thread.sleep(500); } catch(Exception e){}
		
		messenger.sendJoinMessage(botname);
		
		
		// Wait for the first position message
		while(messenger.getPositionMessages().empty()) { try { Thread.sleep(10); } catch(Exception e){} }
		Blueprint blueprint = new Blueprint(messenger.getPositionMessages().peek().getStateInTime());
		Nostradamus nostradamus = new Nostradamus(blueprint);
		visualizer.update(blueprint);
		
		
		long lastTimestamp = System.currentTimeMillis();
		while(running){
			
			try { Thread.sleep(20); } catch(Exception e){}
			
			if(!messenger.getPositionMessages().empty()){
				InputMessage positionMessage = messenger.getPositionMessages().peek();
				blueprint.update(positionMessage.getStateInTime());
				visualizer.update(blueprint);
				
				//System.out.println("--" + positionMessage.getMessage());
			}
			
			
			if(!messenger.getControlMessages().empty()){
				InputMessage controlMessage = messenger.getControlMessages().peek();
				
				//System.out.println("**" + controlMessage.getMessage());
				
				if(controlMessage.isGameOverMessage()){
					playCount--;
					if(playCount <= 0)
						running = false;
				}
			}
			
			if(System.currentTimeMillis() - lastTimestamp > 2000){
				if(nostradamus == null) nostradamus = new Nostradamus(blueprint);
				lastTimestamp = System.currentTimeMillis();
				nostradamus.update(blueprint); 
				
			//	System.out.println("Nostradamus is predicting deathpoints");
				Vec2 firstPoint = nostradamus.getNextDeathPoint();
				Vec2 secondPoint = nostradamus.getNextDeathPoint();
				System.out.println("And the prophesy is first hit " + firstPoint + " and then " + secondPoint);
				PhysicsUtil.alterY(((SimulatedMessenger)messenger).getSimulation().getMyPaddle(),firstPoint);
			}
			
		}
		
		messenger.shutdown();
		
	}
	
	
}
