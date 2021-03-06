package rtsd2015.tol.pm;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

import rtsd2015.tol.pm.enums.Facing;
import rtsd2015.tol.pm.enums.MessageType;
import rtsd2015.tol.pm.enums.Side;


public class Server implements Runnable {
	private Context context;
	private long seed;
	private long lastUpdate = 0;
	private int ticksPerSecond = 10;
	
	/**
	 * Container to hold data of connected clients.
	 * @author Daniel
	 *
	 */
	static protected class ConnectedClient {
		public enum ClientState {CONNECTED, STARTING, READY, DISCONNECTED};
		ClientState state;
		String nickname;
		InetAddress address;
		int port;
		ConnectedClient(String n, InetAddress a, int p) {
			nickname = n;
			address = a;
			port = p;
			state = ClientState.CONNECTED;
		}
	}

	/**
	 * Server context to hold server state.
	 * @author Daniel
	 *
	 */
	protected class Context {
		public Server server;
		public Game game;
		public DatagramSocket socket;
		public ArrayList<ConnectedClient> clients;
		public int clientCounter = 0;
		public State state;
		public MessageHandler messageHandler;

		Context(Server owner) {
			server = owner;
			messageHandler = new MessageHandler();
			clients = new ArrayList<ConnectedClient>();
		}

	}
	
	/**
	 * Interface for state machine implementation of the server
	 * @author Daniel
	 *
	 */
	protected interface State {
		// run returns true for server loop to continue
		public boolean run(Context context) throws Exception;
	}
	
	/**
	 * Try to add new client and return its id.
	 * @param context
	 * @param newClient
	 * @return newClientId
	 */

	static private int addClient(Context context, ConnectedClient newClient) {
		// Returns clients id if succesful, otherwise returns -1
		boolean valid = true;
		int newId = -1;
		for(ConnectedClient oldClient : context.clients) {
			// Validate that no same nick or address/port combination exists
			if((newClient.address.equals(oldClient.address) && newClient.port == oldClient.port) ||
				newClient.nickname.equals(oldClient.nickname)) {
				valid = false;
				break;
			}
		}
		if(valid) {
			newId = context.clientCounter++;
			context.clients.add(newId, newClient);
		}

		return newId;
	}

	/**
	 * Get index of the connected client that sent the message.
	 * @param context
	 * @param msg
	 * @return
	 */
	static private int getSender(Context context,  Message msg) {
		return getSender(context, msg.address, msg.port);
	}

	@SuppressWarnings("unused")
	static private int getSender(Context context, DatagramPacket packet) {
		// Return senders id or -1
		return getSender(context, packet.getAddress(), packet.getPort());
	}

	static private int getSender(Context context, InetAddress address, int port) {
		for (int i = 0; i < context.clients.size(); i++) {
			ConnectedClient client = context.clients.get(i);
			if (client.address.equals(address) && client.port == port) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns a packet received from the socket.
	 * @param context
	 * @return packet
	 * @throws IOException
	 */
	static private DatagramPacket receivePacket(Context context) throws IOException {
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);

		context.socket.receive(packet);

		return packet;
	}

	/**
	 * Returns a message received from the socket.
	 * @param context
	 * @param message
	 * @param client
	 */
	static private void sendMessage(Context context, Message message, ConnectedClient client) {
		sendMessage(context, message, client.address, client.port);
	}
	
	/**
	 * Sends a message to given destination.
	 * @param context
	 * @param message
	 * @param address
	 * @param port
	 */

	static private void sendMessage(Context context, Message message, InetAddress address, int port) {
		byte[] data = message.getData();
		DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
		try {
			context.socket.send(packet);
		}
		catch (IOException e) {
			System.out.println("Server exception when sending message: "+message.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Broadcast a message to all connected clients.
	 * @param context
	 * @param message
	 */
	static private void broadcastMessage(Context context, Message message) {
		for (ConnectedClient client : context.clients) {
			sendMessage(context, message, client);
		}
	}

	/**
	 * Implementation of the server state machine.
	 * @author Daniel
	 *
	 */
	protected enum States implements State {
		/**
		 * State where players connect
		 */
		WAIT_PLAYERS {
			public boolean run(Context context) throws IOException, ClassNotFoundException {
				DatagramPacket packet = receivePacket(context);
				Message message = new Message(packet);

				context.messageHandler.addHandler(MessageType.JOIN, (Message msg) -> {
					Message reply;
					ConnectedClient newClient = new ConnectedClient(msg.body,
							packet.getAddress(), packet.getPort());
					int newClientId = addClient(context, newClient);
					if(newClientId != -1) {
						reply = new Message(MessageType.ACCEPT, Integer.toString(newClientId));
					}
					else {
						reply = new Message(MessageType.DECLINE, "");
					}
					sendMessage(context, reply, packet.getAddress(), packet.getPort());
				});

				context.messageHandler.addHandler(MessageType.START_GAME, (Message msg) -> {
					int senderId = getSender(context, msg);

					if (0 <= senderId && senderId < context.clients.size()) {
						context.clients.get(senderId).state = ConnectedClient.ClientState.STARTING;
					}
					else {
						System.out.println("Server: Unexpected READY from: " + msg.address + ":" + msg.port);
					}
					// Check if all clients are ready and continue to game if so
					boolean allStarting = true;
					for (ConnectedClient client : context.clients) {
						if (client.state != ConnectedClient.ClientState.STARTING) {
							allStarting = false;
						}
					}
					if (allStarting) {
					// Create game
					context.game = new Game(context.server.seed);
					Message prepareMessage = new Message(MessageType.PREPARE,
							context.game.getLevel().getSeed() + " " +
							context.game.getLevel().getWidth() + " " +
							context.game.getLevel().getHeight()); // seed + width + height
					broadcastMessage(context, prepareMessage);

					// Remove handlers not needed in next state
					context.messageHandler.removeHandler(MessageType.START_GAME);
					context.messageHandler.removeHandler(MessageType.JOIN);
					context.state = States.GAME_START;
					}
				});

				context.messageHandler.handle(message);

				return true;
			}
		},
		/**
		 * Pre-game preparation state.
		 */
		GAME_START {
			public boolean run(Context context) throws IOException, ClassNotFoundException {
				DatagramPacket packet = receivePacket(context);
				Message message = new Message(packet);

				context.messageHandler.addHandler(MessageType.READY, (Message msg) -> {
					int senderId = getSender(context, msg);
					if (0 <= senderId && senderId < context.clients.size()) {
						context.clients.get(senderId).state = ConnectedClient.ClientState.READY;
					}
					else {
						System.out.println("Server: Unexpected READY from: " + msg.address + ":" + msg.port);
					}
				});

				if (message != null) {
					context.messageHandler.handle(message);
				}

				// Check if all clients are ready and continue to game if so
				boolean allReady = true;
				for (ConnectedClient client : context.clients) {
					if (client.state != ConnectedClient.ClientState.READY) {
						allReady = false;
					}
				}

				if (allReady) {
					context.state = States.GAME_LOOP;
				}
				else {
					context.state = States.GAME_START;
				}

				return true;
			}
		},
		/**
		 * Game main loop
		 */
		GAME_LOOP {
			public boolean run(Context context) {
				Message message = null;
				try {
					DatagramPacket packet = receivePacket(context);
					 message = new Message(packet);
				}
				catch (Exception e) {
					System.err.println("Server: Error in message handling in game loop.");
					e.printStackTrace(System.err);
				}
				context.messageHandler.addHandler(MessageType.COMMIT, (Message msg) -> {
					int senderId = getSender(context, msg);
					String[] parts = msg.body.split(" ");
					Facing dir = Facing.values[Integer.parseInt(parts[0])];
					int speed = Integer.parseInt(parts[1]);

					Entity player = context.game.getPlayers().get(senderId);
					if (player != null) {
						player.setDir(dir);
						player.setSpeed(speed);
					}
					else {
						System.err.println("Commit from invalid id. " + msg.address + ":" + msg.port);
					}
				});

				if (message != null) {
					context.messageHandler.handle(message);
				}

				long sinceLastUpdate = System.currentTimeMillis() - context.server.lastUpdate;
				if( sinceLastUpdate > 1000/context.server.ticksPerSecond){
					doServerTick(context);
				}

				return true;
			}
		},
		GAME_PAUSE {
			public boolean run(Context context) {
				context.state = States.GAME_LOOP;
				return true;
			}
		},
		GAME_END {
			public boolean run(Context context) {
				return false;
			}
		}
	}

	/**
	 * Do a tick on server game and broadcast changes to connected players.
	 * @param context
	 */
	static protected void doServerTick(Context context) {
		context.game.doTick();
		// Mark players updated so their state gets broad casted
		context.game.markUpdated(context.game.getPlayers().get(0).getId());
		context.game.markUpdated(context.game.getPlayers().get(1).getId());

		GameUpdate gameUpdate = new GameUpdate(context.game);

		Message gameUpdateMsg = new Message(MessageType.GAME_UPDATE, gameUpdate.serialize());

		broadcastMessage(context, gameUpdateMsg);
		
		if(context.game.getWinner() != Side.GAIA) {
			Message gameEndMessage = new Message(MessageType.GAME_END, context.game.getWinner().name());
			broadcastMessage(context, gameEndMessage);
			context.state = States.GAME_END;
		}

		context.server.lastUpdate = System.currentTimeMillis();
	}

	Server(int port) throws Exception {
		this(port, new Random().nextInt()); // Random seed if none given
	}

	Server(int port, long seed) throws Exception {
		this.seed = seed;
		context = new Context(this);
		context.state = States.WAIT_PLAYERS;
		context.socket = new DatagramSocket(port);
		context.socket.setReuseAddress(true);

		context.messageHandler.addHandler(MessageType.PING, (Message msg) -> {
			Message response = new Message(MessageType.PONG, msg.body);
			sendMessage(context, response, msg.address, msg.port);
		});

		context.messageHandler.unexpectedMessage = (Message msg) -> {
			System.out.println("Server unexpected message: " + msg.toString());
		};
	}

	/**
	 * Server thread.
	 */
	public void run() {
		Boolean running = true;
		try {
			while(running){
				try {
					running = context.state.run(context);
				}
				catch (Exception e){
					running = false;
					System.out.println("Server Exception!");
					e.printStackTrace(System.err);
				}
			}
		}
		finally {
			context.socket.close();
		}
	}

	public void close() {
		context.socket.close();
	}

}
