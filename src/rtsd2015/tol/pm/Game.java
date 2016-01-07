package rtsd2015.tol.pm;

import java.util.ArrayList;
import java.util.List;
import rtsd2015.tol.pm.enums.Facing;
import rtsd2015.tol.pm.enums.Side;

public class Game implements Runnable {

	private int[] gameGrid = new int[2];
	private int tick;
	private Level level;
	private List<EntityPlayer> players = new ArrayList<>();
	private List<InterfaceText> interfaceTexts = new ArrayList<>();
	private Timer timer = new Timer();
	private boolean run = true;

	private List<Entity> updatedEntities;

	/**
	 * Initializes a new game for both clients and a server
	 *
	 */
	Game(long sd) {
		tick = 0;
		gameGrid[0] = 16;
		gameGrid[1] = 16;
		this.level = new Level(sd, gameGrid[0], gameGrid[1], 40);
		players.add(new EntityPlayer(Side.BLUE, 0, 0));
		players.add(new EntityPlayer(Side.RED, gameGrid[0], gameGrid[1]));

		updatedEntities = new ArrayList<Entity>();
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

	public List<InterfaceText> getInterfaceTexts() {
		return this.interfaceTexts;
	}

	/**
	 * Update entity defined by id.
	 *
	 */
	public Entity updateEntity(int id, int x, int y, Facing dir, int speed, int health) {
		// TODO Get entity from entity list by id
		Entity entity = Entity.getEntity(id);
		// Update entity
		if(entity != null) {
			entity.setGridPos(x, y);
			entity.setDir(dir);
			entity.setSpeed(speed);
			entity.setHealth(health);
		}

		return entity;
	}

	/**
	 * Get and clear the list of updated entitites
	 * @return entity list
	 */
	public List<Entity> flushUpdatedEntities() {
		List<Entity> copy = new ArrayList<Entity>(updatedEntities);
		updatedEntities.clear();
		return copy;
	}

	public int increaseTick() {
		return ++tick;
	}

	public int getTick() {
		return tick;
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

			EntityPlayer pl1 = players.get(0);
			EntityPlayer pl2 = players.get(1);

			InterfaceText p1_score = new InterfaceText(16,32);
			interfaceTexts.add(p1_score);
			InterfaceText p2_score = new InterfaceText(16,96);
			interfaceTexts.add(p2_score);
			InterfaceText time = new InterfaceText(16,160);
			interfaceTexts.add(time);

			timer.start();

			while (run) {
				// Prepare a new cycle
				lastLoopTime = System.nanoTime();

				p1_score.setTextString("[ Player 1 ]\nscore:" + pl1.getScore() + "\nhealth: " + pl1.getHealth());
				p2_score.setTextString("[ Player 2 ]\nscore:" + pl2.getScore() + "\nhealth: " + pl2.getHealth());
				time.setTextString("Time Left: ");

				players.get(0).move();

				switch(level.getHitbox(players.get(0).getNewGridPos()[0], players.get(0).getNewGridPos()[1])){
				case NONE:
					players.get(0).changePos();
					break;
				case BREAKABLE:
					players.get(0).changePos();
					if(((Entity) level.getEntity(players.get(0).getGridPos()[0], players.get(0).getGridPos()[1])).isAlive()){
						players.get(0).setScore(((Entity) level.getEntity(players.get(0).getGridPos()[0], players.get(0).getGridPos()[1])).getInteractionScore(players.get(0).getSide()));
						((Entity) level.getEntity(players.get(0).getGridPos()[0], players.get(0).getGridPos()[1])).setAlive(false);
					}
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
