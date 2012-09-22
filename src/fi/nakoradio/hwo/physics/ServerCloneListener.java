package fi.nakoradio.hwo.physics;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.contacts.Contact;

import fi.nakoradio.hwo.ai.ServerClone;

public class ServerCloneListener implements ContactListener {

	private ServerClone serverClone;

	public ServerCloneListener(ServerClone clone){
		this.serverClone = clone;
	}

	@Override
	public void beginContact(Contact contact) {
		
		Body opponentPaddle = serverClone.getSimulation().getOpponentPaddle();
		Body ball = serverClone.getSimulation().getBall();
		Body fixtureA = contact.getFixtureA().getBody();
		Body fixtureB = contact.getFixtureB().getBody();
		
		
		
		if(		(fixtureA == opponentPaddle || fixtureB == opponentPaddle ) && (fixtureA == ball || fixtureB == ball )){
			Vec2 ballSpeed = new Vec2(ball.getLinearVelocity());
			Vec2 opponentPaddleSpeed = new Vec2(serverClone.getOpponentPaddleSpeed());
			ball.setLinearVelocity( ballSpeed.add(opponentPaddleSpeed));
		}
		
		
	}

	@Override
	public void endContact(Contact arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact arg0, ContactImpulse arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact arg0, Manifold arg1) {
		// TODO Auto-generated method stub
		
	}
	
	

}
