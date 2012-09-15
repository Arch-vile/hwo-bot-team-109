package fi.nakoradio.hwo.main;

import java.util.Random;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

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

public class HWOBot {

	public static void main(String[] args){
		
		String botname = args[0];
		String host = args[1];
		String port = args[2];
		
		//Messenger messenger = new SocketMessenger(host, new Integer(port));
		String initState = "{\"msgType\":\"gameIsOn\",\"data\":{\"time\":1347651768367,\"left\":{\"y\":240.0,\"playerName\":\"randombot\"},\"right\":{\"y\":240.0,\"playerName\":\"becker\"},\"ball\":{\"pos\":{\"x\":571.0496379848345,\"y\":159.49839994531766}},\"conf\":{\"maxWidth\":640,\"maxHeight\":480,\"paddleHeight\":50,\"paddleWidth\":10,\"ballRadius\":5,\"tickInterval\":30}}}";
		InputMessage initStateMessage = null;
		try {
			initStateMessage = new InputMessage(initState);
		} catch (BadInputMessageException e1) {
			System.err.println("Failed to create init state for message simulation");
			e1.printStackTrace();
		}
		Messenger messenger = new SimulatedMessenger(initStateMessage);
		messenger.start();
		
		
		
		Random random = new Random();
		long lastTimestamp = System.currentTimeMillis();
		int playCount = 20;
		float paddleDirection = 0;
		boolean running = true;
		
		Blueprint blueprint = new Blueprint();
		Box2dTest test = new Box2dTest();
		
		
		Box2dTestbed tester = new Box2dTestbed();
		tester.startSimulation(test);
		
		World testWorld = test.getInitializedWorld();
		testWorld.setGravity(new Vec2(0,0));
		PhysicsWorld world = new PhysicsWorld(testWorld);
		CollisionListener collisions = new CollisionListener(world);
		testWorld.setContactListener(collisions);
		test.setCamera(new Vec2(291.4259f,183.91339f), 0.46997482f);
		
		messenger.sendJoinMessage(botname);
		
		while(running){
			
			try { Thread.sleep(20); } catch(Exception e){}
			
			if(!messenger.getPositionMessages().empty()){
				InputMessage positionMessage = messenger.getPositionMessages().pop();
				blueprint.update(positionMessage);
				world.update(blueprint);
				//System.out.println("--" + positionMessage.getMessage());
			}
			
			
			if(!messenger.getControlMessages().empty()){
				InputMessage controlMessage = messenger.getControlMessages().pop();
				
				//System.out.println("**" + controlMessage.getMessage());
				
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
				messenger.sendPaddleMovementMessage(paddleDirection);
				
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
