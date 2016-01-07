package rtsd2015.tol.pm;

import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.ArrayList;
import java.util.Random;

import rtsd2015.tol.pm.enums.Facing;
import rtsd2015.tol.pm.enums.MessageType;


public class Server implements Runnable {
	private Context context;
	private long seed;
	static protected class ConnectedClient {
		public enum ClientState {CONNECTED, READY, DISCONNECTED};
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

	protected interface State {
		// run returns true for server loop to continue
		public boolean run(Context context) throws Exception;
	}

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

	static private int getSender(Context context,  Message msg) {
		return getSender(context, msg.address, msg.port);
	}

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

	static private DatagramPacket receivePacket(Context context) throws IOException {
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);

		context.socket.receive(packet);

		return packet;
	}

	static private void sendMessage(Context context, Message message, ConnectedClient client) {
		sendMessage(context, message, client.address, client.port);
	}

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

	static private void broadcastMessage(Context context, Message message) {
		for (ConnectedClient client : context.clients) {
			sendMessage(context, message, client);
		}
	}

	public String getEntityStatusString(Entity entity) {
		int id = entity.getId();
		int[] pos = entity.getPos();
		int dir = entity.getDir().ordinal();
		int speed = entity.getSpeed();
		int health = entity.getHealth();

		return id +  "," +  pos[0] + "," + pos[1] + "," + dir + "," + speed + "," + health;
	}

	public void setEntityFromStatusString(String status) {
		String[] parts = status.split(",");
		if (parts.length != 6) {
			throw(new java.lang.IllegalArgumentException("status string must contain 6 fields"));
		}

		int id = Integer.parseInt(parts[0]);

		// TODO get entity by id and set attributes

		int x = Integer.parseInt(parts[1]);
		int y = Integer.parseInt(parts[2]);
		// TODO: FIX!
		Facing dir = Facing.values[Integer.parseInt(parts[3])];
		int speed = Integer.parseInt(parts[4]);
		int health = Integer.parseInt(parts[5]);

	}

	protected enum States implements State {
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
				});

				context.messageHandler.handle(message);

				return true;
			}
		},
		GAME_START {
			public boolean run(Context context) throws IOException, ClassNotFoundException {
				DatagramPacket packet = receivePacket(context);
				Message message = new Message(packet.getData());

				context.messageHandler.addHandler(MessageType.READY, (Message msg) -> {
					int senderId = getSender(context, msg);
					if (0 <= senderId && senderId < context.clients.size()) {
						context.clients.get(senderId).state = ConnectedClient.ClientState.READY;
					}
					else {
						System.out.println("Server: Unexpected READY from: " + msg.address + ":" + msg.port);
					}
				});
				
				context.messageHandler.handle(message);

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
		GAME_LOOP {
			public boolean run(Context context) {
				broadcastGameState(context);
				context.state = States.GAME_END;
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

	static protected void broadcastGameState(Context context) {

	}

	Server(String hostname, int port) throws Exception {
		this(hostname, port, new Random().nextInt()); // Random seed if none given
	}

	Server(String hostname, int port, long seed) throws Exception {
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

	public void run() {
		System.out.println("Server running!");
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
