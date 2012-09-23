package fi.nakoradio.hwo.ai;

import org.apache.log4j.Logger;
import org.jbox2d.common.Vec2;

import fi.nakoradio.hwo.integration.core.InputMessage;
import fi.nakoradio.hwo.integration.core.Messenger;
import fi.nakoradio.hwo.main.HWOBot;
import fi.nakoradio.hwo.physics.PhysicsWorld;
import fi.nakoradio.hwo.physics.visualization.GameVisualizer;
import fi.nakoradio.hwo.util.Utils;

public class PaddleMover implements Runnable {

	private static Logger logger = Logger.getLogger(PaddleMover.class);
	
	private Thread thread;
	private boolean running = false;
	private float firstTarget;
	private float secondTarget;
	private ServerClone serverClone;
	private Messenger messenger;
	private ServerCloneSynchronizer synchronizer;
	
	private float throttle = 1;
	
	//private float evaluatedMaxSpeed = 0;
	private float lastOptimizedTarget = 0;

	private float lastPaddlePosition;

	private boolean newTargetsForOptimizer = true;

	private boolean targetReached = false;
	
	private long synchronizerLastRun = 0;

	private float myLastPos = 0;
	
	public PaddleMover(ServerClone serverClone, Messenger messenger, ServerCloneSynchronizer synchronizer) {
		this.serverClone = serverClone;
		this.messenger = messenger;
		this.synchronizer = synchronizer;
	}

	
	public void setTargets(float firstTarget, float secondTarget){
		this.firstTarget = firstTarget;
		this.secondTarget = secondTarget;
	}
	
	private void act(){
		
		logger.debug("act() - START");
		
		
		float myPos = serverClone.getSimulation().getMyPaddle().getPosition().y;
		
		/// hack to detect paddle bounce on wall.. 
		// for some reason serverClone.getSimulation().getMyPaddle().getLinearVelocity().y < 0 && this.throttle > 0 did not detect it
		if( 	(myPos - this.myLastPos  > 0 && this.throttle < 0) ||
				(myPos - this.myLastPos  < 0 && this.throttle > 0)){
			logger.debug("Something wrong? direction and throttle not matching");
			this.throttle = 0;
			this.targetReached = false;
			serverClone.setMyPaddleSpeed(new Vec2(0,0));
		}
		
		
		float optimizedTarget = calculateOptimizedTarget();
		float distanceToTarget = Math.abs(myPos-optimizedTarget);
		float newThrottle = 0;
		
		logger.debug("Target reached: " + this.targetReached + 
				" current optimized target: " + optimizedTarget + 
				" my position: " + myPos + " last pos: " + this.myLastPos +
				" current throttle: " + Utils.toStringFormat(this.throttle));
		
		
		this.myLastPos = myPos;
		
		if(!this.targetReached){
			
			if(distanceToTarget > getMaxDistanceInOneTick()){
				newThrottle = 1 * getThrottleDirection(myPos,optimizedTarget);
			}else {
				newThrottle = getThrottleToReachTargetInOneTick(distanceToTarget) * getThrottleDirection(myPos,optimizedTarget);
			}
			
			logger.debug("We should use throttle: " + Utils.toStringFormat(newThrottle));
			if(newThrottle != this.throttle){
				//this.throttle = newThrottle;
				float paddleSpeed = newThrottle * serverClone.getDeterminedPaddleMaxSpeed();
				if(Math.abs(newThrottle) <= 0.0001){// || Math.abs(myPos-optimizedTarget) < serverClone.getCurrentBlueprint().getMyPaddle().getHeight()/5 ){ 
					logger.debug("Target REACHED. Lets stop");
					this.targetReached = true; 
					this.throttle  = 0;
					paddleSpeed = 0;
				}else {
					logger.debug("Decided on paddle direction: " + Utils.toStringFormat(newThrottle) + " in position " + Utils.toStringFormat(myPos) + " when target was " + Utils.toStringFormat(optimizedTarget));
				}
				
				/*while(!messenger.messageStackAvailable()){
					logger.debug("Exceeded the output pipe. Forced to wait");
					try { Thread.sleep(5); } catch(Exception e){ logger.error("Failed to sleep"); }
				}*/
				/*boolean sentOk = false;
				while(!sentOk){
					if(messenger.canMessageBeSent()){
						messenger.sendPaddleMovementMessage(newThrottle);
						this.throttle = newThrottle;
						serverClone.setMyPaddleSpeed(new Vec2(0,paddleSpeed));
						sentOk = true;
					}else {
						try { Thread.sleep(5); } catch(Exception e){ }
					}
				}*/
				if(messenger.sendPaddleMovementMessage(newThrottle)){
					this.throttle = newThrottle;
					serverClone.setMyPaddleSpeed(new Vec2(0,paddleSpeed));
				}
				
				
			}else {
				logger.debug("Current throttle was already set to that");
			}
		}
		
		logger.debug("act() - END");
		
	}
	
	
	
	private float getThrottleToReachTargetInOneTick(float distanceToTarget) {
		return distanceToTarget / getMaxDistanceInOneTick() * 1;
	}


	private float getThrottleDirection(float mypos, float optimizedTarget) {
		if(mypos < optimizedTarget) return 1;
		if(mypos > optimizedTarget) return -1;
		return 0;
	}


	private float getMaxDistanceInOneTick() {
		//return this.evaluatedMaxSpeed * ((float)serverClone.getCurrentBlueprint().getTickInterval()) / 1000f;
		return serverClone.getDeterminedPaddleMaxSpeed() * ((float)serverClone.getCurrentBlueprint().getTickInterval()) / 1000f;
	}


	private boolean hasPaddleMoved() {
		if(this.lastPaddlePosition != serverClone.getSimulation().getMyPaddle().getPosition().y){
			this.lastPaddlePosition = serverClone.getSimulation().getMyPaddle().getPosition().y;
			logger.debug("Paddle moved " + System.currentTimeMillis());
			return true;
		}
		
		return false;
	}


	/*xxxprivate void updateMaxSpeed() {
		Vec2 currentSpeed = this.serverClone.getMyPaddleSpeed();
		if(currentSpeed.length() > this.evaluatedMaxSpeed){
			this.evaluatedMaxSpeed = currentSpeed.length();
			logger.trace("New maxspeed evaluated: " + this.evaluatedMaxSpeed);
		}
	}
	*/


	// Optimazed target is so that we cath the ball with the end of the paddle that is farthest from the second target
	
	private float calculateOptimizedTarget() {
		
		// Lets not recalculate if targets have not changed
		if(newTargetsForOptimizer ){
			logger.debug("New targets set so we need to calculate the optimized target for target1: " + this.firstTarget + " and target2: " + this.secondTarget);
			newTargetsForOptimizer = false;
			this.targetReached = false;
			
			float target = firstTarget;
			
			// TODO: currently does not work... seems to calculate correctly but paddle is positioned sometimes too far off?
			/*
			float paddlePosition = serverClone.getSimulation().getMyPaddle().getPosition().y;
			float paddleHeight = serverClone.getSimulation().getBlueprint().getMyPaddle().getHeight();
			
			
			if(paddlePosition <= firstTarget){
				if(secondTarget >= firstTarget){
					target = firstTarget + paddleHeight/2 - paddleHeight/50 ; // some safe betting of extra 50%, with 10% paddle was sometimes just slighty off and missed the ball
				}else if(secondTarget < firstTarget){
					target = firstTarget - paddleHeight/2 + paddleHeight/50;
				}
			} else if(paddlePosition > firstTarget){
				
				if(secondTarget <= firstTarget){
					target = firstTarget - paddleHeight/2 + paddleHeight/50;
				} else if(secondTarget > firstTarget){
					target = firstTarget + paddleHeight/2 - paddleHeight/50;
				}
			}*/
			
			if(HWOBot.visualize){
				PhysicsWorld w = HWOBot.visualizer.getWorld();
				if(w != null) w.getMarker(2).setTransform(new Vec2(0,target), 0f);
			}
			
			this.lastOptimizedTarget = target;
			return target;
		}
		return this.lastOptimizedTarget;
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
				
				//We need to wait for the synchronizer to run. Otherwise the paddle has not moved so there is no point
				while(this.synchronizerLastRun == synchronizer.getLastRun()){
					Thread.sleep(5);
				}
				this.synchronizerLastRun = synchronizer.getLastRun();
				 
				act();
			}
			
		}catch(InterruptedException e){
			logger.error("PaddleMover thread exception: " + e);
		}
		
	}


	public void setTargets(Vec2[] deathPoints) {
		logger.debug("Setting targets");
		if(deathPoints.length >= 1) logger.debug("Setting target1: " + deathPoints[0]);
		if(deathPoints.length >= 2) logger.debug("Setting target2: " + deathPoints[1]);
		if(deathPoints.length >= 1 && deathPoints[0] != null && Math.abs(this.firstTarget - deathPoints[0].y) > 2){
			logger.debug("new target1 difference: " + Math.abs(this.firstTarget - deathPoints[0].y));
			this.firstTarget = deathPoints[0].y;
			this.newTargetsForOptimizer = true;
		}
		if(deathPoints.length >= 2 && deathPoints[1] != null && Math.abs(this.secondTarget - deathPoints[1].y) > 2){
			logger.debug("new target2 difference: " + Math.abs(this.firstTarget - deathPoints[0].y));
			this.secondTarget = deathPoints[1].y;
			this.newTargetsForOptimizer = true;
		}
	}

	

}
