package fi.nakoradio.hwo.util;

import java.text.DecimalFormat;

import org.jbox2d.common.Vec2;

public class Utils {
	
	private static DecimalFormat df = new DecimalFormat("#.####");

	public static String toString(Vec2[] points){
		
		StringBuffer buf = new StringBuffer();
		
		for(Vec2 point : points){
			buf.append(point);
			buf.append(",");
		}
		
		return buf.toString();
	}

	public static String toStringFormat(Vec2 t) {
		return "(" + df.format(t.x) + "," +  df.format(t.y) + ")";
	}
	
	
}
