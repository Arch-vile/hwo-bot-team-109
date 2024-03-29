package fi.nakoradio.hwo.physics;

import java.util.Vector;

import org.apache.log4j.Logger;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import fi.nakoradio.hwo.ai.ServerClone;
import fi.nakoradio.hwo.model.objects.Arena;
import fi.nakoradio.hwo.model.objects.Ball;
import fi.nakoradio.hwo.model.objects.Paddle;
import fi.nakoradio.hwo.model.objects.RealityMapper;
import fi.nakoradio.hwo.model.objects.Blueprint;
import fi.nakoradio.hwo.model.objects.StateInTime;

public class PhysicsWorld {
	
	private static Logger logger = Logger.getLogger(PhysicsWorld.class);
	
	public static int CATEGORY_BOUNDARY =	0x0001;
	public static int CATEGORY_SENSORS = 	0x0002;
	public static int CATEGORY_BALL = 		0x0004;
	public static int CATEGORY_PADDLE = 	0x0008;
	public static int CATEGORY_MARKER = 	0x0016;
	
	World world;
	Body arena;
	Body ball;
	Body myPaddle;
	Body opponentPaddle;
	Body myDeathLine;
	Body opponentDeathLine;
	//xxxBody marker1;
	//xxxBody marker2;
	Vector<Body> markers;
	
	Fixture myEnd;
	
	Blueprint blueprint;
	
	public PhysicsWorld(World world){
		this.world = world;
	}
	
	
	
	public PhysicsWorld(World world, Blueprint blueprint){
		this.world = world;
		this.createObjects(blueprint, false);
	}
	
	public PhysicsWorld(World world, Blueprint blueprint, boolean forNostradamus){
		this.world = world;
		this.createObjects(blueprint, forNostradamus);
	}
	
	// TODO: JBox2d recommends using values in range 0-10 for sizes. This could yield to performance gains and accuracy. Need to scale down here?
	public void createObjects(Blueprint blueprint, boolean forNostradamus){
		this.blueprint = blueprint;
		createWalls(blueprint.getArena());
		createBall(blueprint.getBall());
		//xxxxmarker1 = createMarker(2,2);
		//xxxmarker2 = createMarker(6,6);
		this.markers = new Vector<Body>();
		createMarker(2,2);
		createMarker(6,6);
		createMarker(2,2);
		
		this.myPaddle = createPaddle(blueprint.getMyPaddle());
		this.opponentPaddle = createPaddle(blueprint.getMyPaddle());
		
		if(forNostradamus) createMyDeathLine(blueprint);
		if(forNostradamus) createOpponentDeathLine(blueprint);
	}
	
	
	public void setObjectPositions(Blueprint blueprint) {
		
		//TODO: we really should copy the blueprint instead of reference as it could be shared between other models
		this.blueprint = blueprint;
		
		// TODO: we do not detect changes in static arena variables. etc width, height and radius
		if(this.ball != null) this.ball.setTransform(new Vec2(blueprint.getBall().getPosition()),0);
		if(this.myPaddle != null){
			
			this.myPaddle.setTransform(new Vec2(blueprint.getMyPaddle().getCenterPosition()),0);
		}
		if(this.opponentPaddle != null) this.opponentPaddle.setTransform(new Vec2(blueprint.getOpponentPaddle().getCenterPosition()), 0);
		
	}
	
	
	public Blueprint getCurrentState() {
		StateInTime state = new StateInTime();
		state.setBallX(getBall().getPosition().x);
		state.setBallY(getBall().getPosition().y);
		state.setLeftPlayerY(this.blueprint.getMyPaddle().toLowerLeftCornerPosition(getMyPaddle().getPosition()).y);
		state.setRightPlayerY(this.blueprint.getOpponentPaddle().toLowerLeftCornerPosition(getOpponentPaddle().getPosition()).y);
		
		// static values
		state.setConfBallRadius(getBlueprint().getBall().getRadius());
		state.setConfMaxHeight(getBlueprint().getArena().getHeight());
		state.setConfMaxWidth(getBlueprint().getArena().getWidth());
		state.setConfPaddleHeight(getBlueprint().getMyPaddle().getHeight());
		state.setConfPaddleWidth(getBlueprint().getMyPaddle().getWidth());
		state.setConfTickInterval(getBlueprint().getTickInterval());
		
		//TODO: what actually is the correct time to return
		state.setTime(System.currentTimeMillis());
		return new Blueprint(state);
	}

	
	
	private void createOpponentDeathLine(Blueprint blueprint) {
		
		BodyDef edgeBodyDef = new BodyDef();
		edgeBodyDef.position.set(0, 0);
		this.opponentDeathLine = getPhysics().createBody(edgeBodyDef);

		PolygonShape box = new PolygonShape();
		FixtureDef fixture = new FixtureDef();
		fixture.shape = box;
		fixture.isSensor = false;
		fixture.filter.categoryBits = CATEGORY_SENSORS;
		fixture.filter.maskBits = CATEGORY_BALL; // Only collide with Ball

		float deathLineDistanceFromRightWall = blueprint.getOpponentPaddle().getWidth();
		float deathLinex = blueprint.getArena().getWidth() - deathLineDistanceFromRightWall;
		float deathLineHeight = blueprint.getArena().getHeight();
		box.setAsEdge(new Vec2(deathLinex, 0), new Vec2(deathLinex, deathLineHeight));
		this.opponentDeathLine.createFixture(fixture);
	}

	private void createMyDeathLine(Blueprint blueprint) {
		
		BodyDef edgeBodyDef = new BodyDef();
		edgeBodyDef.position.set(0, 0);
		this.myDeathLine = getPhysics().createBody(edgeBodyDef);

		PolygonShape box = new PolygonShape();
		FixtureDef fixture = new FixtureDef();
		fixture.shape = box;
		fixture.isSensor = false;
		fixture.filter.categoryBits = CATEGORY_SENSORS;
		fixture.filter.maskBits = CATEGORY_BALL; // Only collide with Ball

		float deathLineDistanceFromLeftWall = blueprint.getMyPaddle().getWidth();
		float deathLineHeight = blueprint.getArena().getHeight();
		box.setAsEdge(new Vec2(deathLineDistanceFromLeftWall, 0), new Vec2(deathLineDistanceFromLeftWall, deathLineHeight));
		this.myDeathLine.createFixture(fixture);
	}

	private void createWalls(Arena arena) {
		//TODO: Should we have the friction and restitution here set similar to ball?
		
		if(arena == null) return;
		
		BodyDef edgeBodyDef = new BodyDef();
		edgeBodyDef.position.set(0, 0);
		this.arena = getPhysics().createBody(edgeBodyDef);

		PolygonShape box = new PolygonShape();
		FixtureDef fixture = new FixtureDef();
		fixture.shape = box;
		fixture.filter.categoryBits = CATEGORY_BOUNDARY;
		fixture.filter.maskBits = CATEGORY_BALL; // Only collide with Ball

		float width = arena.getWidth();
		float height = arena.getHeight();
		
		
		box.setAsEdge(new Vec2(0, 0), new Vec2(width, 0));
		this.arena.createFixture(fixture);

		box.setAsEdge(new Vec2(width, 0), new Vec2(width, height));
		this.arena.createFixture(fixture);

		box.setAsEdge(new Vec2(width, height), new Vec2(0, height));
		this.arena.createFixture(fixture);

		box.setAsEdge(new Vec2(0, height), new Vec2(0, 0));
		this.myEnd = this.arena.createFixture(fixture);

	}

	private void createBall(Ball ball) {
		if(ball == null) return;
		
		BodyDef def = new BodyDef();
		def.type = BodyType.DYNAMIC;
		def.bullet = true; //TODO: is needed?
		def.position.set(ball.getPosition());
		this.ball = getPhysics().createBody(def);
		
		CircleShape ballShape = new CircleShape();
		ballShape.m_radius = ball.getRadius();
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = ballShape;
		fixtureDef.density = 1f;
		fixtureDef.friction = 0f;
		fixtureDef.restitution = 1f;
		fixtureDef.filter.categoryBits = CATEGORY_BALL;
		fixtureDef.filter.maskBits = CATEGORY_BOUNDARY | CATEGORY_PADDLE | CATEGORY_SENSORS;
		this.ball.createFixture(fixtureDef);
		
	
	}
	
	private Body createPaddle(Paddle paddle) {
		if(paddle == null) return null;
		
		//TODO: Should we have the friction and restitution here set similar to ball?
		BodyDef def = new BodyDef();
		//def.position.set(paddle.getCenterPosition());
		def.type = BodyType.KINEMATIC;
		Body paddleToCreate = getPhysics().createBody(def);
	    PolygonShape groundBox = new PolygonShape();
	    groundBox.setAsBox(paddle.getWidth()/2,paddle.getHeight()/2);
	    
	    FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = groundBox;
		fixtureDef.density = 1f;
		fixtureDef.friction = 0f;
		fixtureDef.restitution = 0f;
		fixtureDef.filter.categoryBits = CATEGORY_PADDLE;
		fixtureDef.filter.maskBits = CATEGORY_BALL; // Only collide with Ball
		
		paddleToCreate.createFixture(fixtureDef);
	    return paddleToCreate;
	}

	private void createMarker(int width, int height) {
		BodyDef def = new BodyDef();
		def.type = BodyType.STATIC;
		Body marker = getPhysics().createBody(def);
	    PolygonShape groundBox = new PolygonShape();
	    groundBox.setAsBox(width,height);
	    
	    FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = groundBox;
		fixtureDef.density = 0f;
		fixtureDef.friction = 0f;
		fixtureDef.restitution = 0f;
		fixtureDef.filter.categoryBits = CATEGORY_MARKER;
		fixtureDef.filter.maskBits = 0;
	
		marker.setTransform(new Vec2(this.blueprint.getArena().getWidth()/2, this.blueprint.getArena().getHeight()/2),0);
		marker.createFixture(fixtureDef);
		this.markers.add(marker);
	}

	public Body getMarker(int index){
		return this.markers.get(index);
	}
	
	/*xxxxpublic Body getMarker1(){
		return this.marker1;
	}
	
	public Body getMarker2(){
		return this.marker2;
	}*/
	
	public World getPhysics() {
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
