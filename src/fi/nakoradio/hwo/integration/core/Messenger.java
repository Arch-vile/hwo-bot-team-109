package fi.nakoradio.hwo.integration.core;

import fi.nakoradio.hwo.util.SizedStack;

public interface Messenger  extends Runnable{

	public void start();
	public void shutdown();
	public SizedStack<InputMessage> getControlMessages();
	public SizedStack<InputMessage> getPositionMessages();
	public void sendJoinMessage(String name);
	public boolean sendPaddleMovementMessage(float paddleDirection);
	public InputMessage peekLatestPositionMessage();
	public InputMessage popLatestPositionMessage();
	public void sendJoinMessage(String botname, String dueler);
	public boolean canMessageBeSent();
}
