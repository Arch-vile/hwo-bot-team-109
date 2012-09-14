package fi.nakoradio.hwo.physics;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;

public class CollisionListener implements ContactListener {

	PhysicsWorld world;
	
	
	public CollisionListener(PhysicsWorld world){
		this.world = world;
	}
	
	
	@Override
	public void beginContact(Contact contact) {
		
		WorldManifold manifold = new WorldManifold();
		contact.getWorldManifold(manifold);
		
		Vec2[] points = manifold.points;
		if(points.length >= 0){
			System.out.println(points[0].x + "," + points[0].y);
		}
		
		if(contact.getFixtureA().getBody() == world.getMyPaddle() || contact.getFixtureB().getBody() == world.getMyPaddle())
			System.out.println("MY PADDLE");
		
		
		
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

}
