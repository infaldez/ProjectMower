package rtsd2015.tol.pm;

import java.util.ArrayList;
import java.util.List;
import rtsd2015.tol.pm.enums.Facing;
import rtsd2015.tol.pm.enums.Side;
import rtsd2015.tol.pm.enums.Hitbox;

public class Game implements Runnable {

	private int[] gameGrid = new int[2];
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
		gameGrid[0] = 16;
		gameGrid[1] = 16;
		this.level = new Level(sd, gameGrid[0], gameGrid[1], 40);
		players.add(new EntityPlayer(Side.BLUE, 0, 0));
		players.add(new EntityPlayer(Side.RED, gameGrid[0], gameGrid[1]));
	}

	/**
	 * Returns grid size of the used game board
	 *
	 * @return
	 */
	public int[] getGrid() {
		return gameGrid;
	}

	/**
	 * Returns the loaded level
	 *
	 * @return
	 */
	public Level getLevel() {
		return level;
	}

	public List<EntityPlayer> getPlayers() {
		return players;
	}

	/**
	 * Update entity defined by id.
	 *
	 */
	public void updateEntity(int id, int x, int y, Facing dir, int speed, int health) {
		// TODO Get entity from entity list by id
		//Entity entity;
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
			final int TARGET_FPS = 30;
			final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;

			timer.start();

			while (run) {
				// Prepare a new cycle
				lastLoopTime = System.nanoTime();

				// Collision
				switch(level.getHitbox(players.get(0).getNewGridPos()[0], players.get(0).getNewGridPos()[1])){
				case NONE:
					players.get(0).changePos();
					break;
				case BREAKABLE:
					players.get(0).setScore(1);
					players.get(0).changePos();
					break;
				case PLAYER:
					break;
				case STATIC:
					break;
				}

				// Prevent players from leaving the game area
				for (EntityPlayer entity : players) {
					if (entity.getGridPos()[0] < 0) {
						entity.setGridPosX(0);
					}
					if (entity.getGridPos()[1] < 0) {
						entity.setGridPosY(0);
					}
					if (entity.getGridPos()[0] > gameGrid[0]-1) {
						entity.setGridPosX(gameGrid[0]-1);
					}
					if (entity.getGridPos()[1] > gameGrid[1]-1) {
						entity.setGridPosY(gameGrid[1]-1);
					}
				}

				// Wait for the next cycle
				Thread.sleep((lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000);
			}
		} catch (InterruptedException e) {
			System.out.println("Game Exception!");
			e.printStackTrace(System.err);
		}
	}

}
