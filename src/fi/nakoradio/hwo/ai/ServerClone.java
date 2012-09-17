package fi.nakoradio.hwo.ai;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import fi.nakoradio.hwo.model.objects.Blueprint;
import fi.nakoradio.hwo.physics.PhysicsWorld;

public class ServerClone {
	private final static float BALL_MIN_DISTANCE_CALC = 10; // minimum distance required for the ball to be traversed before speed can be calculated
	private PhysicsWorld simulation;
	private Vec2 ballOrigin;
	private long ballOriginTime;
	private float speedMultiplier = 1;
	private boolean goingLeft = true;
	private boolean goingUp = true;
	private Float lastX;
	private Float lastY;
	
	public ServerClone(Blueprint blueprint) {
		this.simulation = new PhysicsWorld(new World(new Vec2(0,0),true), blueprint);
	}
	
	public float getSpeedMultiplier(){
		return this.speedMultiplier;
	}
	
	public void update(Blueprint blueprint){
		boolean bounced = bounced(blueprint);
		
		if(!bounced){
			// Lets handle the twitch in matrix i.e. lets adjust our speeds to better match server model
			Vec2 lastUpdatePosition = this.simulation.getBlueprint().getBall().getPosition();
			float simulatedDistance = this.simulation.getBall().getPosition().sub(lastUpdatePosition).length();
			float actualDistance = blueprint.getBall().getPosition().sub(lastUpdatePosition).length();
			
			// TODO: is never decreased
			// we are going too slow
			if(simulatedDistance > BALL_MIN_DISTANCE_CALC && actualDistance > BALL_MIN_DISTANCE_CALC && simulatedDistance < actualDistance){
				System.out.println("We are going too slow");
				this.speedMultiplier += 0.01;
				System.out.println("Decreased speed multiplier to: " + this.speedMultiplier);
			}
				
			// we are going too fast
			if(simulatedDistance > BALL_MIN_DISTANCE_CALC && actualDistance > BALL_MIN_DISTANCE_CALC && simulatedDistance > actualDistance){
				System.out.println("We are going too fast");
				this.speedMultiplier -= 0.01;
				System.out.println("Decreased speed multiplier to: " + this.speedMultiplier);
			}
		}
		
		
		this.simulation.setObjectPositions(blueprint);
		
		Vec2 speed = calculateBallSpeed(blueprint, bounced);
		this.simulation.getBall().setLinearVelocity(speed);
		
	}
	
	
	public void forwardToPresent(){
		for(int i = 0; i < 10 * speedMultiplier; i++){
			this.simulation.getPhysics().step(1f/60f, 10, 8);
		}
	}
	
	public void forward(long milliseconds) {
		
		
		int iterations = (int)(milliseconds / ((1f/60f)*1000));
		for(int i = 0; i < iterations; i++){
			this.simulation.getPhysics().step(1f/60f, 10, 8);
		}
	}
	

	// If bounce happens zero speed is returned as it cannot be reliably calculated
	private Vec2 calculateBallSpeed(Blueprint blueprint, boolean bounced) {
		Vec2 currentPos = blueprint.getBall().getPosition();
		
		if(ballOrigin == null || bounced){
			ballOrigin = currentPos;
			ballOriginTime = blueprint.getTimestamp();
			return new Vec2(0,0);
		}
		
		Vec2 distance = currentPos.sub(ballOrigin);
		if(distance.length() < BALL_MIN_DISTANCE_CALC)
			return new Vec2(0,0);
		
		long elapsedTime = blueprint.getTimestamp() - this.ballOriginTime;
		float speedValue = ( distance.length() / elapsedTime * 1000 ); if we set this to 0 then for some reason speedmultiplier is not counted
		likely because with speed 0 there is problem with something in multip calcualtor part
		System.out.println(speedValue);
		
		distance.normalize();
		Vec2 speed = distance.mul(speedValue);
		
		this.ballOrigin = currentPos;
		this.ballOriginTime = blueprint.getTimestamp();
		
		return speed;
	}

	// Note that it is not safe to call this twice for same blueprit as the last values are updated
	private boolean bounced(Blueprint blueprint) {
		
		boolean bounced = false;
		if(lastX != null && lastY != null){
			if(goingLeft && blueprint.getBall().getPosition().x > lastX ){
				goingLeft = false;
				bounced = true;
			} else if(!goingLeft && blueprint.getBall().getPosition().x < lastX){
				goingLeft = true;
				bounced = true;
			}
			
			if(goingUp && blueprint.getBall().getPosition().y < lastY ){
				goingUp = false;
				bounced = true;
			}else if(!goingUp && blueprint.getBall().getPosition().y > lastY){
				goingUp = true;
				bounced = true;
			}
		}
			
		lastX = blueprint.getBall().getPosition().x;
		lastY = blueprint.getBall().getPosition().y;
		
		return bounced;
	}

	public PhysicsWorld getSimulation() {
		return simulation;
	}

	

	
	
	
	
	
}
