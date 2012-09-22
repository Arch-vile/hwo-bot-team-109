package fi.nakoradio.hwo.ai;

import org.apache.log4j.Logger;
import org.jbox2d.common.Vec2;

import fi.nakoradio.hwo.integration.core.InputMessage;
import fi.nakoradio.hwo.integration.core.Messenger;
import fi.nakoradio.hwo.main.HWOBot;

public class PaddleMover implements Runnable {

	private static Logger logger = Logger.getLogger(PaddleMover.class);
	
	private Thread thread;
	private boolean running = false;
	private float firstTarget;
	private float secondTarget;
	private ServerClone serverClone;
	private Messenger messenger;
	
	private float throttle = 1;
	
	private float evaluatedMaxSpeed = 0;
	
	
	public PaddleMover(ServerClone serverClone, Messenger messenger) {
		this.serverClone = serverClone;
		this.messenger = messenger;
	}

	
	public void setTargets(float firstTarget, float secondTarget){
		this.firstTarget = firstTarget;
		this.secondTarget = secondTarget;
	}
	
	private void act(){
		
		logger.debug("act() - START");
		
		updateMaxSpeed();
		
		float optimizedTarget = calculateOptimizedTarget();

		float newThrottle = 0;
		if(Math.abs(serverClone.getSimulation().getMyPaddle().getPosition().y - optimizedTarget) < 10 )
			newThrottle = 0;
		else if(serverClone.getSimulation().getMyPaddle().getPosition().y > optimizedTarget){
			newThrottle = -1;
		}else
			newThrottle = 1;
		
		if(newThrottle != this.throttle){
			this.throttle = newThrottle;
			logger.debug("Decided on paddle direction: " + this.throttle);
			messenger.sendPaddleMovementMessage(this.throttle);
			//serverClone.setPaddleSpeed(xx);
		}
		
		logger.debug("act() - END");
		
	}
	
	
	
	private void updateMaxSpeed() {
		Vec2 currentSpeed = this.serverClone.getMyPaddleSpeed();
		if(currentSpeed.length() > this.evaluatedMaxSpeed){
			this.evaluatedMaxSpeed = currentSpeed.length();
			logger.trace("New maxspeed evaluated: " + this.evaluatedMaxSpeed);
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
			messenger.sendPaddleMovementMessage(this.throttle);
			while(!Thread.interrupted() && this.running){

				//TODO: !!!!!! if this is small as we want it to be.. like 5 then message count is exceeded, why?
				Thread.sleep(50); 
				act();
			}
			
		}catch(Exception e){
			System.err.println("PaddleMover thread exception: " + e);
		}
		
	}


	public void setTargets(Vec2[] deathPoints) {
		if(deathPoints.length >= 1 && deathPoints[0] != null)
			this.firstTarget = deathPoints[0].y;
		if(deathPoints.length >= 2 && deathPoints[1] != null)
			this.secondTarget = deathPoints[1].y;
		
	}

	

}
