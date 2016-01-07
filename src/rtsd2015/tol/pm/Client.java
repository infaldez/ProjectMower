package rtsd2015.tol.pm;

import java.net.*;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.KeyEvent;
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

	private MessageHandler messageHandler;

	Client(Launcher mainApp, RootLayoutController controller, String nickname, int port, long seed) {
		this.mainApp = mainApp;
		this.controller = controller;
		this.nickname = nickname;
		this.serverPort = port;
		this.seed = seed;
		this.messageHandler = new MessageHandler();

		this.messageHandler.addHandler(MessageType.PING, (Message msg) -> {
			Message response = new Message(MessageType.PONG, msg.body);
			try {
				sendMessage(response, msg.address, msg.port);
			}
			catch (IOException e) {
				System.err.println("Client failed to send PONG");
				e.printStackTrace(System.err);
			}
		});

		this.messageHandler.unexpectedMessage = (Message msg) -> {
			System.out.println("Client unexpected message: " + msg.toString());
		};
	}

	public void joinServer(InetAddress address) throws SocketException, IOException {
		if (socket == null) {
			serverAddress = address;
			socket = new DatagramSocket();
			socket.setSoTimeout(100);
		}
		// Add handler for the response
		messageHandler.addHandler(MessageType.ACCEPT, (Message msg) -> {
			messageHandler.removeHandler(MessageType.ACCEPT); // Remove this handler
			controller.setStatus("Connection accepted with id: " + msg.body);
			state = State.CONNECTED;
		});

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
			message = new Message(packet);
		}
		else {
			message = null;
		}

		return message;
	}

	private void sendMessage(Message message) throws IOException {
		sendMessage(message, serverAddress, serverPort);
	}

	private void sendMessage(Message message, InetAddress address, int port) throws IOException {
		byte[] data = message.getData();
		DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
		socket.send(packet);
	}

	public void run() {
		try {
			Game clientGame;
			ClientRenderer renderer;
			Thread gameThread;
			Thread renderThread;
			KeyboardInput input;
			StopWatch pingTimer = new StopWatch();
			joinServer(InetAddress.getByName("localhost"));
			Message pingMessage = new Message(MessageType.PING, "test");
			sendMessage(pingMessage);
			pingTimer.start();

			messageHandler.addHandler(MessageType.PONG, (Message msg) -> {
				double time = pingTimer.msLap();
				Platform.runLater(() -> {
					controller.setPing(String.format("%.1f",time));
					controller.switchBtnDisconnect();
				});
				try {
					sendMessage(pingMessage);
				}
				catch (IOException e) {
					System.err.println("Client sending ping failed.");
					e.printStackTrace(System.err);
				}
			});


			while(state == State.CONNECTING) {
				Message message = receiveMessage();
				if (message != null) {
					messageHandler.handle(message);
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
			input = new KeyboardInput(mainApp, clientGame);

			messageHandler.addHandler(MessageType.PREPARE, (Message msg) -> {
				String[] parts = msg.body.split(" ");
				seed = Long.valueOf(parts[0]);
				int width = Integer.valueOf(parts[1]);
				int height = Integer.valueOf(parts[2]);
				//client = new Game(mainApp, seed, true);
				try {
					sendMessage(new Message(MessageType.READY));
					state = State.IN_GAME;
				}
				catch (IOException e) {
					System.out.println("Client Exception!");
					e.printStackTrace(System.err);
				}
			});
			while(state == State.CONNECTED) {
				Message message = receiveMessage();
				if(message != null) {
					messageHandler.handle(message);
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
