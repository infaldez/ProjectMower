package rtsd2015.tol.pm;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class Server {
	protected class Context {
		public Game game;
		public DatagramSocket serverSocket;
		public ArrayList<DatagramSocket> clientSockets;
		public State state;
	}

	protected interface State {
		// run returns true for server loop to continue
		public boolean run(Context context);
	}
	
	protected enum States implements State {
		WAIT_PLAYERS {
			public boolean run(Context context) {
				context.state = States.GAME_START;
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
	
	public void run(String hostname, int port) throws Exception {
		Context context = new Context();
		context.state = States.WAIT_PLAYERS;
		context.serverSocket = new DatagramSocket(port);
		
		while(context.state.run(context));
	}
	
}
