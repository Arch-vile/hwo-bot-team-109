package fi.nakoradio.hwo.ai;

import org.apache.log4j.Logger;
import org.jbox2d.common.Vec2;

import fi.nakoradio.hwo.integration.core.InputMessage;
import fi.nakoradio.hwo.integration.core.Messenger;
import fi.nakoradio.hwo.main.HWOBot;

public class EvenSimplerPaddleMover implements Runnable {

	private static Logger logger = Logger.getLogger(EvenSimplerPaddleMover.class);
	
	private Thread thread;
	private boolean running = false;
	private float firstTarget;
	private float secondTarget;
	private ServerClone serverClone;
	private Messenger messenger;
	
	private float throttle = 1;
	
	public EvenSimplerPaddleMover(ServerClone serverClone, Messenger messenger) {
		this.serverClone = serverClone;
		this.messenger = messenger;
	}

	
	public void setTargets(float firstTarget, float secondTarget){
		this.firstTarget = firstTarget;
		this.secondTarget = secondTarget;
	}
	
	private void act(){
		
		logger.debug("act() - START");
		
		float newThrottle = 0;
		if(Math.random() < 0.5) newThrottle = 1;
		else
			newThrottle = -1;
		
		this.throttle = newThrottle;
		logger.debug("Decided on paddle direction: " + this.throttle);
		messenger.sendPaddleMovementMessage(this.throttle);
		
		// some delay to compasete for ping
		try { Thread.sleep(5); }catch(Exception e) { logger.error("Sleep fail in act"); }
		serverClone.setMyPaddleSpeed(new Vec2(0,this.throttle * serverClone.getDeterminedPaddleMaxSpeed()));
		
		logger.debug("act() - END");
		
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
				Thread.sleep(206); 
				//act();
			}
			
		}catch(Exception e){
			logger.info("PaddleMover shutdown: " + e);
		}
		
	}


	public void setTargets(Vec2[] deathPoints) {
		if(deathPoints.length >= 1 && deathPoints[0] != null)
			this.firstTarget = deathPoints[0].y;
		if(deathPoints.length >= 2 && deathPoints[1] != null)
			this.secondTarget = deathPoints[1].y;
		
	}

	

}
