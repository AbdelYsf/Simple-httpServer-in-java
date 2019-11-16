package Abdel.ysf.coreJava;

import java.io.IOException;
import java.net.ServerSocket;

public class Main {

	public static void main(String[] args) {

		try {
			ServerSocket serverSocket = new ServerSocket(HttpServer.LESTENING_PORT);
			System.out.println("Listening on port "+HttpServer.LESTENING_PORT+" ...");
			
			while(true) {
				HttpServer httpServer = new HttpServer(serverSocket.accept());
				
				// client thread
				Thread clientThread = new Thread(httpServer);
				clientThread.start();
				
		
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
