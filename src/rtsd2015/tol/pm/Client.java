package rtsd2015.tol.pm;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;
import javafx.application.Platform;
import rtsd2015.tol.pm.enums.MessageType;
import rtsd2015.tol.pm.enums.Side;
import rtsd2015.tol.pm.view.RootLayoutController;

public class Client implements Runnable {
	private ClientRenderer renderer;
	private Thread gameThread;
	private Thread renderThread;
	/**
	 * Client states
	 * @author Daniel
	 *
	 */
	public enum State {
		DISCONNECTED, CONNECTING, CONNECTED, IN_GAME, PAUSED, END_GAME
	}
	private DatagramSocket socket = null;
	private String nickname;
	private int playerId = -1;
	private State state = State.DISCONNECTED;
	private Game clientGame;

	private int outgoingTickCount = 20;

	private InetAddress serverAddress;
	private int serverPort;
	private long seed = 128;

	private RootLayoutController controller;
	private MessageHandler messageHandler;

	Client(RootLayoutController controller, String nickname, long seed) {
		this.controller = controller;
		this.nickname = nickname;
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

	public Game getGame() {
		return clientGame;
	}

	public ClientRenderer getRenderer() {
		return renderer;
	}

	public int getPlayerId() {
		return playerId;
	}

	/**
	 * Send join request to game server and create socket if not yet created.
	 * @param address
	 * @param port
	 * @throws SocketException
	 * @throws IOException
	 */
	public void joinServer(InetAddress address, int port) throws SocketException, IOException {
		if (socket == null) {
			serverAddress = address;
			serverPort = port;
			socket = new DatagramSocket();
			socket.setSoTimeout(100);
		}
		// Add handler for the response
		messageHandler.addHandler(MessageType.ACCEPT, (Message msg) -> {
			messageHandler.removeHandler(MessageType.ACCEPT); // Remove this handler
			playerId = Integer.parseInt(msg.body);
			controller.setStatus("Connection accepted with id: " + playerId);
			state = State.CONNECTED;
		});

		sendMessage(new Message(MessageType.JOIN, nickname));
		state = State.CONNECTING;
	}

	/**
	 * Send game start request to the game server.
	 */
	public void startGame() {
		renderer.flush(false);
		if (state == State.CONNECTED) {
			try {
				sendMessage(new Message(MessageType.START_GAME));
			}
			catch (IOException e){
				e.printStackTrace(System.err);
			}
		}
	}

	/**
	 * Disconnect from the server.
	 */
	public void disconnect() {
		socket.close();
		if (socket.isClosed()) {
			controller.setStatus("Disconnected");
		} else {
			controller.setStatus("Could not disconnect!");
		}
	}

	/**
	 * Receive packet from the game server.
	 * @return
	 * @throws IOException
	 */
	private DatagramPacket receivePacket() throws IOException {
		if (socket == null) {
			return null;
		}
		byte[] data = new byte[2048];
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

	/**
	 * Receive message from the game server.
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
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

	/**
	 * Send message to the game server.
	 * @param message
	 * @throws IOException
	 */
	private void sendMessage(Message message) throws IOException {
		sendMessage(message, serverAddress, serverPort);
	}

	private void sendMessage(Message message, InetAddress address, int port) throws IOException {
		byte[] data = message.getData();
		DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
		socket.send(packet);
	}

	/**
	 * Client thread implementation.
	 */
	public void run() {
		try {
			// Wait for joinServer to be invoked.
			while (state.equals(State.DISCONNECTED)) {
				Thread.sleep(200);
			}

			// Wait for server to accept join
			while (state.equals(State.CONNECTING)) {
				Message msg = receiveMessage();
				if (msg != null) {
					messageHandler.handle(msg);
				}
			}

			StopWatch pingTimer = new StopWatch();
			Message pingMessage = new Message(MessageType.PING, "test");

			// Ping response handler
			messageHandler.addHandler(MessageType.PONG, (Message msg) -> {
				double time = pingTimer.msStop();
				Platform.runLater(() -> {
					controller.setPing(String.format("%.1f",time));
				});
			});

			// Ping request executor to ping server once a second
			ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
			executor.scheduleAtFixedRate(() -> {
					pingTimer.start();
					try {
						sendMessage(pingMessage);
					}
					catch (IOException e) { }
				}, 0, 1, TimeUnit.SECONDS);

			clientGame = new Game(seed);
			gameThread = new Thread(clientGame);
			gameThread.start();
			renderer = new ClientRenderer(this);
			renderThread = new Thread(renderer);
			renderThread.start();
			@SuppressWarnings("unused")
			KeyboardInput input = new KeyboardInput(this);

			// Handler to setup a game when server requests so
			messageHandler.addHandler(MessageType.PREPARE, (Message msg) -> {
				String[] parts = msg.body.split(" ");
				seed = Long.valueOf(parts[0]);
				@SuppressWarnings("unused")
				int width = Integer.valueOf(parts[1]);
				@SuppressWarnings("unused")
				int height = Integer.valueOf(parts[2]);

				clientGame.stop();
				try {
					gameThread.join(); // Wait thread to finish
				}
				catch (InterruptedException e) {
					e.printStackTrace(System.err);
				}

				clientGame = new Game(seed);
				renderer.updateGameReference();
				renderer.flush(true);
				gameThread = new Thread(clientGame);
				gameThread.start();
				clientGame.setInGame(true);

				try {
					sendMessage(new Message(MessageType.READY));
				}
				catch (IOException e) {
					System.out.println("Client Exception!");
					e.printStackTrace(System.err);
				}

				state = State.IN_GAME;
			});


			// Wait for server to start a game
			while(state == State.CONNECTED) {
				Message message = receiveMessage();
				if(message != null) {
					messageHandler.handle(message);
				}
			}

			long lastUpdate = System.currentTimeMillis();
			long lastSentUpdate = System.currentTimeMillis();
			int updateDeadline = 20;

			// GameUpdate handler
			messageHandler.addHandler(MessageType.GAME_UPDATE, (Message msg) -> {
				try {
					GameUpdate gameUpdate = GameUpdate.deserialize(msg.body);
					clientGame.setTick(gameUpdate.tick);

					for (EntityUpdate u : gameUpdate.updates) {
						clientGame.updateEntity(u.id, u.x, u.y, u.dir, u.speed, u.health);
					}

					for (Integer id : gameUpdate.kill) {
						Entity.getEntity(id).setAlive(false);
					}

					for (int i = 0; i < gameUpdate.scores.size(); i++) {
						clientGame.getPlayers().get(i).setScore(gameUpdate.scores.get(i));
					}

				}
				catch(Exception e) {
					System.err.println("Client: Error in parsing GameUpdate!");
					e.printStackTrace(System.err);
				}
				state = State.IN_GAME;
			});

			messageHandler.addHandler(MessageType.PAUSE, (Message msg) -> {
				state = State.PAUSED;
			});

			messageHandler.addHandler(MessageType.GAME_END, (Message msg) -> {
				clientGame.setWinner(Side.valueOf(msg.body));
				state = State.END_GAME;
			});

			// Client main game loop
			while (state == State.IN_GAME || state == State.PAUSED) {
				// Read user input
				if (System.currentTimeMillis() - lastSentUpdate > 1000/outgoingTickCount) {
					Entity playerEntity = clientGame.getPlayers().get(playerId);
					String moveCommit = playerEntity.getDir().ordinal() + " " + playerEntity.getSpeed();
					sendMessage(new Message(MessageType.COMMIT, moveCommit));

					lastSentUpdate = System.currentTimeMillis();
				}


				// Determine how long socket is allowed to block
				long tillDeadline = lastUpdate - System.currentTimeMillis() + updateDeadline;
				try {
					int timeout = Math.toIntExact(tillDeadline);
					if (timeout <= 0) {
						timeout = 1;
					}
					socket.setSoTimeout(timeout);
				}
				catch (java.lang.ArithmeticException e){
					// In case something tillDeadline overflows default to full deadline
					socket.setSoTimeout(updateDeadline);
				}

				Message message = receiveMessage();
				if (message != null) {
					messageHandler.handle(message);
				}

				lastUpdate = System.currentTimeMillis();
			}
			
			while (state == State.END_GAME) {
				Thread.sleep(200);
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
