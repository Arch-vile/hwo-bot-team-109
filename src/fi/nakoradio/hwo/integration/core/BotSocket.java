package fi.nakoradio.hwo.integration.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class BotSocket {
	
	 private Socket socket;
     private PrintWriter out;
     private BufferedReader in;
	

     public BotSocket(String host, int port){
	
		try {
			// TODO: what about timeouts etc?
            socket = new Socket(host,port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Cannot find host: " + host);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Failed to connect");
            System.exit(1);
        }
	}
	
	
		

	 public void close(){
		 try{
			 out.close();
			 in.close();
			 socket.close();
		 }catch(Exception e){
			 System.err.println("Failed to close connection");
			 System.exit(1);
		 }
	 }


	public PrintWriter getOut() {
		return out;
	}


	public BufferedReader getIn() {
		return in;
	}
	 
	 
	 
}
