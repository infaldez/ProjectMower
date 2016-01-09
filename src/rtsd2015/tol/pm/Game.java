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
	private volatile boolean run = true;

	private EntityPlayer player1;
	private EntityPlayer player2;

	private List<Entity> updatedEntities;
	private List<Integer> killedEntities; //Ids of killed entities
	boolean inGame = true;

	private Side winner = Side.GAIA;

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
		player1 = players.get(0);
		player2 = players.get(1);

		updatedEntities = new ArrayList<Entity>();
		killedEntities = new ArrayList<Integer>();
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

	/**
	 * Returns a list of players
	 *
	 * @return List
	 */
	public List<EntityPlayer> getPlayers() {
		return players;
	}

	/**
	 * Returns a list of InterfaceText objects
	 *
	 * @return List
	 */
	public List<InterfaceText> getInterfaceTexts() {
		return this.interfaceTexts;
	}

	/**
	 * Returns the winner of the game, GAIA if nobody
	 *
	 * @return Side
	 */
	public Side getWinner() {
		return this.winner;
	}

	/**
	 * Sets winner for the game
	 *
	 * @param side
	 */
	public void setWinner(Side side) {
		this.winner = side;
		System.out.println("Winner: " + side);
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
	 * Marks as updated
	 *
	 * @param id
	 */
	public void markUpdated(int id) {
		Entity entity = Entity.getEntity(id);
		if(entity != null) {
			updatedEntities.add(entity);
		}
	}

	/**
	 * Get and clear the list of updated entities
	 *
	 * @return entity list
	 */
	public ArrayList<Entity> flushUpdatedEntities() {
		ArrayList<Entity> copy = new ArrayList<Entity>(updatedEntities);
		updatedEntities = new ArrayList<Entity>();
		return copy;
	}

	/**
	 * Get and clear the list of killed entity ids
	 *
	 * @return entity list
	 */
	public ArrayList<Integer> flushKilled() {
		ArrayList<Integer> copy = new ArrayList<Integer>(killedEntities);
		killedEntities = new ArrayList<Integer>();
		return copy;
	}

	/**
	 * Returns a list of scores
	 *
	 * @return List
	 */
	public List<Long> getScores(){
		List<Long> scores = new ArrayList<Long>();
		for (EntityPlayer player : players) {
			scores.add(player.getScore());
		}
		return scores;
	}

	/**
	 * Add a tick
	 *
	 * @return
	 */
	public int increaseTick() {
		return ++tick;
	}

	/**
	 * Get the current tick
	 *
	 * @return
	 */
	public int getTick() {
		return tick;
	}

	/**
	 * Set the current tick
	 *
	 * @param tock
	 */
	public void setTick(int tock) {
		tick = tock;
	}

	/**
	 * Returns whether given side is the winner or not
	 *
	 * @param player
	 * @return boolean true to yes
	 */
	public boolean isWinner(EntityPlayer player) {
		if (getWinner() == player.getSide()) {
			return true;
		}
		return false;
	}

	/**
	 * Performs a tick
	 *
	 * @return (int) tick
	 */
	public int doTick() {
		tick++;

		// Decide whether the player can move
		checkedMove(player1);
		checkedMove(player2);

		if (inGame) {
			if (!level.hasTargetsLeft(player1.getSide())) {
				setInGame(false);
			}
			if (!level.hasTargetsLeft(player2.getSide())) {
				setInGame(false);
			}
		} else {
			if (getWinner() == Side.GAIA) {
				if (player1.getScore() > player2.getScore()) {
					// Player1 wins
					setWinner(player1.getSide());
				} else {
					// Player2 wins
					setWinner(player2.getSide());
				}
			}
		}

		Iterator<Entity> entityIterator = Entity.getEntities().iterator();
		while (entityIterator.hasNext()) {
			Entity entity = entityIterator.next();
			entity.move();
		}

		return tick;
	}

	/**
	 * Stops the game
	 *
	 */
	public void stop() {
		run = false;
	}

	/**
	 * Sets the inGame state
	 *
	 * @param state
	 */
	public void setInGame(boolean state) {
		this.inGame = state;
	}

	/**
	 * Returns the inGame state
	 *
	 * @return
	 */
	public boolean getInGame() {
		return this.inGame;
	}

	/**
	 * Do move and determine consequences.
	 *
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
			Entity hitEntity = level.getEntity(player.getGridPos());
			if(hitEntity.isAlive()){
				hitEntity.setAlive(false);
				if (hitEntity.isTarget()) {
					level.updateTargetCount(hitEntity.getSide(), -1);
				}
				killedEntities.add(hitEntity.getId());
				player.setScore(hitEntity.getInteractionScore(player.getSide()));
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
			InterfaceText txt_gameState = new InterfaceText(16,144, Font.font("Verdana",12), Color.RED);
			interfaceTexts.add(txt_gameState);
			txt_gameState.setTextString("Press ENTER To Begin");

			while (run && !inGame && !Launcher.getAppSandbox()) {
				Thread.sleep(500);
			}

			while (run) {
				// Prepare a new cycle
				lastLoopTime = System.nanoTime();
				// Update UI text strings
				p1_score.setTextString("[ Player 1 ]\nscore:" + pl1.getScore() + "\nhealth: " + pl1.getHealth());
				p2_score.setTextString("[ Player 2 ]\nscore:" + pl2.getScore() + "\nhealth: " + pl2.getHealth());

				if (getWinner() != Side.GAIA) {
					switch(getWinner()) {
						case BLUE:
							txt_gameState.setTextString("Winner: Abel Blue!");
						break;
						case RED:
							txt_gameState.setTextString("Winner: Cain Red!");
						break;
						default:
						break;
					}
				} else {
					txt_gameState.setTextString("Mow competitor's flowers!");
				}

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
