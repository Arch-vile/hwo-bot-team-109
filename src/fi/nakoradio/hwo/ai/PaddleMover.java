package fi.nakoradio.hwo.ai;

import org.jbox2d.common.Vec2;

import fi.nakoradio.hwo.integration.core.InputMessage;
import fi.nakoradio.hwo.integration.core.Messenger;

public class PaddleMover implements Runnable {

	private Thread thread;
	private boolean running = false;
	private float firstTarget;
	private float secondTarget;
	private ServerClone serverClone;
	private Messenger messenger;
	
	private float throttle;
	
	public PaddleMover(ServerClone serverClone, Messenger messenger) {
		this.serverClone = serverClone;
		this.messenger = messenger;
	}

	
	public void setTargets(float firstTarget, float secondTarget){
		this.firstTarget = firstTarget;
		this.secondTarget = secondTarget;
	}
	
	private void act(){
		
		float optimizedTarget = calculateOptimizedTarget();

		float newThrottle = 0;
		if(Math.abs(serverClone.getSimulation().getMyPaddle().getPosition().y - optimizedTarget) < 4 )
			newThrottle = 0;
		else if(serverClone.getSimulation().getMyPaddle().getPosition().y > optimizedTarget){
			newThrottle = -1;
		}else
			newThrottle = 1;
		
		if(newThrottle != this.throttle){
			messenger.sendPaddleMovementMessage(newThrottle);
		}
		
	}
	
	
	
	// TODO
	private float calculateOptimizedTarget() {
		return firstTarget;
	}


	public void start() {
		this.thread = new Thread(this);
		this.thread.start();
	}

	public void shutdown() {
		this.running = false;
		this.thread.interrupt();
	}
	@Override
	public void run() {
		this.running = true;
		
		try{
			while(!Thread.interrupted() && this.running){
				Thread.sleep(5); 
				act();
			}
			
		}catch(Exception e){
			System.err.println("PaddleMover thread exception: " + e);
		}
		
	}

	

}
