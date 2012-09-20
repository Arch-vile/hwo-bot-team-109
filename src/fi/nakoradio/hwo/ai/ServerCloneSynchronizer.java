package fi.nakoradio.hwo.ai;

public class ServerCloneSynchronizer implements Runnable {

	private Thread thread;
	public boolean running = false;
	private ServerClone clone;
	
	private boolean batchUpdating = false;
	
	public ServerCloneSynchronizer(ServerClone clone){
		this.clone = clone;
	}
	
	public void start() {
		this.thread = new Thread(this);
		this.thread.start();
	}

	public void shutdown() {
		this.running = false;
		this.thread.interrupt();
	}
	
	// TODO: one could also calculate this instead of loop.
	// timestamp the next tick should be run
	public long getNextTickStart(){
		long nextTickTimestamp = this.clone.getCurrentBlueprint().getTimestamp();
		while(nextTickTimestamp <= System.currentTimeMillis()){
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
				long sleepTillNextTick = getNextTickStart() - System.currentTimeMillis();
				Thread.sleep(sleepTillNextTick); 
				act();
			}
			
		}catch(Exception e){
			System.err.println("ServerCloneSynchronizer thread exception: " + e);
		}
		
	}
	
	public void serverCloneUpdated() {
		System.out.println("Rolling server clone to current time after model update");
		this.batchUpdating = true;
		
		long forwardedTo = this.clone.getCurrentBlueprint().getTimestamp();
		long nextTickStart = getNextTickStart(); //TODO: this is the same stamp that the main loop is waiting... or should be. add some printing. but what if it takes as more then 2xtick time to update here? then main loop would miss one tick?
		while(forwardedTo < nextTickStart){
			forwardByTick();
			forwardedTo += this.clone.getCurrentBlueprint().getTickInterval();
		}
		
		this.batchUpdating = false;
		System.out.println("Server clone back in real time");
	}
	
	private void act() throws InterruptedException {
//		System.out.print("Simulating...");
		
		// TODO: maybe to do this nicer?
		while(batchUpdating) Thread.sleep(1); 
		forwardByTick();
		
	//	System.out.println("DONE");
	}
	
	private void forwardByTick() {
		int dtDivider = (int)(((float)this.clone.getCurrentBlueprint().getTickInterval()) / (1f/60f*1000f)+1); 
		float dt = ((float)this.clone.getCurrentBlueprint().getTickInterval()) / dtDivider / 1000f;

		for(int i = 0; i < dtDivider; i++){
			this.clone.getSimulation().getPhysics().step(dt, 10, 8);
		}
	}

	
	

}
