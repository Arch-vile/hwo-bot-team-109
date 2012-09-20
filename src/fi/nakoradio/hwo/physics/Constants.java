package fi.nakoradio.hwo.physics;

public class Constants {

	// Maximum number of messages in time unit
	public static final long OUTPUT_MESSAGE_COUNT_LIMIT = 10;
	
	// The ouput message speed limit. Max no of messages in this time frame (ms).
	public static final long OUTPUT_MESSAGE_SPEED_LIMIT = 1000;
	
	// How many milliseconds we decrease the actual output speed limit per message.
	public static final long OUTPUT_MESSAGE_SPEED_SAFE_FACTOR = 8;
	
	
	
	
	public static float MYSTICAL_PADDLE_BOUNCE_SPEED_MODIFIER = 0.2f;

}
