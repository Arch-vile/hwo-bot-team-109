package fi.nakoradio.hwo.physics;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.contacts.Contact;

import fi.nakoradio.hwo.ai.ServerClone;

public class DeathPointListener implements ContactListener {

	PhysicsWorld world;
	Vec2 deathPoint;
	private ServerClone clone;
	
	public DeathPointListener(PhysicsWorld world, ServerClone clone){
		this.world = world;
		this.world.getPhysics().setContactListener(this);
		this.clone = clone;
	}
	
	
	@Override
	public void beginContact(Contact contact) {

		Body myDeathLine = world.getMyDeathLine();
		Body opponentDeathLine = world.getOpponentDeathLine();
		Body ball = world.getBall();
		Body fixtureA = contact.getFixtureA().getBody();
		Body fixtureB = contact.getFixtureB().getBody();
		
		// Simulate hit to opponent paddle
		if(		(fixtureA == opponentDeathLine || fixtureB == opponentDeathLine ) && (fixtureA == ball || fixtureB == ball )){
			Vec2 newBallSpeed = new Vec2(ball.getLinearVelocity());
			float maxSpeed = clone.getDeterminedPaddleMaxSpeed();
			
			// going downwards
			if(ball.getLinearVelocity().y < 0){
				newBallSpeed.add(new Vec2(0,-1*maxSpeed));
			}
			
			/// going upwards
			if(ball.getLinearVelocity().y > 0){
				newBallSpeed.add(new Vec2(0,maxSpeed));
			}
			
			ball.setLinearVelocity(newBallSpeed);
		}
		
		
		// Track my death points
		if(		(fixtureA == myDeathLine || fixtureB == myDeathLine ) && (fixtureA == ball || fixtureB == ball )){
			// TODO: Other cases where we should copy instead of reference? Here the balls vector is changing so the deathPoint keeps changing also
			this.deathPoint = new Vec2(world.getBall().getPosition());
			//System.out.println("DEATH AT: " + deathPoint);
		}
		
	}

	@Override
	public void endContact(Contact arg0) {
		//System.out.println("endContact");
		
	}

	@Override
	public void postSolve(Contact arg0, ContactImpulse arg1) {
		//System.out.println("postSolve");
		
	}

	@Override
	public void preSolve(Contact arg0, Manifold arg1) {
		//System.out.println("preSolve");
		
	}


	public Vec2 getDeathPoint() {
		return deathPoint;
	}

	public Vec2 popDeathPoint(){
		Vec2 toReturn = this.deathPoint;
		this.deathPoint = null;
		return toReturn;
	}
	
}
