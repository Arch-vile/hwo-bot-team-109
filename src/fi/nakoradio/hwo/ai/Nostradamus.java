package fi.nakoradio.hwo.ai;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import fi.nakoradio.hwo.model.objects.Blueprint;
import fi.nakoradio.hwo.physics.CollisionListener;
import fi.nakoradio.hwo.physics.PhysicsWorld;
import fi.nakoradio.hwo.physics.visualization.GameVisualizer;

public class Nostradamus {

	CollisionListener collisions;
	PhysicsWorld world;
	
	public Nostradamus() {
		this.world = new PhysicsWorld(new World(new Vec2(0,0),true));
		this.collisions = new CollisionListener(world,"Nostradamus");
		this.world.getPhysics().setContactListener(collisions);
	}
	
	public void update(Blueprint blueprint, boolean staticWorld){
		this.world.update(blueprint, staticWorld);
		collisions.popDeathPoint();
	}
	
	public void set(Blueprint blueprint, boolean staticWorld){
		this.world = new PhysicsWorld(new World(new Vec2(0,0),true), blueprint);
		this.collisions = new CollisionListener(world,"Nostradamus");
		this.world.getPhysics().setContactListener(collisions);
		this.world.update(blueprint, staticWorld);
	}

	public Vec2 getNextDeathPoint(){
		
		//GameVisualizer vis = new GameVisualizer();
		//vis.start();
		//vis.update(world.getBlueprint());
		
		//try { Thread.sleep(100000); } catch(Exception e){}
		
		System.out.println("Started death search");
		while(collisions.getDeathPoint() == null){
			world.getPhysics().step(1f/60f, 10, 8);
			//try { Thread.sleep(1000/60); }catch(Exception e){ }
			//vis.update(world.getCurrentState());
			
			//System.out.println(world.getBall().getPosition());
				
		}
		System.out.println("ENDED death search");
		
		Vec2 deathPoint = collisions.popDeathPoint();
		return deathPoint;
	}
	
	public PhysicsWorld getWorld(){
		return world;
	}
	
}
