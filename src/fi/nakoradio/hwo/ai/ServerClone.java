package fi.nakoradio.hwo.ai;

import org.apache.log4j.Logger;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import fi.nakoradio.hwo.util.*;

import fi.nakoradio.hwo.model.objects.Blueprint;
import fi.nakoradio.hwo.model.objects.ModelObject;
import fi.nakoradio.hwo.physics.Constants;
import fi.nakoradio.hwo.physics.ObjectPath;
import fi.nakoradio.hwo.physics.PhysicsWorld;
import fi.nakoradio.hwo.physics.ServerCloneListener;

public class ServerClone {
	
	private static Logger logger = Logger.getLogger(ServerClone.class);
	
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
	
	
	public ServerClone(Blueprint blueprint) {
		World world = new World(new Vec2(0,0),true);
		this.simulation = new PhysicsWorld(world, blueprint);
		//world.setContactListener(new ServerCloneListener(this));
		this.ballPath = new ObjectPath();
		this.update(blueprint);
		
	}
	
	public float getSpeedMultiplier(){
		return this.speedMultiplier;
	}
	
	public void update(Blueprint blueprint){
		
		this.previousBluePrint = this.currentBlueprint;
		this.currentBlueprint = blueprint;
		
		this.simulation.setObjectPositions(this.currentBlueprint);
		
		updateBallSpeed();
		updateMyPaddleSpeed();
		
		
	}
	
	private void updateMyPaddleSpeed() {
		if(this.previousBluePrint != null && this.currentBlueprint != null){
			Vec2 speed = calculateObjectSpeed(this.currentBlueprint.getMyPaddle(), this.previousBluePrint.getMyPaddle());
			if(speed != null){
				this.simulation.getMyPaddle().setLinearVelocity(speed);
			}
		}
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
	
	
	private Vec2 calculateObjectSpeed(ModelObject bodyFromCurrentBluePrint, ModelObject bodyFromPreviousBluePrint){
		Vec2 currentPos = new Vec2(bodyFromCurrentBluePrint.getPosition());
		Vec2 previousPos = new Vec2(bodyFromPreviousBluePrint.getPosition());
		Vec2 distance = currentPos.sub(previousPos);
		long elapsedTime = getCurrentBlueprint().getTimestamp() - getPreviousBluePrint().getTimestamp();
		float speedValue = 1000 * ( distance.length() / elapsedTime  );
		distance.normalize();
		Vec2 speed = distance.mul(speedValue);
		return speed;
	}
	
	
	
	public Vec2 getBallSpeed(){
		return new Vec2(this.simulation.getBall().getLinearVelocity());
	}
	
	public Vec2 getMyPaddleSpeed(){
		return new Vec2(this.simulation.getMyPaddle().getLinearVelocity());
	}
	
	
	// TODO: do we really need to calculate ball speeed differently? It bounces and this could cause interesting speed vectors also
		// we do require certain distance travelled to measure ball speed but is it necessary? propably not.
		// NOTE: It is dangarous to use the actual speed of the Body object. As it hits the wall the speed vector could have x velocity of 0
		private Vec2 updateBallSpeed() {
			Vec2 newPos = new Vec2(this.currentBlueprint.getBall().getPosition());
			this.ballPath.push(newPos, this.currentBlueprint.getTimestamp()); // TODO: is this correct is the timestamp the time of the ball position capture or something else
			
			if(this.previousBluePrint == null){
				logger.trace("calculateBallSpeed() - END - null because no previous blueprint");
				return null;
			}
			
			if(this.ballPath.size() < 2){
				logger.trace("calculateBallSpeed() - END - null because less then 2 elements in list");
				return null;
			}
			
			if(bounced()){
				this.ballPath.clear();
				logger.trace("calculateBallSpeed() - END - null because bounced");
				return null;
			}
			
			Vec2 referencePos = this.ballPath.peekFirst().getPosition();
			Vec2 distance = newPos.sub(referencePos);
			if(distance.length() < BALL_MIN_DISTANCE_CALC){
				logger.trace("calculateBallSpeed() - END -  null because not enough distance");
				return null;
			}
			
			// speed / seconds
			long elapsedTime = this.currentBlueprint.getTimestamp() - this.ballPath.peekFirst().getTimestamp();
			float speedValue = 1000 * ( distance.length() / elapsedTime  );
			distance.normalize();
			Vec2 speed = distance.mul(speedValue);
			
			this.ballPath.popFirst();
			logger.trace("calculateBallSpeed() - END -  normal " + speed);
			
			
			
			if(speed != null){
				this.simulation.getBall().setLinearVelocity(speed);
				Vec2 t = new Vec2(speed);
				t.normalize();
				logger.trace("Updated ball velocity to " + Utils.toStringFormat(t));
			}else {
				// note that when this happens... the synchronizer will not end up with correct end result
				logger.trace("Ball velocity set to 0 as it could not be calculated reliably");
				this.simulation.getBall().setLinearVelocity(new Vec2(0,0));
			}
			
			
			return speed;
		}
	
	

	public boolean bounced() {
		// Transform from previous blueprint to this new blueprint
		Vec2 currentBallTransform = this.currentBlueprint.getBall().getPosition().sub(this.previousBluePrint.getBall().getPosition());
		
		boolean bounce = false;
		if(this.previousBallTransform != null){
			float previousAngle = MathUtils.atan2(this.previousBallTransform.y, this.previousBallTransform.x);
			float currentAngle = MathUtils.atan2(currentBallTransform.y, currentBallTransform.x);
			
			logger.trace("Angles " + currentAngle + " - " + previousAngle);
			
			float angleDifference = currentAngle-previousAngle;//;(new Mat22(currentBallTransform, this.previousBallTransform)).getAngle();
			
			if(Math.abs(angleDifference) > 0.05){ //TODO: what is correct value? We need to required big enough difference so we do not detect bounce on normal variation
				bounce = true;
			}
		}
		
		this.previousBallTransform = currentBallTransform;
		return bounce;
		
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

	public void reset() {
		// TODO Auto-generated method stub
		
	}

	
	
}
