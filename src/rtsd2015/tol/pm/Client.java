package rtsd2015.tol.pm;

import java.net.*;
import java.io.*;
import java.lang.*;
import rtsd2015.tol.pm.enums.MessageType;

public class Client implements Runnable {
	private DatagramSocket socket = null;
	private String nickname;
	
	private InetAddress serverAddress;
	private int serverPort;
	private int serverPing;
	
	Client(String nick) {
		nickname = nick;
	}
	
	public void joinServer(InetAddress address, int port)
			throws SocketException {
		if (socket == null) {
			serverAddress = address;
			serverPort = port;
			socket = new DatagramSocket();
		}
	}
	
	public void disconnect() {
		
	}
	
	private DatagramPacket receivePacket() throws IOException {
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		socket.receive(packet);
		return packet;
	}

	private Message receiveMessage() throws IOException, ClassNotFoundException {
		DatagramPacket packet = receivePacket();
		Message message = new Message(packet.getData());
		
		return message;
	}
	
	private void sendMessage(Message message) throws IOException {
		byte[] data = message.getData();
		DatagramPacket packet = new DatagramPacket(data, data.length,
				serverAddress, serverPort);
		socket.send(packet);
	}
	
	public void run() {
		try {
			StopWatch pingTimer = new StopWatch();
			System.out.println("Client running!");
			joinServer(InetAddress.getByName("localhost"), 3141);
			sendMessage(new Message(MessageType.PING, "test"));
			pingTimer.start();
			Message message = receiveMessage();
			double time = pingTimer.msLap();
			
			System.out.format("Client received: %s ping: %.2fms\n", message.toString(), time);
		}
		catch (Exception e) {
			System.out.print("Client Exception!");
			e.printStackTrace(System.err);
		}
		finally {
			socket.close();
		}
	}
}
