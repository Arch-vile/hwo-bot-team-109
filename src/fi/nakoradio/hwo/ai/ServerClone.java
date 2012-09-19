package fi.nakoradio.hwo.ai;

import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import fi.nakoradio.hwo.model.objects.Blueprint;
import fi.nakoradio.hwo.physics.Constants;
import fi.nakoradio.hwo.physics.ObjectPath;
import fi.nakoradio.hwo.physics.PhysicsWorld;
import fi.nakoradio.hwo.physics.ServerCloneListener;

public class ServerClone {
	private final static float BALL_MIN_DISTANCE_CALC = 5; // minimum distance required for the ball to be traversed before speed can be calculated
	private PhysicsWorld simulation;
	private Vec2 ballOrigin;
	private long ballOriginTime;
	private float speedMultiplier = 1;
	private boolean goingLeft = true;
	private boolean goingUp = true;
	private Float lastX;
	private Float lastY;
	
	private ObjectPath ballPath;
	
	private Vec2 previousBallTransform;
	
	private Blueprint currentBlueprint;
	private Blueprint previousBluePrint;
	
	private long currentTime;
	private float sumdtRemainder;
	
	public ServerClone(Blueprint blueprint) {
		World world = new World(new Vec2(0,0),true);
		this.simulation = new PhysicsWorld(world, blueprint);
		//world.setContactListener(new ServerCloneListener(this));
		this.ballPath = new ObjectPath();
		this.update(blueprint, false);
		
	}
	
	public float getSpeedMultiplier(){
		return this.speedMultiplier;
	}
	
	public void update(Blueprint blueprint, boolean updatePhantom){
		
		
		if(this.previousBluePrint != null){
			Vec2 speed = calculateBallSpeed();
		
			if(updatePhantom){
				this.simulation.getPhantom().setLinearVelocity(speed);
			}
		}
		
		this.simulation.setObjectPositions(blueprint, updatePhantom);
		this.previousBluePrint = this.currentBlueprint;
		this.currentBlueprint = blueprint;
		this.currentTime = this.currentBlueprint.getTimestamp();
	}
	
	public Vec2 getOpponentPaddleSpeed(){
		Vec2 newPos = getCurrentBlueprint().getOpponentPaddle().getCenterPosition();
		Vec2 refrencePos = getPreviousBluePrint().getOpponentPaddle().getCenterPosition();
		Vec2 distance = newPos.sub(refrencePos);
		long elapsedTime = getCurrentBlueprint().getTimestamp() - getPreviousBluePrint().getTimestamp();
		float speedValue = 1000 * ( distance.length() / elapsedTime  );
		distance.normalize();
		Vec2 speed = distance.mul(speedValue * Constants.MYSTICAL_PADDLE_BOUNCE_SPEED_MODIFIER);
		return speed;
	}
	
	
	/*public Vec2 calculateObjectSpeed(Body bodyFromCurrentBluePrint, Body bodyFromPreviousBluePrint){
		Vec2 newPos = getCurrentBlueprint().getOpponentPaddle().getCenterPosition();
		Vec2 refrencePos = getPreviousBluePrint().getOpponentPaddle().getCenterPosition();
		Vec2 distance = newPos.sub(refrencePos);
		long elapsedTime = getCurrentBlueprint().getTimestamp() - getPreviousBluePrint().getTimestamp();
		float speedValue = 1000 * ( distance.length() / elapsedTime  );
		distance.normalize();
		Vec2 speed = distance.mul(speedValue * Constants.MYSTICAL_PADDLE_BOUNCE_SPEED_MODIFIER);
		return speed;
	}*/
	
	// TODO: do we really need to calculate ball speeed differently? It bounces and this could cause interesting speed vectors also
	// we do require certain distance travelled to measure ball speed but is it necessary? propably not.
	public Vec2 calculateBallSpeed() {
		if(bounced(this.currentBlueprint)) this.ballPath.clear();
		
		Vec2 newPos = this.currentBlueprint.getBall().getPosition();
		this.ballPath.push(newPos, this.currentBlueprint.getTimestamp()); // TODO: is this correct is the timestamp the time of the ball position capture or something else

		if(this.ballPath.size() <= 1){
			return new Vec2(0,0);
		}
		
		Vec2 referencePos = this.ballPath.peekFirst().getPosition();
		Vec2 distance = newPos.sub(referencePos);
		if(distance.length() < BALL_MIN_DISTANCE_CALC){
			return new Vec2(0,0);
		}
		
		// speed / seconds
		long elapsedTime = this.currentBlueprint.getTimestamp() - this.ballPath.peekFirst().getTimestamp();
		float speedValue = 1000 * ( distance.length() / elapsedTime  );
		distance.normalize();
		Vec2 speed = distance.mul(speedValue);
		
		this.ballPath.popFirst();
		return speed;
	}
	
	private Vec2 dkdkdkkkd(Blueprint blueprint, long tickCount) {
		Vec2 newPos = blueprint.getBall().getPosition();
		Vec2 previousPos = this.simulation.getBlueprint().getBall().getPosition();
		
		Vec2 distance = newPos.sub(previousPos);
		if(distance.length() < BALL_MIN_DISTANCE_CALC){
			System.out.println(0);
			return new Vec2(0,0);
		}
		// speed / seconds
		float speedValue = 1000 * ( distance.length() / (tickCount*blueprint.getTickInterval()) );
		System.out.println(speedValue);
		distance.normalize();
		Vec2 speed = distance.mul(speedValue);
		return speed;
	}
	
	

	
	
	public void forwardToPresent(){
		for(int i = 0; i < 10 * speedMultiplier; i++){
			this.simulation.getPhysics().step(1f/60f, 10, 8);
		}
	}
	
	
	public void forward(long milliseconds) {
		int iterations = (int)(milliseconds / ((1f/60f)*1000));
		
		
		System.out.println("ITERATIONS:" + iterations);
		for(int i = 0; i < iterations; i++){
			this.simulation.getPhysics().step(1f/60f, 10, 8);
		}
	}
	

	// If bounce happens zero speed is returned as it cannot be reliably calculated
	private Vec2 calculateBallSpeeddssdsddsdsd(Blueprint blueprint, boolean bounced) {
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
		float speedValue = ( distance.length() / elapsedTime * 1000 ); 
		
		
		if(Math.random() < 0.001){
			System.err.println("Do what is explained below");
			System.exit(1);
		}
		//if we set this to 0 then for some reason speedmultiplier is not counted
		//likely because with speed 0 there is problem with something in multip calcualtor part
		//System.out.println(speedValue);
		
		distance.normalize();
		Vec2 speed = distance.mul(speedValue);
		
		this.ballOrigin = currentPos;
		this.ballOriginTime = blueprint.getTimestamp();
		
		return speed;
	}

	
	private boolean bounced(Blueprint blueprint) {
		// Transform from previous blueprint to this new blueprint
		Vec2 currentBallTransform = blueprint.getBall().getPosition().sub(this.simulation.getBlueprint().getBall().getPosition());
		
		boolean bounce = false;
		if(this.previousBallTransform != null){
			float previousAngle = MathUtils.atan2(this.previousBallTransform.y, this.previousBallTransform.x);
			float currentAngle = MathUtils.atan2(currentBallTransform.y, currentBallTransform.x);
			float angleDifference = currentAngle-previousAngle;//;(new Mat22(currentBallTransform, this.previousBallTransform)).getAngle();
			
			if(Math.abs(angleDifference) > 0.005){ //TODO: what is correct value
				bounce = true;
			}
		}
		
		this.previousBallTransform = currentBallTransform;
		return bounce;
		
	}
	
	// TODO: will not detect all situations. if last position is far from wall and new position is just after bounce
	// then this will detect bounce only on next iteration. 
	// Note that it is not safe to call this twice for same blueprit as the last values are updated
	private boolean bouncedeeeeeeeee(Blueprint blueprint) {
		
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
		
		
		if(bounced) System.out.println("BOUNCE OLD");
		return bounced;
	}

	public PhysicsWorld getSimulation() {
		return simulation;
	}

	public Blueprint getCurrentBlueprint() {
		return currentBlueprint;
	}

	public Blueprint getPreviousBluePrint() {
		return previousBluePrint;
	}

	public void advanceToPresentTime() {
		
		System.out.println("Advance to present time by forwarding " + (System.currentTimeMillis()-this.currentTime) + " ms ");
		long startTime = this.currentTime;
		float sumdt = this.sumdtRemainder; // What was left over from last run
		long runTime = (long)sumdt;
		
		float dt = 1f / 160f;
		while(startTime + runTime <= System.currentTimeMillis()){
			getSimulation().getPhysics().step(dt, 10, 8);
			sumdt += dt;
			runTime = (long)(sumdt*1000);
		}
		this.sumdtRemainder = (sumdt*1000 - runTime) / 1000f;
		this.currentTime = startTime + runTime;
		
		System.out.println("Advance to present time - DONE");
		
	}

	

	
	
	
	
	
}
