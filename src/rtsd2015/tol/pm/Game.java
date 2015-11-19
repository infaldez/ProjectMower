package rtsd2015.tol.pm;

import java.util.ArrayList;

public class Game {

	private Timer timer;
	private Level lvl;
	private ArrayList<Player> players;
	private ArrayList<Score> scores;

	private static int limitedCycles = 1280;	// For testing purposes only
	private static int fps = 0;
	private static long lastFpsTime;
	private static long frameId;

	/**
	 * Starts a new game locally
	 *
	 * @throws InterruptedException
	 */
	public Game() throws InterruptedException {
		boolean initialized = initGame();
		if (initialized) {
			gameLoop();
		}
		// TODO: end game
	}

	/**
	 * Initializes the new game
	 *
	 * @return true if everything was OK and game can begin
	 */
	private boolean initGame() {
		// TODO: init game elements
		timer = new Timer();
		timer.start();
		return false;
	}

	/**
	 * The main gameloop
	 *
	 * @throws InterruptedException
	 */
	private static void gameLoop() throws InterruptedException {

		// Init game loop
		long		lastLoopTime	= System.nanoTime();
		final int	TARGET_FPS		= 16;
		final long	OPTIMAL_TIME	= 1000000000 / TARGET_FPS;
		// TODO: GUI-call

		while (limitedCycles > 0) {

			// Define this cycle
			long now 				= System.nanoTime();
			long updateLength 		= now - lastLoopTime;
			lastLoopTime 			= now;
			double delta 			= updateLength / ((double)OPTIMAL_TIME);

			// Update counters
			lastFpsTime 			+= updateLength;
			fps++;
			frameId++;
			limitedCycles--;

			// TODO: content of the cycle

			// Wait for the next frame
			Thread.sleep((lastLoopTime-System.nanoTime()+OPTIMAL_TIME)/1000000);
		}
	}


}
