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
	
	public Nostradamus(Blueprint blueprint) {
		boolean forNostradamus = true;
		this.world = new PhysicsWorld(new World(new Vec2(0,0),true), blueprint, forNostradamus);
		this.collisions = new DeathPointListener(world,"Nostradamus");
	}
	
	public void update(Blueprint blueprint){
		this.world.setObjectPositions(blueprint,false);
		
		if("d".length() < 2){
			//this.world.setBallSpeed(blueprint.getBall().getSpeed());
			System.err.println("todo in Nostradamus");
			System.exit(1);
		}
		collisions.popDeathPoint();
	}
	
	

	public Vec2 getNextDeathPoint(){
		
		//GameVisualizer vis = new GameVisualizer();
		//vis.start();
		//vis.update(world.getBlueprint());
		
		//try { Thread.sleep(100000); } catch(Exception e){}
		
		collisions.popDeathPoint();
		boolean ballIsMoving = (this.world.getBall().getLinearVelocity().length() != 0);
		if(!ballIsMoving){
			System.err.println("Ball is not moving. Will skip getting next death point.");
		}
		
		while(ballIsMoving && collisions.getDeathPoint() == null){
			world.getPhysics().step(1f/60f, 10, 8);
			//try { Thread.sleep(1000/60); }catch(Exception e){ }
			//vis.update(world.getCurrentState());
			
			//System.out.println(world.getBall().getPosition());
				
		}
		
		Vec2 deathPoint = collisions.popDeathPoint();
		return deathPoint;
	}
	
	public PhysicsWorld getWorld(){
		return world;
	}

	public void forward(long milliseconds) {
		int iterations = (int)(milliseconds / ((1f/60f)*1000));
		for(int i = 0; i < iterations; i++){
			world.getPhysics().step(1f/60f, 10, 8);
		}
	}
	
}
