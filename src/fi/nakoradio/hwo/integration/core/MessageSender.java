package fi.nakoradio.hwo.integration.core;

public class MessageSender {

	private BotSocket socket;
	
	public MessageSender(BotSocket socket){
		this.socket = socket;
	}
	
	public void sendJoinMessage(){
		socket.getOut().println("{\"msgType\":\"join\",\"data\":\"RandomName\"}");
	}

	
	public void sendPaddleMovementMessage(float paddleDirection) {
		socket.getOut().println("{\"msgType\":\"changeDir\",\"data\":"+paddleDirection+"}");
		
	}
	
}
