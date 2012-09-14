package fi.nakoradio.hwo.model.objects;

import org.jbox2d.common.Vec2;

import fi.nakoradio.hwo.integration.core.InputMessage;


public class SimpleWorld {

	private float width;
	private float height;
	private Ball ball;
	private Paddle myPaddle;
	private Paddle opponentPaddle;
	
	public SimpleWorld(){
		this.ball = new Ball();
		this.myPaddle = new Paddle();
		this.opponentPaddle = new Paddle();
	}
	
	public void update(InputMessage message){
		
		this.width = message.getConfMaxWidth();
		this.height = message.getConfMaxHeight();
		
		this.ball.setPosition(new Vec2(message.getBallX(), message.getBallY()));
		this.ball.setRadius(message.getConfBallRadius());
		
		this.myPaddle.setPosition(new Vec2(0,message.getLeftPlayerY()));
		this.myPaddle.setHeight(message.getConfPaddleHeight());
		this.myPaddle.setWidth(message.getConfPaddleWidth());
		
		this.opponentPaddle.setPosition(new Vec2(message.getConfMaxWidth(),message.getRightPlayerY()));
		this.opponentPaddle.setHeight(message.getConfPaddleHeight());
		this.opponentPaddle.setWidth(message.getConfPaddleWidth());
		
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

	public Ball getBall() {
		return ball;
	}

	public void setBall(Ball ball) {
		this.ball = ball;
	}

	public Paddle getMyPaddle() {
		return myPaddle;
	}

	public void setMyPaddle(Paddle myPaddle) {
		this.myPaddle = myPaddle;
	}

	public Paddle getOpponentPaddle() {
		return opponentPaddle;
	}

	public void setOpponentPaddle(Paddle opponentPaddle) {
		this.opponentPaddle = opponentPaddle;
	}
	
	
	
}
