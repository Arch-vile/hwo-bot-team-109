package fi.nakoradio.hwo.main;

import java.util.Random;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import fi.nakoradio.hwo.integration.core.BadInputMessageException;
import fi.nakoradio.hwo.integration.core.BotSocket;
import fi.nakoradio.hwo.integration.core.InputMessage;
import fi.nakoradio.hwo.integration.core.MessageSender;
import fi.nakoradio.hwo.integration.core.StackedMessageReader;
import fi.nakoradio.hwo.model.objects.SimpleWorld;
import fi.nakoradio.hwo.physics.CollisionListener;
import fi.nakoradio.hwo.physics.PhysicsWorld;
import fi.nakoradio.hwo.physics.visualization.Box2dTest;
import fi.nakoradio.hwo.physics.visualization.Box2dTestbed;

public class HWOBot {

	public static void main(String[] args){
		
		String botname = args[0];
		String host = args[1];
		String port = args[2];
		
		//BotSocket newSocket = new BotSocket(host, new Integer(port));
		//StackedMessageReader messageReader = StackedMessageReader.startReading(50, newSocket);
		
		
		
		Random random = new Random();
		long lastTimestamp = System.currentTimeMillis();
		int playCount = 20;
		float paddleDirection = 0;
		boolean running = true;
		
		SimpleWorld simpleWorld = new SimpleWorld();
		Box2dTest test = new Box2dTest();
		
		
		Box2dTestbed tester = new Box2dTestbed();
		tester.startSimulation(test);
		
		World testWorld = test.getInitializedWorld();
		testWorld.setGravity(new Vec2(0,0));
		PhysicsWorld world = new PhysicsWorld(testWorld);
		test.setCamera(new Vec2(291.4259f,183.91339f), 0.46997482f);
		
		try {
			String initState = "{\"msgType\":\"gameIsOn\",\"data\":{\"time\":1347651768367,\"left\":{\"y\":240.0,\"playerName\":\"randombot\"},\"right\":{\"y\":240.0,\"playerName\":\"becker\"},\"ball\":{\"pos\":{\"x\":571.0496379848345,\"y\":159.49839994531766}},\"conf\":{\"maxWidth\":640,\"maxHeight\":480,\"paddleHeight\":50,\"paddleWidth\":10,\"ballRadius\":5,\"tickInterval\":30}}}";
			InputMessage message = new InputMessage(initState);
			simpleWorld.update(message);
			world.update(simpleWorld);
			
		}catch (BadInputMessageException e){
			System.err.println(e);
		}
		
		
		World calcWorld = new World(new Vec2(0,0), true);
		PhysicsWorld calcContainer = new PhysicsWorld(calcWorld);
		calcWorld.setContactListener(new CollisionListener(calcContainer));
		
		calcContainer.update(simpleWorld);
		
		while(1<2){
			calcWorld.step(1f/60f, 10, 8);
			try { Thread.sleep(1000/60); }catch(Exception e){ }
		}
		
		
		
		
		
		
		
		
		//MessageSender messageSender = new MessageSender(newSocket);
		//messageSender.sendJoinMessage(botname);
		
		
		
		/*while(running){
			
			try { Thread.sleep(20); } catch(Exception e){}
			
			if(!messageReader.getPositionMessages().empty()){
				InputMessage positionMessage = messageReader.getPositionMessages().pop();
				simpleWorld.update(positionMessage);
				world.update(simpleWorld);
				System.out.println(positionMessage.getMessage());
				running = false;
			}
			
			
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
		*/
	}
	
	
}
