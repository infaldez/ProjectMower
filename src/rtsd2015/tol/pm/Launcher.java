package rtsd2015.tol.pm;

import java.io.*;
import java.util.Scanner;

public class Launcher {

	public static void main(String[] args) throws Exception {
		Client client = new Client("player1");
		Server server = new Server("localhost",	3141);
		
		Thread clientThread = new Thread(client);
		Thread serverThread = new Thread(server);
		
		clientThread.start();
		serverThread.start();
		
		Scanner scanner = new Scanner(System.in);
		scanner.next();
		scanner.close();
		clientThread.interrupt();
		serverThread.interrupt();

	}

}
