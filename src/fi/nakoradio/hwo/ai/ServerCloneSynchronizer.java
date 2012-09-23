package fi.nakoradio.hwo.ai;

import org.apache.log4j.Logger;
import org.jbox2d.common.Vec2;

import fi.nakoradio.hwo.main.HWOBot;
import fi.nakoradio.hwo.physics.visualization.GameVisualizer;

public class ServerCloneSynchronizer implements Runnable {

	private static Logger logger = Logger.getLogger(ServerCloneSynchronizer.class);
	
	private Thread thread;
	public boolean running = false;
	private ServerClone clone;
	
	private boolean batchUpdating = false;

	private long lastRunTimestamp = 0;
	
	public ServerCloneSynchronizer(ServerClone clone){
		this.clone = clone;
	}

	// Returns the current time on server. We assume it to be in sycn with local time.
	public long getServerTime(){
		return System.currentTimeMillis()+0;
	}
	
	public void start() {
		this.thread = new Thread(this);
		this.thread.start();
	}

	public void shutdown() {
		this.running = false;
		if(running) this.thread.interrupt();
	}
	
	// TODO: one could also calculate this instead of loop.
	// timestamp the next tick should be run
	public long getNextTickStart(){
		long nextTickTimestamp = this.clone.getCurrentBlueprint().getTimestamp();
		while(nextTickTimestamp <= getServerTime()){
			nextTickTimestamp += this.clone.getCurrentBlueprint().getTickInterval();
		}
		return nextTickTimestamp;
	}
	
	@Override
	// TODO: these should actually match the timestamps coming from server. you should print and check.
	public void run() {
		this.running = true;
		
		try{
			// We want this to match the actual tick event on the server. i.e the ticks will happen at same time local and server
			while(!Thread.interrupted() && this.running){
				long nextTickStart = getNextTickStart();
				long sleepTillNextTick = nextTickStart - System.currentTimeMillis();
				Thread.sleep(sleepTillNextTick);
				act();
				logger.debug("\nmypaddle: paddle position on simulation at ["+(nextTickStart-HWOBot.firstPosMessageTime)+"] is ["+clone.getSimulation().getMyPaddle().getPosition()+"]");
				
			}
			
		}catch(Exception e){
			logger.info("ServerCloneSynchronizer thread shutdown: " + e);
		}
		
	}
	
	public void serverCloneUpdated() {
		this.batchUpdating = true;
		
		long forwardedTo = this.clone.getCurrentBlueprint().getTimestamp();
		long nextTickStart = getNextTickStart(); //TODO: this is the same stamp that the main loop is waiting... or should be. add some printing. but what if it takes as more then 2xtick time to update here? then main loop would miss one tick?
		
		while(forwardedTo < nextTickStart){
			forwardByTick();
			forwardedTo += this.clone.getCurrentBlueprint().getTickInterval();
		}
		this.lastRunTimestamp = System.currentTimeMillis();
		this.batchUpdating = false;
	}
	
	private void act() throws InterruptedException {
		logger.trace("act() - START");
		// TODO: maybe to do this nicer?
		while(batchUpdating) Thread.sleep(1); 
		forwardByTick();
		this.lastRunTimestamp = System.currentTimeMillis();
		logger.trace("act() - DONE");
	}
	
	public void forwardByTick() {
		
		int dtDivider = (int)(((float)this.clone.getCurrentBlueprint().getTickInterval()) / (1f/60f*1000f)+1); 
		float dt = ((float)this.clone.getCurrentBlueprint().getTickInterval()) / dtDivider / 1000f;

		for(int i = 0; i < dtDivider; i++){
			this.clone.getSimulation().getPhysics().step(dt, 10, 8);
			handleMyPaddleWallCollision();
		}
	}

	
	// TODO: !!! this causes some wobbling when situation from server is update correcly, maybe we can live with that
	// TODO: could be done by physics engine... by setting paddle to collide with wall.... but is there continues collision as they now overlap
	private void handleMyPaddleWallCollision() {
		float paddleHeightAdjs = clone.getCurrentBlueprint().getMyPaddle().getHeight()/2;
		if(		clone.getSimulation().getMyPaddle().getPosition().y + paddleHeightAdjs  >= clone.getCurrentBlueprint().getArena().getHeight() ||
				clone.getSimulation().getMyPaddle().getPosition().y - paddleHeightAdjs  <= 0){
			clone.getSimulation().getMyPaddle().setLinearVelocity( clone.getSimulation().getMyPaddle().getLinearVelocity().mul(-1) );
			logger.debug("Switched paddle direction");
		}
		
	}

	public long getLastRun() {
		return this.lastRunTimestamp;
	}
	
	

	
	

}
