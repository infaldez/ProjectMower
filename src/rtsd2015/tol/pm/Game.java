package rtsd2015.tol.pm;

import java.util.ArrayList;
import java.util.List;

import rtsd2015.tol.pm.enums.Side;

public class Game {

	private static Timer timer;
	private static Level lvl;
	private static List<EntityPlayer> players = new ArrayList<>();

	private static int limitedCycles = 1280; // For testing purposes only
	private static long lastFpsTime;
	private static long frameId;

	/**
	 * Initializes a new game
	 * 
	 * @throws InterruptedException
	 */
	Game() throws InterruptedException {
		
		// Initialize players
		players.add(new EntityPlayer(Side.BLUE));
		players.add(new EntityPlayer(Side.RED));
		
		// Initialize in-game timer
		timer = new Timer();

		// Finally, start the game loop
		gameLoop();
	}

	/**
	 * The main gameloop
	 *
	 * @throws InterruptedException
	 */
	private static void gameLoop() throws InterruptedException {

		// Initialize game loop
		long lastLoopTime = System.nanoTime();
		final int TARGET_FPS = 16;
		final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;

		// Start timer
		timer.start();

		while (limitedCycles > 0) {

			// Define this cycle
			long now = System.nanoTime();
			long updateLength = now - lastLoopTime;
			lastLoopTime = now;
			double delta = updateLength / ((double) OPTIMAL_TIME);

			// Update counters
			lastFpsTime += updateLength;
			frameId++;
			limitedCycles--;

			// TODO: content of the cycle
			System.out.println("Current frame: " + frameId + ", frames left: " + limitedCycles);
			System.out.println("Player 1 side: " + players.get(0).getSide() + " dir: " + players.get(0).getDir());
			System.out.println("Player 2 side: " + players.get(1).getSide() + " dir: " + players.get(1).getDir());
			System.out.println("EntityCount: " + Entity.entityCount);

			// Wait for the next frame
			Thread.sleep((lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000);
		}
	}

}
