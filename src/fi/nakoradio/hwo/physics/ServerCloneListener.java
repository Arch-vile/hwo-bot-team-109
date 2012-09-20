package fi.nakoradio.hwo.physics;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

import fi.nakoradio.hwo.ai.ServerClone;

public class ServerCloneListener implements ContactListener {

	private ServerClone serverClone;

	public ServerCloneListener(ServerClone clone){
		this.serverClone = clone;
	}

	@Override
	public void beginContact(Contact contact) {
		System.out.println("Contact " + this.getClass().getName());
		
		if(		(contact.getFixtureA().getBody() == serverClone.getSimulation().getOpponentPaddle() || contact.getFixtureB().getBody() == serverClone.getSimulation().getOpponentPaddle() ) &&
				(contact.getFixtureA().getBody() == serverClone.getSimulation().getPhantom() || contact.getFixtureB().getBody() == serverClone.getSimulation().getPhantom() )){
			System.out.println("Hit opponent " + System.currentTimeMillis());
			serverClone.getSimulation().getPhantom().setLinearVelocity( serverClone.getSimulation().getPhantom().getLinearVelocity().add(serverClone.getOpponentPaddleSpeed()));
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
