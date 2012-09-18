package fi.nakoradio.hwo.model.objects;

import org.jbox2d.common.Vec2;

import fi.nakoradio.hwo.integration.core.InputMessage;


public class Blueprint {

	private Ball ball;
	private Ball phantom;
	private Paddle myPaddle;
	private Paddle opponentPaddle;
	private Arena arena;
	private long tickInterval;
	private long timestamp;
	
	public Blueprint(){
		
	}
	
	public Blueprint(StateInTime state){
		update(state);
	}
	
	public void init(StateInTime state){
		update(state);
	}
	
	
	public void update(StateInTime state){
		if(this.ball == null) this.ball = new Ball();
		if(this.phantom == null) this.phantom = new Ball();
		if(this.myPaddle == null) this.myPaddle = new Paddle();
		if(this.opponentPaddle == null) this.opponentPaddle = new Paddle();
		if(this.arena == null) this.arena = new Arena(); 
		
		this.tickInterval = state.getConfTickInterval();
		this.timestamp = state.getTime();
		
		this.arena.setWidth(state.getConfMaxWidth());
		this.arena.setHeight(state.getConfMaxHeight());
		
		this.ball.setPosition(new Vec2(state.getBallX(), state.getBallY()));
		this.ball.setRadius(state.getConfBallRadius());
		
		this.phantom.setPosition(new Vec2(state.getPhantomX(), state.getPhantomY()));
		this.phantom.setRadius(state.getConfBallRadius());
		
		this.myPaddle.setLowerLeftCornerPosition(new Vec2(0,state.getLeftPlayerY()));
		this.myPaddle.setHeight(state.getConfPaddleHeight());
		this.myPaddle.setWidth(state.getConfPaddleWidth());
		
		this.opponentPaddle.setLowerLeftCornerPosition(new Vec2(state.getConfMaxWidth()-state.getConfPaddleWidth(),state.getRightPlayerY()));
		this.opponentPaddle.setHeight(state.getConfPaddleHeight());
		this.opponentPaddle.setWidth(state.getConfPaddleWidth());
		
	}
	

	public Ball getBall() {
		return ball;
	}

	
	public Paddle getMyPaddle() {
		return myPaddle;
	}

	
	public Paddle getOpponentPaddle() {
		return opponentPaddle;
	}

	public Arena getArena() {
		return arena;
	}

	public long getTickInterval() {
		return this.tickInterval;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public Ball getPhantom() {
		return phantom;
	}
	
	
}
