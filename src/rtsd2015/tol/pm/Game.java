package rtsd2015.tol.pm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import rtsd2015.tol.pm.enums.Movement;
import rtsd2015.tol.pm.enums.Side;

public class Game {

	private Launcher mainApp;
	private static GraphicsContext gc;

	protected static int seed;

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
	Game(Launcher mainApp) throws InterruptedException {

		this.mainApp = mainApp;

		// Initialize a new level
		this.seed = 0; // TODO: Add seed variable
		lvl = new Level(100, 100);

		// Initialize players
		players.add(new EntityPlayer(Side.BLUE));
		players.add(new EntityPlayer(Side.RED));

		// Initialize in-game timer
		timer = new Timer();

		// Get the main rendering space
		this.gc = mainApp.getCanvas();
		gc.clearRect(0, 0, 800, 520);

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

			// New rendering frame
			gc.clearRect(0, 0, 800, 520);

			// TODO: content of the cycle
			gc.strokeText("Delta: " + delta, 16, 96);
			System.out.println("Delta: " + delta + ", frames left: " + limitedCycles);
			System.out.println("Player(" + players.get(0).id + ") side: " + players.get(0).getSide() + " dir: "
					+ players.get(0).getDir() + " pos: " + players.get(0).getPos()[0] + ","
					+ players.get(0).getPos()[1]);
			System.out.println("Player(" + players.get(1).id + ") side: " + players.get(1).getSide() + " dir: "
					+ players.get(1).getDir() + " pos: " + players.get(1).getPos()[0] + ","
					+ players.get(1).getPos()[1]);
			System.out.println("EntityCount: " + Entity.entityCount);
			System.out.println("\b");

			// Wait for the next frame
			Thread.sleep((lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000);
		}
	}

}
