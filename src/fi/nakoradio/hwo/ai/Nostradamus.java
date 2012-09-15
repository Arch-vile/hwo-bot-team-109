package fi.nakoradio.hwo.ai;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import fi.nakoradio.hwo.model.objects.Blueprint;
import fi.nakoradio.hwo.physics.CollisionListener;
import fi.nakoradio.hwo.physics.PhysicsWorld;

public class Nostradamus {

	CollisionListener collisions;
	PhysicsWorld world;
	
	public Nostradamus() {
		this.world = new PhysicsWorld(new World(new Vec2(0,0),true));
		this.collisions = new CollisionListener(world,"Nostradamus");
		this.world.getPhysics().setContactListener(collisions);
	}
	
	public void update(Blueprint blueprint){
		this.world.update(blueprint, false);
		collisions.popDeathPoint();
	}

	public Vec2 getNextDeathPoint(){
		
		while(collisions.getDeathPoint() == null){
			world.getPhysics().step(1f/60f, 10, 8);
		}
		
		Vec2 deathPoint = collisions.popDeathPoint();
		return deathPoint;
	}
	
	public PhysicsWorld getWorld(){
		return world;
	}
	
}
