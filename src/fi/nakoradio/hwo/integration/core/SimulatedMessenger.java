package fi.nakoradio.hwo.integration.core;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import fi.nakoradio.hwo.model.objects.Blueprint;
import fi.nakoradio.hwo.physics.PhysicsWorld;
import fi.nakoradio.hwo.util.SizedStack;

public class SimulatedMessenger implements Messenger {

	private SizedStack<InputMessage> controlMessages;
	private SizedStack<InputMessage> positionMessages;
	private InputMessage latestPositionMessage;
	PhysicsWorld simulation;
	
	private boolean running = false;
	private Thread thread;
	
	public SimulatedMessenger(){ //y:159
		String initState = "{\"msgType\":\"gameIsOn\",\"data\":{\"time\":1347651768367,\"left\":{\"y\":240.0,\"playerName\":\"randombot\"},\"right\":{\"y\":240.0,\"playerName\":\"becker\"},\"ball\":{\"pos\":{\"x\":200.0,\"y\":20.4}},\"conf\":{\"maxWidth\":640,\"maxHeight\":480,\"paddleHeight\":50,\"paddleWidth\":10,\"ballRadius\":5,\"tickInterval\":30}}}";
		InputMessage initStateMessage = null;
		try {
			initStateMessage = new InputMessage(initState);
			Blueprint blueprint = new Blueprint(initStateMessage.getStateInTime());
			this.simulation = new PhysicsWorld(new World(new Vec2(0,0), true), blueprint);
			this.simulation.getBall().setLinearVelocity(new Vec2(40,40));
			
			this.controlMessages = new SizedStack<InputMessage>(50);
			this.positionMessages = new SizedStack<InputMessage>(50);
		} catch (BadInputMessageException e1) {
			System.err.println("Failed to create init state for message simulation");
			e1.printStackTrace();
		}
	}
	
	
	@Override
	public void run() {
		this.running = true;
		
		long timeStamp = System.currentTimeMillis();
		long latency = 20;
		float dt = 1f/60f;
		float sumt = 0;
		float tickTime = 30f/1000f;
		try{
		
			while(!Thread.interrupted() && this.running){
				
				
				Thread.sleep((long)(tickTime*1000));
				while(sumt < tickTime){
					simulation.getPhysics().step(dt, 10, 8);
					sumt += dt;
				}
				sumt -= tickTime;
				
				
				
				// Lets simulate variable latency
				if(System.currentTimeMillis()-timeStamp > latency){
					timeStamp = System.currentTimeMillis();
					
					int random = (int)(Math.random()*1000);
					if(random >= 0 && random < 700) latency = 20;
					else if(random >= 700 && random < 980) latency = 300;
					else if(random >= 980 && random < 992) latency = 1000;
					else if(random >= 992 && random < 1000) latency = 5000;
					else latency = 20;
					latency = 1;
					
					String messageData = this.getStateAsJSON();
					try {
						InputMessage message = new InputMessage(messageData);
						this.positionMessages.push(message);
						this.latestPositionMessage = message;
					}catch (BadInputMessageException e){
						System.err.println("Failed to parse input message. " + e + " - " + e.getCause());
					}
				}
				
			}
			
		}catch(InterruptedException e){
			System.err.println("SimulatedMessenger thread interrupted: " + e);
		}
		

	}



	private String getStateAsJSON() {
		long timeStamp = System.currentTimeMillis();
		
		// Values from the simulation. These change as simulation runs.
		float leftPadY = this.getSimulation().getMyPaddle().getPosition().y;
		float rightPadY = this.getSimulation().getOpponentPaddle().getPosition().y;
		float ballx = this.getSimulation().getBall().getPosition().x;
		float bally = this.getSimulation().getBall().getPosition().y;
		
		// Values from blueprint. These should be constants.
		float arenaWidth = this.getSimulation().getBlueprint().getArena().getWidth();
		float arenaHeight = this.getSimulation().getBlueprint().getArena().getHeight();
		float paddleHeight = this.simulation.getBlueprint().getMyPaddle().getHeight();
		float paddleWidth = this.simulation.getBlueprint().getMyPaddle().getWidth();
		float ballRadius = this.simulation.getBlueprint().getBall().getRadius();
		long tickInterval = this.simulation.getBlueprint().getTickInterval();
		
		String state = "{\"msgType\":\"gameIsOn\",\"data\":{\"time\":"+timeStamp+",\"left\":{\"y\":"+leftPadY+",\"playerName\":\"randombot\"},\"right\":{\"y\":"+rightPadY+",\"playerName\":\"becker\"},\"ball\":{\"pos\":{\"x\":"+ballx+",\"y\":"+bally+"}},\"conf\":{\"maxWidth\":"+arenaWidth+",\"maxHeight\":"+arenaHeight+",\"paddleHeight\":"+paddleHeight+",\"paddleWidth\":"+paddleWidth+",\"ballRadius\":"+ballRadius+",\"tickInterval\":"+tickInterval+"}}}";
		
		return state;
	}



	@Override
	public void start() {
		this.thread = new Thread(this);
		this.thread.start();
	}

	@Override
	public void shutdown() {
		this.running = false;
		this.thread.interrupt();
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
	public void sendJoinMessage(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean sendPaddleMovementMessage(float paddleDirection) {
		// TODO Auto-generated method stub
		return true; 	
	}



	public PhysicsWorld getSimulation() {
		return simulation;
	}


	@Override
	public InputMessage peekLatestPositionMessage() {
		return this.latestPositionMessage;
	}


	@Override
	public InputMessage popLatestPositionMessage() {
		InputMessage toReturn = this.latestPositionMessage;
		this.latestPositionMessage = null;
		return toReturn;
	}


	@Override
	public void sendJoinMessage(String botname, String dueler) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public boolean canMessageBeSent() {
		// TODO Auto-generated method stub
		return true;
	}


	
	
	

}
