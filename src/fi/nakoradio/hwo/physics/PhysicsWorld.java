package fi.nakoradio.hwo.physics;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import fi.nakoradio.hwo.model.objects.Arena;
import fi.nakoradio.hwo.model.objects.Ball;
import fi.nakoradio.hwo.model.objects.Paddle;
import fi.nakoradio.hwo.model.objects.RealityMapper;
import fi.nakoradio.hwo.model.objects.Blueprint;

public class PhysicsWorld {
	
	World world;
	Body arena;
	Body ball;
	Body myPaddle;
	Body opponentPaddle;
	
	Fixture leftWall;
	Fixture rightWall;
	
	Blueprint blueprint;
	
	public PhysicsWorld(World world){
		this.world = world;
	}
	
	public PhysicsWorld(World world, Blueprint blueprint){
		this.world = world;
		init(blueprint);
	}
	
	// TODO: JBox2d recommends using values in range 0-10 for sizes. This could yield to performance gains and accuracy. Need to scale down here?
	public void init(Blueprint blueprint){
		this.update(blueprint);
	}
	
	public void update(Blueprint blueprint) {
		this.blueprint = blueprint;
		if(this.arena == null) createWalls(blueprint.getArena());
		if(this.ball == null) createBall(blueprint.getBall());
		if(this.myPaddle == null) this.myPaddle = createPaddle(blueprint.getMyPaddle());
		if(this.opponentPaddle == null) this.opponentPaddle = createPaddle(blueprint.getMyPaddle());
		
		this.ball.setTransform(blueprint.getBall().getPosition(),0);
		this.myPaddle.setTransform(blueprint.getMyPaddle().getPosition(), 0);
		this.opponentPaddle.setTransform(blueprint.getOpponentPaddle().getPosition(), 0);
	}
	
	
	
	private void createWalls(Arena arena) {
		//TODO: Should we have the friction and restitution here set similar to ball?
		
		BodyDef edgeBodyDef = new BodyDef();
		edgeBodyDef.position.set(0, 0);
		this.arena = getWorld().createBody(edgeBodyDef);

		PolygonShape box = new PolygonShape();
		FixtureDef fixture = new FixtureDef();
		fixture.shape = box;

		float width = arena.getWidth();
		float height = arena.getHeight();
		
		
		box.setAsEdge(new Vec2(0, 0), new Vec2(width, 0));
		this.arena.createFixture(fixture);

		box.setAsEdge(new Vec2(width, 0), new Vec2(width, height));
		this.rightWall = this.arena.createFixture(fixture);

		box.setAsEdge(new Vec2(width, height), new Vec2(0, height));
		this.arena.createFixture(fixture);

		box.setAsEdge(new Vec2(0, height), new Vec2(0, 0));
		this.leftWall = this.arena.createFixture(fixture);

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
		return arena;
	}


	public void setTableBoundaries(Body walls) {
		this.arena = walls;
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

	public Fixture getLeftWall() {
		return leftWall;
	}

	public Fixture getRightWall() {
		return rightWall;
	}

	public Blueprint getBlueprint() {
		return this.blueprint;
	}

	

}
