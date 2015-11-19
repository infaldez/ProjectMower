package rtsd2015.tol.pm;

import java.util.ArrayList;

public class Game {

	private Timer timer;
	private Level lvl;
	private ArrayList<Player> players;
	private ArrayList<Score> scores;
	private boolean ingame;

	public Game() {
		boolean initialized = initGame();
		if (initialized) {
			ingame = true;
			gameCycle();
		}
	}

	private boolean initGame() {
		// TODO: init game elements
		return false;
	}

	private void gameCycle() {
		long cycle = 0;
		while (ingame && (cycle < 100)) {
			cycle++;
		}
	}


}
