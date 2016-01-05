package rtsd2015.tol.pm;

import java.util.ArrayList;
import java.util.List;
import rtsd2015.tol.pm.enums.Facing;
import rtsd2015.tol.pm.enums.Side;

public class Game implements Runnable {

	// SHARED
	private int[] grid = new int[2];
	private Level level;
	private List<EntityPlayer> players = new ArrayList<>();
	private Timer timer = new Timer();

	private boolean run = true;


	/**
	 * Initializes a new game for both clients and a server
	 *
	 * @throws InterruptedException
	 */
	Game(long sd) throws InterruptedException {
		this.grid[0] = 24;
		this.grid[1] = 24;
		this.level = new Level(sd, grid[0], grid[1]);
		players.add(new EntityPlayer(Side.BLUE));
		players.add(new EntityPlayer(Side.RED));
	}

	/**
	 * Returns grid size of the used game board
	 *
	 * @return
	 */
	public int[] getGrid() {
		return this.grid;
	}

	/**
	 * Returns the loaded level
	 *
	 * @return
	 */
	public Level getLevel() {
		return level;
	}

	/**
	 * Update entity defined by id.
	 *
	 */
	public void updateEntity(int id, int x, int y, Facing dir, int speed, int health) {
		// TODO Get entity from entity list by id
		Entity entity;
		// Update entity
//		entity.setPos(x, y);
//		entity.setDir(dir);
//		entity.setSpeed(speed);
//		entity.setHealth(health);
	}

	/**
	 * Independent thread for the game logic
	 *
	 */
	public void run() {
		try {
			long lastLoopTime = System.nanoTime();
			final int TARGET_FPS = 16;
			final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;

			timer.start();

			while (run) {
				// Prepare a new cycle
				lastLoopTime = System.nanoTime();

				// TODO: Add logic content

				// Wait for the next cycle
				Thread.sleep((lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000);
			}
		} catch (InterruptedException e) {
			System.out.println("Game Exception!");
			e.printStackTrace(System.err);
		}
	}

}
