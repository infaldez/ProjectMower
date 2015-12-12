package rtsd2015.tol.pm;

import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.ArrayList;

import rtsd2015.tol.pm.enums.MessageType;
public class Server implements Runnable {
	private Context context;
	protected class ConnectedClient {
		InetAddress address;
		int port;
		String nick;
	}

	protected class Context {
		public Game game;
		public DatagramSocket socket;
		public ArrayList<ConnectedClient> clients;
		public State state;
	}

	protected interface State {
		// run returns true for server loop to continue
		public boolean run(Context context) throws Exception;
	}
	
	
	static private int getSender(Context context, DatagramPacket packet) {
		// Return senders id or -1
		for (int i = 0; i < context.clients.size(); i++) {
			ConnectedClient client = context.clients.get(i);
			InetAddress address = packet.getAddress();
			int port = packet.getPort();
			if (client.address.equals(address) && client.port == port) {
				return i;
			}
		}
		return -1;
	}
	
	static private DatagramPacket receivePacket(Context context)
			throws Exception {
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		
		context.socket.receive(packet);
		
		return packet;
	}
	
	static private void sendMessage(Context context, Message message,
			InetAddress address, int port) throws Exception {
		byte[] data = message.getData();
		DatagramPacket packet = new DatagramPacket(data, data.length, address,
				port);
		context.socket.send(packet);
	}

	
	protected enum States implements State {
		WAIT_PLAYERS {
			public boolean run(Context context) throws Exception {
				DatagramPacket packet = receivePacket(context);
				Message message = new Message(packet.getData());
				
				switch (message.type) {
				case PING:
					Message reply = new Message(MessageType.PONG, message.body); 
					sendMessage(context, reply, packet.getAddress(),
							packet.getPort());
					break;
				default:
					System.out.print("Unexpected message: ");
					System.out.println(message.toString());
				}

				context.state = States.WAIT_PLAYERS;
				return true;
			}
		},
		GAME_START {
			public boolean run(Context context) {
				context.state = States.GAME_LOOP;
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
		context = new Context();
		context.state = States.WAIT_PLAYERS;
		context.socket = new DatagramSocket(port);
		context.socket.setReuseAddress(true);
	}
	
	public void run() {
		System.out.println("Server running!");
		Boolean running = true;
		while(running){
			try {
			running = context.state.run(context);
			}
			catch (Exception e){
				System.err.println(e.getMessage());
				running = false;
			}
		}
	}
	
}
