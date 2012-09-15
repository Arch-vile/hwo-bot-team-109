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
	
	public static int CATEGORY_BOUNDARY =	0x0001;
	public static int CATEGORY_SENSORS = 	0x0002;
	public static int CATEGORY_BALL = 		0x0004;
	public static int CATEGORY_PADDLE = 	0x0008;
	
	
	World world;
	Body arena;
	Body ball;
	Body myPaddle;
	Body opponentPaddle;
	Body myDeathLine;
	Body opponentDeathLine;
	
	
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
		if(this.myDeathLine == null) createMyDeathLine(blueprint);
		//if(this.opponentDeathLine == null) this.opponentDeathLine = createOpponentDeathLine(blueprint);
		
		// TODO: we do not detect changes in static arena variables. etc width, height and radius
		this.ball.setTransform(blueprint.getBall().getPosition(),0);
		this.myPaddle.setTransform(blueprint.getMyPaddle().getCenterPosition(), 0);
		this.opponentPaddle.setTransform(blueprint.getOpponentPaddle().getCenterPosition(), 0);
	}
	
	
	
	private Body createOpponentDeathLine(Blueprint blueprint) {
		return null;
	}

	private void createMyDeathLine(Blueprint blueprint) {
		BodyDef edgeBodyDef = new BodyDef();
		edgeBodyDef.position.set(0, 0);
		this.myDeathLine = getWorld().createBody(edgeBodyDef);

		PolygonShape box = new PolygonShape();
		FixtureDef fixture = new FixtureDef();
		fixture.shape = box;
		fixture.isSensor = true;
		fixture.filter.categoryBits = CATEGORY_SENSORS;
		fixture.filter.maskBits = CATEGORY_BALL; // Only collide with Ball

		float deathLineDistanceFromLeftWall = blueprint.getMyPaddle().getWidth();
		float deathLineHeight = blueprint.getArena().getHeight();
		box.setAsEdge(new Vec2(deathLineDistanceFromLeftWall, 0), new Vec2(deathLineDistanceFromLeftWall, deathLineHeight));
		this.myDeathLine.createFixture(fixture);
	}

	private void createWalls(Arena arena) {
		//TODO: Should we have the friction and restitution here set similar to ball?
		
		BodyDef edgeBodyDef = new BodyDef();
		edgeBodyDef.position.set(0, 0);
		this.arena = getWorld().createBody(edgeBodyDef);

		PolygonShape box = new PolygonShape();
		FixtureDef fixture = new FixtureDef();
		fixture.shape = box;
		fixture.filter.categoryBits = CATEGORY_BOUNDARY;

		float width = arena.getWidth();
		float height = arena.getHeight();
		
		
		box.setAsEdge(new Vec2(0, 0), new Vec2(width, 0));
		this.arena.createFixture(fixture);

		box.setAsEdge(new Vec2(width, 0), new Vec2(width, height));
		this.arena.createFixture(fixture);

		box.setAsEdge(new Vec2(width, height), new Vec2(0, height));
		this.arena.createFixture(fixture);

		box.setAsEdge(new Vec2(0, height), new Vec2(0, 0));
		this.arena.createFixture(fixture);

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
		fixtureDef.filter.categoryBits = CATEGORY_BALL;
		this.ball.createFixture(fixtureDef);
		
		this.ball.applyLinearImpulse(new Vec2(5000,7000), this.ball.getPosition());
	}
	
	private Body createPaddle(Paddle paddle) {
		//TODO: Should we have the friction and restitution here set similar to ball?
		BodyDef def = new BodyDef();
		def.position.set(paddle.getCenterPosition());
		Body paddleToCreate = getWorld().createBody(def);
	    PolygonShape groundBox = new PolygonShape();
	    groundBox.setAsBox(paddle.getWidth()/2,paddle.getHeight()/2);
	    
	    FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = groundBox;
		fixtureDef.density = 1f;
		// TODO: Should we prevent paddle from colliding with the boundary
		fixtureDef.filter.categoryBits = CATEGORY_PADDLE;
	    
		paddleToCreate.createFixture(fixtureDef);
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

	

	public Blueprint getBlueprint() {
		return this.blueprint;
	}

	public Body getMyDeathLine() {
		return myDeathLine;
	}

	public Body getOpponentDeathLine() {
		return opponentDeathLine;
	}

	

}
