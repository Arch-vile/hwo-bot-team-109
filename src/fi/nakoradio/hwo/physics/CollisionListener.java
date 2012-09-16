package fi.nakoradio.hwo.physics;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;

public class CollisionListener implements ContactListener {

	PhysicsWorld world;
	Vec2 deathPoint;
	private String id;
	
	public CollisionListener(PhysicsWorld world, String id){
		this.world = world;
		this.id = id;
	}
	
	
	@Override
	public void beginContact(Contact contact) {
		
		System.out.println("["+ id + "] collision ");
		
		
		/*System.out.println("["+ id + "] collision ");
		System.out.println("" + (contact.getFixtureA().getBody() == world.getMyDeathLine() || contact.getFixtureB().getBody() == world.getMyDeathLine() ));
		System.out.println("" + (contact.getFixtureA().getBody() == world.getBall() || contact.getFixtureB().getBody() == world.getBall() ));
		*/
		if(		(contact.getFixtureA().getBody() == world.getMyDeathLine() || contact.getFixtureB().getBody() == world.getMyDeathLine() ) &&
				(contact.getFixtureA().getBody() == world.getBall() || contact.getFixtureB().getBody() == world.getBall() )){
			// TODO: Other cases where we should copy instead of reference? Here the balls vector is changing so the deathPoint keeps changing also
			this.deathPoint = new Vec2(world.getBall().getPosition());
			System.out.println("["+ id + "] DEATH AT: " + deathPoint);
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
