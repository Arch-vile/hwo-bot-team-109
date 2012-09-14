package fi.nakoradio.hwo.model.objects;

import org.jbox2d.common.Vec2;

public class RealityMapper {

	
	public static Vec2 modelToPhysics(Vec2 vector){
		return new Vec2(vector.x, vector.y*-1);
	}
	
	
}
