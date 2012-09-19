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
		
		Messenger messenger = new SocketMessenger(host, new Integer(port));
	//	Messenger messenger = new SimulatedMessenger();
		messenger.start();
		//DeathPointListener list = new DeathPointListener(((SimulatedMessenger)messenger).getSimulation(), "Simulation");
		
		
		boolean running = true;
		
		//GameVisualizer visualizer = new GameVisualizer();
		//visualizer.start();
		GameVisualizer visualizer2 = new GameVisualizer();
		visualizer2.start();
		try { Thread.sleep(500); } catch(Exception e){}
		
		messenger.sendJoinMessage(botname);
		//PaddleMover paddleMover = new PaddleMover(messenger);
		
		System.out.println("Waiting first position message");
		// Wait for the first position message
		// TODO: should propably be done inside the loop to make sure we start immediately in correct sync with the server
		while(messenger.peekLatestPositionMessage() == null) { try { Thread.sleep(10); } catch(Exception e){} }
		System.out.println(messenger.peekLatestPositionMessage().getMessage());
		messenger.sendPaddleMovementMessage(-1);
		
		
		// Set up the initial situation
		Blueprint blueprint = new Blueprint(messenger.popLatestPositionMessage().getStateInTime());
		ServerClone serverClone = new ServerClone(blueprint);
		Nostradamus nostradamus = new Nostradamus(serverClone);
		
		PaddleMover paddleMover = new PaddleMover(serverClone, messenger);
		//paddleMover.start();
		
		
		//visualizer.update(blueprint);
		visualizer2.update(blueprint);
		
		long lastTimestamp = System.currentTimeMillis();
		int counter = 0;
		
		
		float t = 0;
		
		float tickInterval = ((float)blueprint.getTickInterval())/1000f;
		System.out.println("Starting main loop");
		boolean setPhantomToBall = true;
		while(running){
			
			try { Thread.sleep(50); } catch(Exception e){ System.err.println("Error in main program sleep");}
			
			
			if(serverClone.getSimulation().getBall().getPosition().x > 600){
				setPhantomToBall = false;
			}
			
			if(serverClone.getSimulation().getBall().getPosition().x < 50){
				setPhantomToBall = true;
			}
			
			
			while(!messenger.getControlMessages().empty()){
				InputMessage m = messenger.getControlMessages().pop();
				if(m.isGameOverMessage()){
					setPhantomToBall = true;
					lastTimestamp = System.currentTimeMillis();
				}
			}
			
			while(messenger.peekLatestPositionMessage() != null){
				InputMessage positionMessage = messenger.popLatestPositionMessage();
				blueprint = new Blueprint(positionMessage.getStateInTime());
				
				if(setPhantomToBall)
					blueprint.getPhantom().setPosition(new Vec2(blueprint.getBall().getPosition().x, blueprint.getBall().getPosition().y+0));
					
				serverClone.update(blueprint, setPhantomToBall);
			}
			
			
			/*Vec2 deathPoint = nostradamus.getNextDeathPoint();
			
			if(deathPoint != null){
				paddleMover.setTargets(deathPoint.y, 0);
			}*/
			
			// TODO: do we do this even if the state is updated by message? try without
			//serverClone.forward(blueprint.getTickInterval());
			
			serverClone.advanceToPresentTime();
			visualizer2.update(serverClone.getSimulation().getCurrentState());
			
		}
		
		messenger.shutdown();
		
	}
	
	
}
