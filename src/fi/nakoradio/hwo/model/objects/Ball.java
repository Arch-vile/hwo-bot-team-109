package fi.nakoradio.hwo.model.objects;

import org.jbox2d.common.Vec2;


public class Ball {

	
	
	private float radius;
	private Vec2 position;
	private Vec2 speedCalcPos;
	private long speedCalcTimestamp;
	
	public Ball(){

	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public Vec2 getPosition() {
		return position;
	}

	public void setPosition(Vec2 position) {
		//this.speedCalcPos = this.position;
		this.position = position;
	}
	
	
	
}

