package fi.nakoradio.hwo.ai;	

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import fi.nakoradio.hwo.model.objects.Blueprint;
import fi.nakoradio.hwo.physics.DeathPointListener;
import fi.nakoradio.hwo.physics.PhysicsWorld;
import fi.nakoradio.hwo.physics.visualization.GameVisualizer;

public class Nostradamus {

	DeathPointListener collisions;
	PhysicsWorld world;
	private ServerClone serverClone;
	
	public Nostradamus(ServerClone serverClone) {
		this.serverClone = serverClone;
		boolean forNostradamus = true;
		// TODO: we should have a world that simulates bounces from paddles instead of plain walls
		this.world = new PhysicsWorld(new World(new Vec2(0,0),true), serverClone.getCurrentBlueprint(), forNostradamus);
		this.collisions = new DeathPointListener(world,"Nostradamus");
	}
	
	public Vec2 getNextDeathPoint(){
		System.out.println("getNextDeathPoint is:");
		this.world.setObjectPositions(this.serverClone.getCurrentBlueprint(), true);
		
		collisions.popDeathPoint();
		Vec2 ballSpeed = this.serverClone.calculateBallSpeed();
		if(ballSpeed.length() == 0){
			System.err.println("Ball is not moving. Will skip getting next death point.");
		}
		this.world.getBall().setLinearVelocity(ballSpeed);
		
		// TODO!!!!: how in earth are we going to simulate the mystic paddle bounces in our simulation? hmmm... but it should work through listener yes?
		// so add it to this model also
		while(ballSpeed.length() != 0 && collisions.getDeathPoint() == null){
			world.getPhysics().step(1f/60f, 10, 8);
		}
			
		Vec2 deathPoint = collisions.popDeathPoint();
		System.out.println("getNextDeathPoint: " + deathPoint);
		return deathPoint;
	}
	
	
}
 