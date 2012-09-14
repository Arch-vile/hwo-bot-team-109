package fi.nakoradio.hwo.physics.visualization;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.testbed.framework.TestbedTest;

import fi.nakoradio.hwo.physics.PhysicsWorld;

public class Box2dTest extends TestbedTest {

	
	public World getInitializedWorld(){
		
		while(getWorld() == null){
			try { Thread.sleep(20); } catch(Exception e){};
		}
		
		return getWorld();
	}
	
	@Override
	public String getTestName() {
		return "HWO Test";
	}

	@Override
	public void initTest(boolean arg0) {
	
		
		/*
		BodyDef bodyDef = new BodyDef();
	    bodyDef.type = BodyType.DYNAMIC;
	    bodyDef.position.set(0, 20);
	    Body body = getWorld().createBody(bodyDef);
	    body.applyLinearImpulse(new Vec2(0,-10), bodyDef.position);
	    
	    CircleShape ball = new CircleShape();
	    ball.m_radius=1;
	    FixtureDef fixtureDef = new FixtureDef();
	    fixtureDef.shape = ball;
	    fixtureDef.density=1f;
	    fixtureDef.friction=0f;
	    fixtureDef.restitution = 1f;
	    body.createFixture(fixtureDef);
*/
		
	}
	
	
	

}
