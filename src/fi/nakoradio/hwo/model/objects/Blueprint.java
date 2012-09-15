package fi.nakoradio.hwo.model.objects;

import org.jbox2d.common.Vec2;

import fi.nakoradio.hwo.integration.core.InputMessage;


public class Blueprint {

	private Ball ball;
	private Paddle myPaddle;
	private Paddle opponentPaddle;
	private Arena arena;
	private long tickInterval;
	
	public Blueprint(){
		
	}
	
	public Blueprint(InputMessage message){
		update(message);
	}
	
	public void init(InputMessage message){
		update(message);
	}
	
	public void update(InputMessage message){
		if(this.ball == null) this.ball = new Ball();
		if(this.myPaddle == null) this.myPaddle = new Paddle();
		if(this.opponentPaddle == null) this.opponentPaddle = new Paddle();
		if(this.arena == null) this.arena = new Arena(); 
		
		this.tickInterval = message.getConfTickInterval();
		
		this.arena.setWidth(message.getConfMaxWidth());
		this.arena.setHeight(message.getConfMaxHeight());
		
		this.ball.setPosition(new Vec2(message.getBallX(), message.getBallY()));
		this.ball.setRadius(message.getConfBallRadius());
		
		this.myPaddle.setPosition(new Vec2(0,message.getLeftPlayerY()));
		this.myPaddle.setHeight(message.getConfPaddleHeight());
		this.myPaddle.setWidth(message.getConfPaddleWidth());
		
		this.opponentPaddle.setPosition(new Vec2(message.getConfMaxWidth(),message.getRightPlayerY()));
		this.opponentPaddle.setHeight(message.getConfPaddleHeight());
		this.opponentPaddle.setWidth(message.getConfPaddleWidth());
		
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

	
	
	
	
}
