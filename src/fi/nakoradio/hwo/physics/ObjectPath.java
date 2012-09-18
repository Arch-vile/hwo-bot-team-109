package fi.nakoradio.hwo.physics;

import java.util.Vector;

import org.jbox2d.common.Vec2;

public class ObjectPath {

	private Vector<PointInTime> points;
	
	public ObjectPath(){
		this.points = new Vector<PointInTime>();
	}

	public void clear(){
		this.points.removeAllElements();
	}
	
	public void push(Vec2 position, long timestamp){
		this.points.add(new PointInTime(position, timestamp));
	}
	
	public PointInTime peekFirst(){
		if(points.size() == 0) return null;
		return points.elementAt(0);
	}
	
	public PointInTime popFirst(){
		if(points.size() == 0) return null;
		return points.remove(0);
	}
	
	public int size(){
		return points.size();
	}
}
