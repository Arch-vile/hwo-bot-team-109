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
	
	
	// TODO: remove
	// TODO: If there is a bounce and the two positions are before and after 
	// the bounce -> we will get wrong vector. How ever the sample frequency
	// should be quick enough for this not really to be a problem.
	public Vec2 getSpeedxxx(){
		
		Vec2 speed = null;
		
		if(position == null || speedCalcPos == null || (this.position.equals(speedCalcPos))){
			speed = new Vec2(0,0);
		}else {
			Vec2 distance = this.position.sub(speedCalcPos);
			//long time = this.timestamp - this.speedCalcTimestamp;
			
			//Vec2 speed = distance.mul( 1f / ( distance.length() / time )  ); //xxxthis.position.sub(previousPosition);//xxxx.mul(1000);
			//float speedValue = ( distance.length() / time );
			distance.normalize();
			//speed = distance.mul(speedValue);
		}
		
		System.out.println(speed);
		return speed;
	}
	
	
	
	
	
}

