package fi.nakoradio.hwo.model.objects;

import org.jbox2d.common.Vec2;

public class Paddle {

	private float width;
	private float height;
	private Vec2 position;
	
	public Paddle(){

	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public Vec2 getUpperLeftCornerPosition() {
		return position;
	}

	public void setUpperLeftCornerPosition(Vec2 position) {
		this.position = position;
	}
	
	
	public Vec2 getCenterPosition(){
		return this.getUpperLeftCornerPosition().add(new Vec2(this.width/2,this.height/2));
	}
	
	
	
}
