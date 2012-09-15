package fi.nakoradio.hwo.integration.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import fi.nakoradio.hwo.model.objects.StateInTime;

public class InputMessage {

	private static final String MESSAGETYPE_JOINED = "joined";
	private static final String MESSAGETYPE_GAMESTARTED = "gameStarted";
	private static final String MESSAGETYPE_GAMEISOVER = "gameIsOver";
	private static final String MESSAGETYPE_GAMEISON = "gameIsOn";
	
	private String message;
	JsonElement root;
	
	public InputMessage(String jsonString) throws BadInputMessageException {
		this.message = jsonString;
		
		String method = "";
		try {
			this.root = new JsonParser().parse(this.message);
			
			method = ""; 
			getMessageType();
			
			
			if(isGameIsOnMessage()){
				method = "getTime"; getTime();
				method = "getLeftPlayerY"; getLeftPlayerY();
				method = "getLeftPlayerName"; getLeftPlayerName();
				method = "getRightPlayerY"; getRightPlayerY();
				method = "getRightPlayerName"; getRightPlayerName();
				method = "getBallX"; getBallX();
				method = "getBallY"; getBallY();
				method = "getConfMaxWidth"; getConfMaxWidth();
				method = "getConfMaxHeight"; getConfMaxHeight();
				method = "getConfPaddleHeight"; getConfPaddleHeight();
				method = "getConfPaddleWidth"; getConfPaddleWidth();
				method = "getConfBallRadius"; getConfBallRadius();
				method = "getConfTickInterval"; getConfTickInterval();
			}else if(isJoinedMessage()){
				method = "getVisualizeUrl"; getVisualizeUrl();
			}else if(isGameStartedMessage()){
				method = "getPlayer1"; getPlayer1();
				method = "getPlayer2"; getPlayer2();
			} else if(isGameOverMessage()){
				method = "getWinner"; getWinner();
			} else
				throw new BadInputMessageException("Unknown message type detected");
			
		}catch(Exception e){
			throw new BadInputMessageException("For input ["+jsonString+"] method ["+method+"()]",e);
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

	public float getConfBallRadius() {
		return root.getAsJsonObject().get("data").getAsJsonObject().get("conf").getAsJsonObject().get("ballRadius").getAsFloat();
	}

	public float getConfPaddleWidth() {
		return root.getAsJsonObject().get("data").getAsJsonObject().get("conf").getAsJsonObject().get("paddleWidth").getAsFloat();
	}

	public float getConfPaddleHeight() {
		return root.getAsJsonObject().get("data").getAsJsonObject().get("conf").getAsJsonObject().get("paddleHeight").getAsFloat();
	}

	public float getConfMaxHeight() {
		return root.getAsJsonObject().get("data").getAsJsonObject().get("conf").getAsJsonObject().get("maxHeight").getAsFloat();
	}

	public float getConfMaxWidth() {
		return root.getAsJsonObject().get("data").getAsJsonObject().get("conf").getAsJsonObject().get("maxWidth").getAsFloat();
	}

	public float getBallY() {
		return root.getAsJsonObject().get("data").getAsJsonObject().get("ball").getAsJsonObject().get("pos").getAsJsonObject().get("y").getAsFloat();
	}

	public float getBallX() {
		return root.getAsJsonObject().get("data").getAsJsonObject().get("ball").getAsJsonObject().get("pos").getAsJsonObject().get("x").getAsFloat();
	}

	public String getRightPlayerName() {
		return root.getAsJsonObject().get("data").getAsJsonObject().get("right").getAsJsonObject().get("playerName").getAsString();
	}

	public float getRightPlayerY() {
		return root.getAsJsonObject().get("data").getAsJsonObject().get("right").getAsJsonObject().get("y").getAsFloat();
	}

	public String getLeftPlayerName() {
		return root.getAsJsonObject().get("data").getAsJsonObject().get("left").getAsJsonObject().get("playerName").getAsString();
		
	}

	public float getLeftPlayerY() {
		return root.getAsJsonObject().get("data").getAsJsonObject().get("left").getAsJsonObject().get("y").getAsFloat();
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

	public StateInTime getStateInTime() {
		StateInTime state = new StateInTime();
		state.setBallX(getBallX());
		state.setBallY(getBallY());
		state.setConfBallRadius(getConfBallRadius());
		state.setConfMaxHeight(getConfMaxHeight());
		state.setConfMaxWidth(getConfMaxWidth());
		state.setConfPaddleHeight(getConfPaddleHeight());
		state.setConfPaddleWidth(getConfPaddleWidth());
		state.setConfTickInterval(getConfTickInterval());
		state.setLeftPlayerY(getLeftPlayerY());
		state.setRightPlayerY(getRightPlayerY());
		state.setTime(getTime());
		return state;
	}
	
	
	
	
}
