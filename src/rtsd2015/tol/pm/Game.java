package rtsd2015.tol.pm;

import java.util.ArrayList;
import java.util.List;

public class Game {

	private Timer timer;
	private Level lvl;
	private List<Player> players;
	private List<Score> scores;

	private static int limitedCycles = 1280;	// For testing purposes only
	private static long lastFpsTime;
	private static long frameId;

	/**
	 * Initializes a new game
	 *
	 * @return true if everything was OK and game can begin
	 * @throws InterruptedException
	 */
	void initGame() throws InterruptedException {
		// TODO: init game elements
		timer = new Timer();
		timer.start();
		gameLoop();
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

		while (limitedCycles > 0) {

			// Define this cycle
			long now 				= System.nanoTime();
			long updateLength 		= now - lastLoopTime;
			lastLoopTime 			= now;
			double delta 			= updateLength / ((double)OPTIMAL_TIME);

			// Update counters
			lastFpsTime 			+= updateLength;
			frameId++;
			limitedCycles--;

			// TODO: content of the cycle
			System.out.println("Current frame: " + frameId + ", frames left: " + limitedCycles);

			// Wait for the next frame
			Thread.sleep((lastLoopTime-System.nanoTime()+OPTIMAL_TIME)/1000000);
		}
	}


}
