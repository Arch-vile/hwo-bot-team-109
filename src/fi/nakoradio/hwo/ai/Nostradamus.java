package fi.nakoradio.hwo.ai;	

import org.apache.log4j.Logger;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import fi.nakoradio.hwo.main.HWOBot;
import fi.nakoradio.hwo.model.objects.Blueprint;
import fi.nakoradio.hwo.physics.DeathPointListener;
import fi.nakoradio.hwo.physics.PhysicsWorld;
import fi.nakoradio.hwo.physics.visualization.GameVisualizer;
import fi.nakoradio.hwo.util.Utils;

public class Nostradamus {

	private static Logger logger = Logger.getLogger(Nostradamus.class);
	
	
	DeathPointListener collisions;
	public PhysicsWorld world;
	private ServerClone serverClone;
	public GameVisualizer tempvisu;
	
	float templ = 0;
	
	public Nostradamus(ServerClone serverClone) {
		this.serverClone = serverClone;
		boolean forNostradamus = true;
		// TODO: we should have a world that simulates bounces from paddles instead of plain walls
		this.world = new PhysicsWorld(new World(new Vec2(0,0),true), serverClone.getCurrentBlueprint(), forNostradamus);
		this.collisions = new DeathPointListener(world,serverClone);
	}
	
	public Vec2[] getNextDeathPoints(int amount){
		logger.trace("getNextDeathPoints() - START");
		
		Vec2[] deathPoints = new Vec2[amount];
		this.world.setObjectPositions(this.serverClone.getSimulation().getCurrentState());
		
		collisions.popDeathPoint();
		
		Vec2 ballSpeed = this.serverClone.getBallSpeed();
	
		// TODO: this is required as the ball bounces of the walls it could have no or very slow sleep at certain time during the bounce. 
		// Once the speed is set to very slow the ball will not bounce and we have eternal looping.
		// Other solution would be to reqularly update the serverclone bluprint during serverSynchronizer but we should be ok with these constraints. 
		// Just make sure that these are not excepted speeds at any point. Having minimum sizes for speed vector also helps to filter out the abnormal 
		// speed vectors during bounces
		
		// dont add check for y-coordinate as then we will miss direct balls!
		if(ballSpeed.length() == 0 || Math.abs(ballSpeed.x) < 6){
			//System.err.println("Ball is not moving. Will skip getting death points.");
			logger.trace("getNextDeathPoints() - END - too slow ball " + ballSpeed);
			return deathPoints;
		}
		
		this.world.getBall().setLinearVelocity(new Vec2(ballSpeed));
		
		// TODO!!!!: how in earth are we going to simulate the mystic paddle bounces in our simulation? hmmm... but it should work through listener yes?
		// so add it to this model also
		for(int i = 0; i < amount; i++){
		
			int counter = 0;
			while(collisions.getDeathPoint() == null){
				counter++;
				// TODO: something smarter... but this is a safety measure
				if(counter > 1000){ 
					/*System.err.println("Death point search seems to be looping. Exiting");*/ 
					logger.trace("getNextDeathPoints() - END - too many iterations");
					return deathPoints; 
				}
				world.getPhysics().step(1f/60f, 10, 8);
			}
			
			deathPoints[i] = collisions.popDeathPoint();
		}
		
		logger.trace("getNextDeathPoints() - END - normal end ");
		return deathPoints;
	}
	
}
 