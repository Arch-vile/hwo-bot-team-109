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
	
	public CollisionListener(PhysicsWorld world){
		this.world = world;
	}
	
	
	@Override
	public void beginContact(Contact contact) {
		
		WorldManifold manifold = new WorldManifold();
		contact.getWorldManifold(manifold);
		
		if(contact.getFixtureA() == world.getLeftWall() || contact.getFixtureB() == world.getLeftWall()){
			Vec2[] points = manifold.points;
			if(points.length >= 0){
				this.deathPoint = points[0];
			}
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
