package fi.nakoradio.hwo.integration.core;

public class BadInputMessageException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BadInputMessageException(String message, Exception e){
		super(message,e);
	}
	
	public BadInputMessageException(String message){
		super(message);
	}
	
	
}
