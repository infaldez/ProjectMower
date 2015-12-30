package rtsd2015.tol.pm;

import java.net.*;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;

import java.io.*;
import java.lang.*;
import rtsd2015.tol.pm.enums.MessageType;
import rtsd2015.tol.pm.view.RootLayoutController;

public class Client implements Runnable {
	public enum State {
		DISCONNECTED, CONNECTING, CONNECTED
	}
	private DatagramSocket socket = null;
	private String nickname;
	private State state = State.DISCONNECTED;

	private InetAddress serverAddress;
	private int serverPort;
	private int serverPing;

	private RootLayoutController controller;

	Client(RootLayoutController c, String nick, int port) {
		controller = c;
		nickname = nick;
		serverPort = port;
	}

	public void joinServer(InetAddress address) throws SocketException, IOException {
		if (socket == null) {
			serverAddress = address;
			socket = new DatagramSocket();
		}
		sendMessage(new Message(MessageType.JOIN, nickname));
		state = State.CONNECTING;
	}

	public void disconnect() {
		socket.close();
		if (socket.isClosed()) {
			controller.switchBtnDisconnect();
			controller.setStatus("Disconnected");
		} else {
			controller.setStatus("Could not disconnect!");
		}
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
			joinServer(InetAddress.getByName("localhost"));
			sendMessage(new Message(MessageType.PING, "test"));
			pingTimer.start();
			while(state == State.CONNECTING) {
				Message message = receiveMessage();
				Platform.runLater(() -> {
					switch(message.type) {
					case PONG:
						double time = pingTimer.msLap();
						controller.setStatus("Client received: "+message.toString()+" ping: "+time+"ms");
						controller.switchBtnDisconnect();
						break;
					case ACCEPT:
						controller.setStatus("Connection accepted with id: "+message.body);
						break;
					default:
						controller.setStatus("Unexcepted message: "+message.toString());
					}
				});
			}
		}
		catch (Exception e) {
			System.out.println("Client Exception!");
			e.printStackTrace(System.err);
		}
		finally {
			socket.close();
		}
	}
}
