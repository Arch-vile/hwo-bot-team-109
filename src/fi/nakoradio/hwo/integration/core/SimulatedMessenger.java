package fi.nakoradio.hwo.integration.core;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import fi.nakoradio.hwo.model.objects.Blueprint;
import fi.nakoradio.hwo.physics.PhysicsWorld;
import fi.nakoradio.hwo.util.SizedStack;

public class SimulatedMessenger implements Messenger {

	private SizedStack<InputMessage> controlMessages;
	private SizedStack<InputMessage> positionMessages;
	PhysicsWorld simulation;
	
	private boolean running = true;
	private Thread thread;
	
	public SimulatedMessenger(InputMessage initState){
		Blueprint blueprint = new Blueprint(initState);
		this.simulation = new PhysicsWorld(new World(new Vec2(0,0), true), blueprint);
		this.controlMessages = new SizedStack<InputMessage>(50);
		this.positionMessages = new SizedStack<InputMessage>(50);
	}
	
	
	@Override
	public void run() {
		
		try{
		
			while(!Thread.interrupted() && this.running){
				
				simulation.getWorld().step(1f/60f, 10, 8);
				Thread.sleep(1000/60);
				
				String messageData = this.getStateAsJSON();
				try {
					InputMessage message = new InputMessage(messageData);
					this.positionMessages.push(message);
				}catch (BadInputMessageException e){
					System.err.println("Failed to parse input message. " + e + " - " + e.getCause());
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
	public void sendPaddleMovementMessage(float paddleDirection) {
		// TODO Auto-generated method stub

	}



	public PhysicsWorld getSimulation() {
		return simulation;
	}
	
	

}
