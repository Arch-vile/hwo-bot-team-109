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
		this.world = new PhysicsWorld(new World(new Vec2(0,0),true), blueprint, true);
		this.collisions = new DeathPointListener(world,"Nostradamus");
	}
	
	public void update(Blueprint blueprint){
		this.world.setObjectPositions(blueprint);
		this.world.setBallSpeed(blueprint.getBall().getSpeed());
		collisions.popDeathPoint();
	}
	
	

	public Vec2 getNextDeathPoint(){
		
		//GameVisualizer vis = new GameVisualizer();
		//vis.start();
		//vis.update(world.getBlueprint());
		
		//try { Thread.sleep(100000); } catch(Exception e){}
		
		while(collisions.getDeathPoint() == null){
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
	
}
