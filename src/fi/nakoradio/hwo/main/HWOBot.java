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
import fi.nakoradio.hwo.physics.CollisionListener;
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
		
		// TODO: NOTE: Printing death point at simulation and at nostradamus do not match exactly... is it something to worry about?
		// version showing this error is tagged in git as "deathpointMismatch". it opens two simulations windows
		CollisionListener collisions = new CollisionListener(((SimulatedMessenger)messenger).getSimulation(),"Simulation");
		((SimulatedMessenger)messenger).getSimulation().getPhysics().setContactListener(collisions);
		
		
		int playCount = 20;
		boolean running = true;
		
		Blueprint blueprint = new Blueprint();
		
		
		GameVisualizer visualizer = new GameVisualizer();
		visualizer.start();
		
		GameVisualizer visualizer2 = new GameVisualizer();
		visualizer2.start();
		
		Nostradamus nostradamus = new Nostradamus();
		
		messenger.sendJoinMessage(botname);
		
		long lastTimestamp = System.currentTimeMillis();
		
		int counter = 0;
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
			
			if(nostradamus.getWorld().getBlueprint() != null){
				visualizer2.update(nostradamus.getWorld().getCurrentState());
				nostradamus.getWorld().getPhysics().step(1f/60f, 10, 8);
			}
			
			if(System.currentTimeMillis() - lastTimestamp > 1500){
				lastTimestamp = System.currentTimeMillis()+500000;
				
				nostradamus.update(blueprint);
				
				//Vec2 deathPoint = nostradamus.getNextDeathPoint();
				//((SimulatedMessenger)messenger).getSimulation().getMyPaddle().setTransform(deathPoint, 0);
			}
			
		}
		
		messenger.shutdown();
		
		
		
		
		/*
		 * 
		 * 
		 * try {
			String initState = "{\"msgType\":\"gameIsOn\",\"data\":{\"time\":1347651768367,\"left\":{\"y\":240.0,\"playerName\":\"randombot\"},\"right\":{\"y\":240.0,\"playerName\":\"becker\"},\"ball\":{\"pos\":{\"x\":571.0496379848345,\"y\":159.49839994531766}},\"conf\":{\"maxWidth\":640,\"maxHeight\":480,\"paddleHeight\":50,\"paddleWidth\":10,\"ballRadius\":5,\"tickInterval\":30}}}";
			InputMessage message = new InputMessage(initState);
			simpleWorld.update(message);
			world.update(simpleWorld);
			
		}catch (BadInputMessageException e){
			System.err.println(e);
		}
		
		
		World calcWorld = new World(new Vec2(0,0), true);
		PhysicsWorld calcContainer = new PhysicsWorld(calcWorld);
		CollisionListener collisions = new CollisionListener(calcContainer);
		calcWorld.setContactListener(collisions);
		
		calcContainer.update(simpleWorld);
	
		while(collisions.getDeathPoint() == null)
			calcWorld.step(1f/60f, 10, 8);
		
		Vec2 deathPoint = collisions.popDeathPoint();
		world.getMyPaddle().setTransform(deathPoint, 0);
		
		try { Thread.sleep(1000/60); }catch(Exception e){ }
		*/
		 
	}
	
	
}
