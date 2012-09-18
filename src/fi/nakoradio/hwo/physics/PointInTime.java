package fi.nakoradio.hwo.physics;

import org.jbox2d.common.Vec2;

public class PointInTime {
	
	private Vec2 position;
	private long timestamp;
	
	public PointInTime(Vec2 position, long timeStamp){
		this.position = position;
		this.timestamp = timeStamp;
	}
	
	public Vec2 getPosition() {
		return position;
	}
	public void setPosition(Vec2 position) {
		this.position = position;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	

}
