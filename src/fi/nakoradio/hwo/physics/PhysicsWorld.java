package fi.nakoradio.hwo.physics;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import fi.nakoradio.hwo.model.objects.Ball;
import fi.nakoradio.hwo.model.objects.Paddle;
import fi.nakoradio.hwo.model.objects.RealityMapper;
import fi.nakoradio.hwo.model.objects.SimpleWorld;

public class PhysicsWorld {
	
	World world;
	Body walls;
	Body ball;
	Body myPaddle;
	Body opponentPaddle;
	
	
	public PhysicsWorld(World world){
		this.world = world;
	}
	
	public void update(SimpleWorld simpleWorld) {
		if(this.walls == null) createWalls(simpleWorld.getWidth(), simpleWorld.getHeight());
		if(this.ball == null) createBall(simpleWorld.getBall());
		if(this.myPaddle == null) this.myPaddle = createPaddle(simpleWorld.getMyPaddle());
		if(this.opponentPaddle == null) this.opponentPaddle = createPaddle(simpleWorld.getMyPaddle());
		
		this.ball.setTransform(simpleWorld.getBall().getPosition(),0);
		this.myPaddle.setTransform(simpleWorld.getMyPaddle().getPosition(), 0);
		this.opponentPaddle.setTransform(simpleWorld.getOpponentPaddle().getPosition(), 0);
	}
	
	
	
	private void createWalls(float width, float height) {
		//TODO: Should we have the friction and restitution here set similar to ball?
		BodyDef edgeBodyDef = new BodyDef();
		edgeBodyDef.position.set(0, 0);
		this.walls = getWorld().createBody(edgeBodyDef);

		PolygonShape box = new PolygonShape();
		FixtureDef fixture = new FixtureDef();
		fixture.shape = box;

		box.setAsEdge(new Vec2(0, 0), new Vec2(width, 0));
		this.walls.createFixture(fixture);

		box.setAsEdge(new Vec2(width, 0), new Vec2(width, height));
		this.walls.createFixture(fixture);

		box.setAsEdge(new Vec2(width, height), new Vec2(0, height));
		this.walls.createFixture(fixture);

		box.setAsEdge(new Vec2(0, height), new Vec2(0, 0));
		this.walls.createFixture(fixture);

	}

	private void createBall(Ball ball) {
		BodyDef def = new BodyDef();
		def.type = BodyType.DYNAMIC;
		def.position.set(ball.getPosition());
		this.ball = getWorld().createBody(def);
		
		CircleShape ballShape = new CircleShape();
		ballShape.m_radius = ball.getRadius();
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = ballShape;
		fixtureDef.density = 1f;
		fixtureDef.friction = 0f;
		fixtureDef.restitution = 1f;
		this.ball.createFixture(fixtureDef);
		
		this.ball.applyLinearImpulse(new Vec2(5000,7000), this.ball.getPosition());
	}
	
	private Body createPaddle(Paddle paddle) {
		//TODO: Should we have the friction and restitution here set similar to ball?
		BodyDef def = new BodyDef();
		def.position.set(paddle.getPosition());
		Body paddleToCreate = getWorld().createBody(def);
	    PolygonShape groundBox = new PolygonShape();
	    groundBox.setAsBox(paddle.getWidth(),paddle.getHeight());
	    paddleToCreate.createFixture(groundBox, 1f);
	    
	    
	    return paddleToCreate;
	}


	public World getWorld() {
		return world;
	}


	public void setWorld(World world) {
		this.world = world;
	}


	public Body getTableBoundaries() {
		return walls;
	}


	public void setTableBoundaries(Body walls) {
		this.walls = walls;
	}


	public Body getBall() {
		return ball;
	}


	public void setBall(Body ball) {
		this.ball = ball;
	}


	public Body getMyPaddle() {
		return myPaddle;
	}


	public void setMyPaddle(Body myPaddle) {
		this.myPaddle = myPaddle;
	}


	public Body getOpponentPaddle() {
		return opponentPaddle;
	}


	public void setOpponentPaddle(Body opponentPaddle) {
		this.opponentPaddle = opponentPaddle;
	}


	
	
	

}
