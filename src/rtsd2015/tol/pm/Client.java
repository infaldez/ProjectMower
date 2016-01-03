package rtsd2015.tol.pm;

import java.net.*;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

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

	private Launcher mainApp;
	private RootLayoutController controller;

	Client(Launcher app, RootLayoutController c, String nick, int port) {
		mainApp = app;
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
			Message pingMessage = new Message(MessageType.PING, "test");
			sendMessage(pingMessage);
			pingTimer.start();
			while(state == State.CONNECTING) {
				Message message = receiveMessage();
				switch(message.type) {
				case PONG:
					double time = pingTimer.msLap();
					Platform.runLater(() -> {
						controller.setPing(String.format("%.1f",time));
						controller.switchBtnDisconnect();
					});
					sendMessage(pingMessage);
					break;
				case ACCEPT:
					controller.setStatus("Connection accepted with id: "+message.body);
					Game client = new Game(mainApp);
					break;
				default:
					controller.setStatus("Unexcepted message: "+message.toString());
				}
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
