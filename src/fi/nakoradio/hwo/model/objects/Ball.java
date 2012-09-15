package fi.nakoradio.hwo.model.objects;

import org.jbox2d.common.Vec2;


public class Ball {

	private float radius;
	private Vec2 position;
	private Vec2 previousPosition;
	
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
		this.previousPosition = this.position;
		this.position = position;
	}
	
	// TODO: If there is a bounce and the two positions are before and after 
	// the bounce -> we will get wrong vector. How ever the sample frequency
	// should be quick enough for this not really to be a problem.
	public Vec2 getSpeed(){
		if(position == null || previousPosition == null)
			return new Vec2(0,0);
		
		Vec2 speed = this.position.sub(previousPosition).mul(1000);
		return speed;
	}
	
	
	
	
	
}
