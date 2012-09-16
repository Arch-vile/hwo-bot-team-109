package fi.nakoradio.hwo.physics;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

public class PhysicsUtil {
	
	
	public static void alterY(Body body, Vec2 newY){
		body.setTransform(new Vec2(body.getPosition().x, newY.y), 0);
		
	}

}
