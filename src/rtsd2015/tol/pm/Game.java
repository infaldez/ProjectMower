package rtsd2015.tol.pm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import rtsd2015.tol.pm.enums.Facing;
import rtsd2015.tol.pm.enums.Side;

public class Game implements Runnable {

	private int[] gameGrid = new int[2];
	private int tick;
	private Level level;
	private List<EntityPlayer> players = new ArrayList<>();
	private List<InterfaceText> interfaceTexts = new ArrayList<>();
	private Timer timer = new Timer();
	private volatile boolean run = true;

	private List<Entity> updatedEntities;
	boolean inGame = false;

	/**
	 * Initializes a new game for both clients and a server
	 *
	 */
	Game(long sd) {
		Entity.resetEntities();
		EntityPlayer.resetPlayers();
		tick = 0;
		gameGrid[0] = 16;
		gameGrid[1] = 16;
		this.level = new Level(sd, gameGrid[0], gameGrid[1], 40);
		players.add(new EntityPlayer(Side.BLUE, 0, 0));
		players.add(new EntityPlayer(Side.RED, gameGrid[0]-1, gameGrid[1]-1));

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

	public void markUpdated(int id) {
		Entity entity = Entity.getEntity(id);
		if(entity != null) {
			updatedEntities.add(entity);
		}
	}

	/**
	 * Get and clear the list of updated entitites
	 * @return entity list
	 */
	public ArrayList<Entity> flushUpdatedEntities() {
		ArrayList<Entity> copy = new ArrayList<Entity>(updatedEntities);
		updatedEntities = new ArrayList<Entity>();
		return copy;
	}

	public int increaseTick() {
		return ++tick;
	}

	public int getTick() {
		return tick;
	}

	public void setTick(int tock) {
		tick = tock;
	}

	public int doTick() {
		tick++;

		checkedMove(players.get(0));
		checkedMove(players.get(1));

		Iterator<Entity> entityIterator = Entity.getEntities().iterator();
		while (entityIterator.hasNext()) {
			Entity entity = entityIterator.next();
			entity.move();
		}

		return tick;
	}

	public void stop() {
		run = false;
	}

	public void setInGame(boolean state) {
		this.inGame = state;
	}

	public boolean getInGame() {
		return this.inGame;
	}

	/**
	 * Do move and determine consequences.
	 * @param player
	 * @return
	 */
	public void checkedMove(EntityPlayer player) {
		int[] newGridPos = player.getNewGridPos();
		int x = newGridPos[0];
		int y = newGridPos[1];

		// Prevent players from leaving the game area
		if (x < 0 || y < 0 || x > gameGrid[0] - 1 || y > gameGrid[1] - 1) {
			player.resetPos();
		}

		switch(level.getHitbox(newGridPos)){
		case NONE:
			player.changePos();
			break;
		case BREAKABLE:
			player.changePos();
			// If hit entity is breakable kill it and adjust score accordingly
			if(level.getEntity(player.getGridPos()).isAlive()){
				player.setScore(level.getEntity(player.getGridPos()).getInteractionScore(player.getSide()));
				level.getEntity(player.getGridPos()).setAlive(false);
			}
			break;
		case PLAYER:
			player.resetPos();
			break;
		case STATIC:
			player.resetPos();
			break;
		}
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

			// Initialize in-game UI texts
			InterfaceText p1_score = new InterfaceText(16,16, Font.font("Verdana",14), Color.WHITE);
			interfaceTexts.add(p1_score);
			InterfaceText p2_score = new InterfaceText(16,80, Font.font("Verdana",14), Color.WHITE);
			interfaceTexts.add(p2_score);
			InterfaceText time = new InterfaceText(16,144, Font.font("Verdana",14), Color.WHITE);
			interfaceTexts.add(time);
			InterfaceText txt_gameState = new InterfaceText(16,16, Font.font("Verdana",12), Color.RED);
			interfaceTexts.add(txt_gameState);
			txt_gameState.setTextString("Press ENTER To Begin");

			while (!inGame && !Launcher.getAppSandbox()) {
				Thread.sleep(500);
			}

			txt_gameState.setTextString("");
			timer.start();

			while (run) {
				// Prepare a new cycle
				lastLoopTime = System.nanoTime();

				// Update UI text strings
				p1_score.setTextString("[ Player 1 ]\nscore:" + pl1.getScore() + "\nhealth: " + pl1.getHealth());
				p2_score.setTextString("[ Player 2 ]\nscore:" + pl2.getScore() + "\nhealth: " + pl2.getHealth());
				time.setTextString("Time Left: ");

				// Wait for the next cycle if we are ahead
				long sleepTime = (lastLoopTime + OPTIMAL_TIME - System.nanoTime()) / 1000000;
				if (sleepTime > 0) {
					Thread.sleep(sleepTime);
				}
			}
		} catch (InterruptedException e) {
			System.out.println("Game Exception!");
			e.printStackTrace(System.err);
		}
	}

}
