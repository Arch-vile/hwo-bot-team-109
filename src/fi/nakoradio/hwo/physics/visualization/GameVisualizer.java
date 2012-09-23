package fi.nakoradio.hwo.physics.visualization;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import fi.nakoradio.hwo.model.objects.Blueprint;
import fi.nakoradio.hwo.physics.PhysicsWorld;

public class GameVisualizer {

	Box2dTest test;
	Box2dTestbed tester;
	PhysicsWorld simulation;
	World world;
	
	public GameVisualizer() {
		test = new Box2dTest();
		tester = new Box2dTestbed();
	}
	
	public void start(){
		tester.startSimulation(test);
		World testWorld = test.getInitializedWorld();
		this.world = testWorld;
		testWorld.setGravity(new Vec2(0,0));
		test.setCamera(new Vec2(291.4259f,183.91339f), 0.46997482f);
	}
	
	public void update(Blueprint blueprint){
		if(this.simulation == null)
			this.simulation = new PhysicsWorld(this.world, blueprint);
		this.simulation.setObjectPositions(blueprint);
	}

	public PhysicsWorld getWorld(){
		return this.simulation;
	}

	public void plotDeathPoints(Vec2[] deathPoints) {
		if(getWorld() == null) return;
		if(deathPoints[0] != null)
			getWorld().getMarker(0).setTransform(new Vec2(deathPoints[0]), 0);
		if(deathPoints[1] != null)
			getWorld().getMarker(1).setTransform(new Vec2(deathPoints[1]), 0);
	}
	
	public void plotMarker(int index, Vec2 pos){
		if(this.simulation.getMarker(index) != null){
			this.simulation.getMarker(index).setTransform(new Vec2(pos), 0f);
		}
	}
	
	
}
