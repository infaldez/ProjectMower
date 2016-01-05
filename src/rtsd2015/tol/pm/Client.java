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
		DISCONNECTED, CONNECTING, CONNECTED, IN_GAME, PAUSED
	}
	private DatagramSocket socket = null;
	private String nickname;
	private State state = State.DISCONNECTED;

	private InetAddress serverAddress;
	private int serverPort;
	private int serverPing;
	private long seed = 128;

	private Launcher mainApp;
	private RootLayoutController controller;

	Client(Launcher mainApp, RootLayoutController controller, String nickname, int port, long seed) {
		this.mainApp = mainApp;
		this.controller = controller;
		this.nickname = nickname;
		this.serverPort = port;
		this.seed = seed;
	}

	public void joinServer(InetAddress address) throws SocketException, IOException {
		if (socket == null) {
			serverAddress = address;
			socket = new DatagramSocket();
			socket.setSoTimeout(100);
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
		try {
			socket.receive(packet);
		}
		catch (java.net.SocketTimeoutException e) {
			// Time out return null
			packet = null;
		}

		return packet;
	}

	private Message receiveMessage() throws IOException, ClassNotFoundException {
		DatagramPacket packet = receivePacket();
		Message message;
		if (packet != null) {
			message = new Message(packet.getData());
		}
		else {
			message = null;
		}

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
			Game clientGame;
			ClientRenderer renderer;
			Thread gameThread;
			Thread renderThread;
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
					state = State.CONNECTED;
					break;
				default:
					controller.setStatus("Unexcepted message: "+message.toString());
				}
			}
			// FIXME init game here to get graphics rolling, to be removed once graphics remade
			// not to run forever
			clientGame = new Game(seed);
			gameThread = new Thread(clientGame);
			gameThread.start();
			renderer = new ClientRenderer(mainApp, clientGame, 24, 24);
			renderThread = new Thread(renderer);
			renderThread.start();

			while(state == State.CONNECTED) {
				Message message = receiveMessage();
				if (message != null) {
					switch(message.type) {
						case PREPARE:
							String[] parts = message.body.split(" ");
							seed = Long.valueOf(parts[0]);
							int width = Integer.valueOf(parts[1]);
							int height = Integer.valueOf(parts[2]);
							//client = new Game(mainApp, seed, true);
							sendMessage(new Message(MessageType.READY));
							state = State.IN_GAME;
							break;
						default:
							controller.setStatus("Unexcepted message: "+message.toString());
					}
				}
			}

			long lastUpdate = System.currentTimeMillis();
			int updateDeadline = 20;
			while(state == State.IN_GAME) {
				// Game loop
				System.out.println("je");
				// Read user input

				// Determine how long socket is allowed to block
				long tillDeadline = lastUpdate - System.currentTimeMillis() + updateDeadline;
				try {
					socket.setSoTimeout(Math.toIntExact(tillDeadline));
				}
				catch (java.lang.ArithmeticException e){
					// In case something tillDeadline overflows default to full deadline
					socket.setSoTimeout(updateDeadline);
				}

				Message message = receiveMessage();
				if (message != null) {
					switch(message.type) {
					case GAME_UPDATE:
						break;
					case PAUSE:
						state = State.PAUSED;
						break;
					default:
						System.out.println("Client: Unexpected message: "+message.toString());
					}

					while(state == State.PAUSED) {
						state = State.IN_GAME;
					}
				}
				// TODO Run view update

				lastUpdate = System.currentTimeMillis();
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
