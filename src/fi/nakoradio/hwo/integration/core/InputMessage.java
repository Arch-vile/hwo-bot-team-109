package fi.nakoradio.hwo.integration.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class InputMessage {

	private static final String MESSAGETYPE_JOINED = "joined";
	private static final String MESSAGETYPE_GAMESTARTED = "gameStarted";
	private static final String MESSAGETYPE_GAMEISOVER = "gameIsOver";
	private static final String MESSAGETYPE_GAMEISON = "gameIsOn";
	
	private String message;
	JsonElement root;
	
	public InputMessage(String jsonString) throws BadInputMessageException {
		this.message = jsonString;
		
		try {
			this.root = new JsonParser().parse(this.message);
			
			getMessageType();
			
			if(isGameIsOnMessage()){
				getTime();
				getLeftPlayerY();
				getLeftPlayerName();
				getRightPlayerY();
				getRightPlayerName();
				getBallX();
				getBallY();
				getConfMaxWidth();
				getConfMaxHeight();
				getConfPaddleHeight();
				getConfPaddleWidth();
				getConfBallRadius();
				getConfTickInterval();
			}else if(isJoinedMessage()){
				getVisualizeUrl();
			}else if(isGameStartedMessage()){
				getPlayer1();
				getPlayer2();
			} else if(isGameOverMessage()){
				getWinner();
			} else
				throw new BadInputMessageException("Unknown message type detected");
			
		}catch(Exception e){
			throw new BadInputMessageException("For input ["+jsonString+"]",e);
		}
	}
	
	public String getMessage(){
		return this.message;
	}
	
	public String getWinner() {
		return root.getAsJsonObject().get("data").getAsString();
	}

	public boolean isGameOverMessage() {
		return getMessageType().equals(MESSAGETYPE_GAMEISOVER);
	}
	
	public boolean isGameStartedMessage() {
		return getMessageType().equals(MESSAGETYPE_GAMESTARTED);
	}

	public boolean isJoinedMessage() {
		return getMessageType().equals(MESSAGETYPE_JOINED);
	}
	
	public boolean isGameIsOnMessage() {
		return getMessageType().equals(MESSAGETYPE_GAMEISON);
	}

	
	public String getPlayer2() {
		return root.getAsJsonObject().get("data").getAsJsonArray().get(1).getAsString();
		
	}

	public String getPlayer1() {
		return root.getAsJsonObject().get("data").getAsJsonArray().get(0).getAsString();
	}

	public String getVisualizeUrl() {
		return root.getAsJsonObject().get("data").getAsString();
	}

	public long getConfTickInterval() {
		return root.getAsJsonObject().get("data").getAsJsonObject().get("conf").getAsJsonObject().get("tickInterval").getAsLong();
	}

	public long getConfBallRadius() {
		return root.getAsJsonObject().get("data").getAsJsonObject().get("conf").getAsJsonObject().get("ballRadius").getAsLong();
	}

	public long getConfPaddleWidth() {
		return root.getAsJsonObject().get("data").getAsJsonObject().get("conf").getAsJsonObject().get("paddleWidth").getAsLong();
	}

	public long getConfPaddleHeight() {
		return root.getAsJsonObject().get("data").getAsJsonObject().get("conf").getAsJsonObject().get("paddleHeight").getAsLong();
	}

	public long getConfMaxHeight() {
		return root.getAsJsonObject().get("data").getAsJsonObject().get("conf").getAsJsonObject().get("maxHeight").getAsLong();
	}

	public long getConfMaxWidth() {
		return root.getAsJsonObject().get("data").getAsJsonObject().get("conf").getAsJsonObject().get("maxWidth").getAsLong();
	}

	public double getBallY() {
		return root.getAsJsonObject().get("data").getAsJsonObject().get("ball").getAsJsonObject().get("pos").getAsJsonObject().get("y").getAsDouble();
	}

	public double getBallX() {
		return root.getAsJsonObject().get("data").getAsJsonObject().get("ball").getAsJsonObject().get("pos").getAsJsonObject().get("x").getAsDouble();
	}

	public String getRightPlayerName() {
		return root.getAsJsonObject().get("data").getAsJsonObject().get("right").getAsJsonObject().get("playerName").getAsString();
	}

	public double getRightPlayerY() {
		return root.getAsJsonObject().get("data").getAsJsonObject().get("right").getAsJsonObject().get("y").getAsDouble();
	}

	public String getLeftPlayerName() {
		return root.getAsJsonObject().get("data").getAsJsonObject().get("left").getAsJsonObject().get("playerName").getAsString();
		
	}

	public double getLeftPlayerY() {
		return root.getAsJsonObject().get("data").getAsJsonObject().get("left").getAsJsonObject().get("y").getAsDouble();
	}

	public String getMessageType(){
		return root.getAsJsonObject().get("msgType").getAsString();
	}
	
	public long getTime(){
		return root.getAsJsonObject().get("data").getAsJsonObject().get("time").getAsLong();
	}
	
	
	public static void main(String[] args){
		
		
		// Empty message test
		try { new InputMessage(""); }catch(Exception e){ System.err.println(e); }
		
		// random content message test
		try { new InputMessage("dksa kdk: { sad\" : ddasd }}"); }catch(Exception e){ System.err.println(e); }
		
		// double msgType field
		try { new InputMessage("{ msgType: \"foobar\",msgType: \"karhu\" }"); }catch(Exception e){ System.err.println(e); }
		
		//TODO: add more checks for message format
	}
	
	
	
	
}
