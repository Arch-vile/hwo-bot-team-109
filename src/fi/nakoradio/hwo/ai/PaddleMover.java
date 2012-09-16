package fi.nakoradio.hwo.ai;

import org.jbox2d.common.Vec2;

import fi.nakoradio.hwo.integration.core.InputMessage;
import fi.nakoradio.hwo.integration.core.Messenger;

public class PaddleMover implements Runnable {

	// TODO: Note that the same messenger will be read from multiple threads... so atleast make sure you do not pop messages at wrong point
	private Messenger messenger;
	private Vec2 target;
	private Thread thread;
	private boolean running = false;
	
	public PaddleMover(Messenger messenger) {
		this.messenger = messenger;
	}

	public void move(Vec2 firstPoint) {
		this.target = firstPoint;
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
			
				
				Thread.sleep(20); 
				
				
				
				
				if(!messenger.getPositionMessages().empty()){
					
					InputMessage positionMessage = messenger.getPositionMessages().peek();
					Vec2 distanceToTarget = distanceToTarget(positionMessage);
					
					
					
				}
				
					
				
				
			}
			
		}catch(Exception e){
			System.err.println("PaddleMover thread exception: " + e);
		}
		
	}

	// Returns zero if any part of the paddle is on target
	private Vec2 distanceToTarget(InputMessage positionMessage) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
