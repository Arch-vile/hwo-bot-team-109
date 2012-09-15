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
	
	public SimulatedMessenger(){
		String initState = "{\"msgType\":\"gameIsOn\",\"data\":{\"time\":1347651768367,\"left\":{\"y\":240.0,\"playerName\":\"randombot\"},\"right\":{\"y\":240.0,\"playerName\":\"becker\"},\"ball\":{\"pos\":{\"x\":571.0496379848345,\"y\":159.49839994531766}},\"conf\":{\"maxWidth\":640,\"maxHeight\":480,\"paddleHeight\":50,\"paddleWidth\":10,\"ballRadius\":5,\"tickInterval\":30}}}";
		InputMessage initStateMessage = null;
		try {
			initStateMessage = new InputMessage(initState);
			Blueprint blueprint = new Blueprint(initStateMessage.getStateInTime());
			this.simulation = new PhysicsWorld(new World(new Vec2(0,0), true), blueprint);
			this.simulation.getBall().applyLinearImpulse(new Vec2(5000,7000), this.simulation.getBall().getPosition());
			
			this.controlMessages = new SizedStack<InputMessage>(50);
			this.positionMessages = new SizedStack<InputMessage>(50);
		} catch (BadInputMessageException e1) {
			System.err.println("Failed to create init state for message simulation");
			e1.printStackTrace();
		}
	}
	
	
	@Override
	public void run() {
		
		try{
		
			while(!Thread.interrupted() && this.running){
				
				simulation.getPhysics().step(1f/60f, 10, 8);
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
