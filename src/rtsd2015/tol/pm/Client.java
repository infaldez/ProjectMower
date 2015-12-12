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

	private Message receiveMessage() throws Exception {
		DatagramPacket packet = receivePacket();
		Message message = new Message(packet.getData());
		
		return message;
	}
	
	private void sendMessage(Message message) throws Exception {
		byte[] data = message.getData();
		DatagramPacket packet = new DatagramPacket(data, data.length,
				serverAddress, serverPort);
		socket.send(packet);
	}
	
	public void run() {
		try {
		System.out.println("Client running!");
		joinServer(InetAddress.getByName("localhost"), 3141);
		sendMessage(new Message(MessageType.PING, "test"));
		Message message = receiveMessage();
		
		System.out.println("Client received: " + message.toString());
		}
		catch (Exception e) {
			System.out.print("Client Exception!");
			e.printStackTrace(System.err);
		}
	}
}
