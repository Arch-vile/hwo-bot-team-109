package fi.nakoradio.hwo.main;

import java.util.Random;

import org.apache.log4j.Logger;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import fi.nakoradio.hwo.ai.EvenSimplerPaddleMover;
import fi.nakoradio.hwo.ai.Nostradamus;
import fi.nakoradio.hwo.ai.PaddleMover;
import fi.nakoradio.hwo.ai.ServerClone;
import fi.nakoradio.hwo.ai.ServerCloneSynchronizer;
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
	
	private static Logger logger = Logger.getLogger(HWOBot.class);
	
	private Messenger messenger;
	public static GameVisualizer visualizer;
	private String botname;
	private String dueler;

	public static Boolean visualize = false;
	
	public static long firstPosMessageTime = 0;
	
	public HWOBot(String botname, String host, Integer port, String dueler, Boolean visualize){
		this.botname = botname;
		this.dueler = dueler;
		HWOBot.visualize = visualize;
		
		logger.info("Creating new HWOBot");
		this.messenger = new SocketMessenger(host, port);
		//this.messenger = new SimulatedMessenger();
		if(visualize){
			HWOBot.visualizer = new GameVisualizer();
			HWOBot.visualizer.start();
			try { Thread.sleep(1500); } catch(Exception e){} // This is to make sure visualizer is ready
		}
		
	}
	

	
	
	public void start(){
		
		logger.info("Joining to server");
		messenger.start();
		if(dueler != null) messenger.sendJoinMessage(botname, dueler);
		else messenger.sendJoinMessage(botname);
		
		
		while(runGame()){
			logger.info("Game ended starting new one");
			try { Thread.sleep(1); } catch(Exception e){ logger.error("Sleep error while running game");} 
			
		}
		
		messenger.shutdown();
	}

	private boolean runGame(){
		

		while(messenger.peekLatestPositionMessage() == null) { try { Thread.sleep(10); } catch(Exception e){} }
		Blueprint blueprint = new Blueprint(messenger.popLatestPositionMessage().getStateInTime());
		ServerClone serverClone = new ServerClone(blueprint);
		Nostradamus nostradamus = new Nostradamus(serverClone);
		ServerCloneSynchronizer synchronizer = new ServerCloneSynchronizer(serverClone);
		PaddleMover paddleMover = new PaddleMover(serverClone, messenger, synchronizer);
		
		// There is a crashing bug in the box2d... lets try to recover
		try{
			logger.info("Waiting for first status message");
			
			HWOBot.firstPosMessageTime = blueprint.getTimestamp();
			
			paddleMover.start();
			synchronizer.start();
			
			logger.info("Starting the main loop");
			Vec2[] deathPoints = new Vec2[2];
			boolean updateFromServer = true;
			boolean running = true;
			long loopStartTime = System.currentTimeMillis();
			long lastTargetCalcTime = 0;
			while(running){
				
				// TODO: remove
				if(System.currentTimeMillis() - loopStartTime > 5000000)
					updateFromServer = false;
				
				logger.debug("Main loop - START");
				try { Thread.sleep(10); } catch(Exception e){ logger.error("Error in main program sleep");}
				
				logger.debug("Main loop checking control messages - START");
				while(updateFromServer && !messenger.getControlMessages().empty()){
					InputMessage m = messenger.getControlMessages().pop();
					if(m.isGameOverMessage()){
						running = false;
						break;
					}
				}
				if(!running) break;
				
				logger.debug("Main loop checking position messages - START");
				while(updateFromServer && messenger.peekLatestPositionMessage() != null){
					InputMessage positionMessage = messenger.popLatestPositionMessage();
					blueprint = new Blueprint(positionMessage.getStateInTime());
					
					logger.debug("\nmypaddle: paddle position from server at ["+(blueprint.getTimestamp()-HWOBot.firstPosMessageTime)+"] is ["+blueprint.getMyPaddle().getPosition()+"]");
					
					serverClone.update(blueprint);
					synchronizer.serverCloneUpdated();
					
					//if(visualize) visualizer.getWorld().getMarker2().setTransform( new Vec2(blueprint.getBall().getPosition()), 0f);
				}
				
	
				// Otherwise we are flooding the paddle mover with new targets as there is variation motion calculation
				if(serverClone.bounced() || System.currentTimeMillis()-lastTargetCalcTime  > 100){
					lastTargetCalcTime = System.currentTimeMillis();
					logger.debug("Main loop getting death points - START");
					deathPoints = nostradamus.getNextDeathPoints(2);
					logger.trace("DEATH: " + deathPoints[0]  );
					paddleMover.setTargets(deathPoints);
	
				}
				
				
				logger.debug("Main loop draw - START");
				if(HWOBot.visualize){
					HWOBot.visualizer.plotDeathPoints(deathPoints);
					HWOBot.visualizer.update(serverClone.getSimulation().getCurrentState());
				}
				
				logger.debug("Main loop - END");
			}
		}catch(Exception e){
			logger.error("Box2d crash" + e);
			logger.error(e);
		}
		
		paddleMover.shutdown();
		synchronizer.shutdown();
		return true;
	}


	
	public static void main(String[] args){
		System.out.println("./start.sh <name> <host> <port>");
		logger.info("HWOBot called with arguments: ");
		for(String argument : args) logger.info(argument);
		
		
		if(args.length < 3){
			System.out.println("You need to give name, host and port as arguments");
			System.exit(1);
		}
		
		String botname = args[0];
		String host = args[1];
		Integer port = new Integer(args[2]);
		String dueler = null;
		Boolean visualize = false;
		if(args.length >= 5) dueler = args[4];
		if(args.length >= 4) visualize = new Boolean(args[3]);
		
		HWOBot bot = new HWOBot(botname, host, port, dueler, visualize);
		bot.start();
	}
	
	
}
