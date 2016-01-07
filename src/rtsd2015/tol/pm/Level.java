package rtsd2015.tol.pm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import rtsd2015.tol.pm.enums.Hitbox;
import rtsd2015.tol.pm.enums.Side;
import rtsd2015.tol.pm.enums.WorldFillProbability;

/**
 * Game level, including all static entities
 *
 * @author Ari, Janne
 */
public class Level {

	private List<Object> staticEntities = new ArrayList <Object>();
	private List<Object> dynamicEntities = new ArrayList <Object>();
	private Hitbox[][] hitboxBoard;
	private Object[][] entityBoard;

	private long seed;
	private int area;
	private int width, height;
	private Object entity;

	private Random randomFill = new Random();
	private Random randomFillDynamic = new Random();

	/**
	 * Initializes a new level based on seed
	 *
	 * @param seed
	 * @param width
	 * @param height
	 */
	Level(long seed, int width, int height, double density) {
		this.seed = seed;
		this.width = width;
		this.height = height;
		this.area = width * height;
		this.hitboxBoard = new Hitbox[height][width];
		this.entityBoard = new Object[height][width];

		initSurfaceEntities();
		initMissionEntities();
		initWorldEntities(density);
	}

	/**
	 * Initializes the surface of the board, also known as ground
	 *
	 */
	private void initSurfaceEntities() {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (i == 0 || j == 0 || i == width-1 || j == height-1) {
					entity = new EntityDirt(i, j);
					staticEntities.add(entity);
					entityBoard[i][j] = entity;
				} else {
					entity = new EntityGrass(i, j);
					staticEntities.add(entity);
					entityBoard[i][j] = entity;
				}
				hitboxBoard[i][j] = Hitbox.NONE;
			}
		}
	}

	/**
	 * Populates world board with mission entities, like flowers
	 *
	 */
	private void initMissionEntities() {
		double toBePlacedB = Math.floor(area / 16);
		double toBePlacedR = Math.floor(area / 16);
		int count = 0;
		int limit = area * 2;
		while (toBePlacedB > 0 && count < limit) {
			count++;
			int x = randomFillDynamic.nextInt(width-2) + 1;
			int y = randomFillDynamic.nextInt(height-2) + 1;
			if (hitboxBoard[x][y] == Hitbox.NONE) {
				entity = new EntityFlowerBlue(Side.BLUE, x, y);
				dynamicEntities.add(entity);
				entityBoard[x][y] = entity;
				hitboxBoard[x][y] = Hitbox.BREAKABLE;
				toBePlacedB--;
			}
		}
		count = 0;
		while (toBePlacedR > 0 && count < limit) {
			count++;
			int x = randomFillDynamic.nextInt(width-2) + 1;
			int y = randomFillDynamic.nextInt(height-2) + 1;
			if (hitboxBoard[x][y] == Hitbox.NONE) {
				entity = new EntityFlowerRed(Side.RED, x, y);
				dynamicEntities.add(entity);
				entityBoard[x][y] = entity;
				hitboxBoard[x][y] = Hitbox.BREAKABLE;
				toBePlacedR--;
			}
		}
	}

	/**
	 * Populates world board with static entities, like trees and rocks
	 *
	 * @param density the density orders how filled the final board will be
	 */
	private void initWorldEntities(double density) {
		boolean placed = false;
		if (density >= 1) {
			density = density / 100;
		}
		if (density < 0) {
			density = 0;
		}
		for (int i = 1; i < height-1; i++) {
			for (int j = 1; j < width-1; j++) {
				placed = false;
				if (getRandomBoolean(density) && hitboxBoard[i][j] == Hitbox.NONE) {
					if (!placed && WorldFillProbability.BIGROCK.getWillBePlaced()) {
						placed = true;
						entity = new EntityBigRock(i, j);
						staticEntities.add(entity);
						entityBoard[i][j] = entity;
						hitboxBoard[i][j] = Hitbox.STATIC;
					}
					if (!placed && WorldFillProbability.SMALLROCK.getWillBePlaced()) {
						placed = true;
						entity = new EntitySmallRock(i, j);
						dynamicEntities.add(entity);
						entityBoard[i][j] = entity;
						hitboxBoard[i][j] = Hitbox.BREAKABLE;
					}
					if (!placed && WorldFillProbability.TREE.getWillBePlaced()) {
						placed = true;
						entity = new EntityTree(i, j);
						staticEntities.add(entity);
						entityBoard[i][j] = entity;
						hitboxBoard[i][j] = Hitbox.STATIC;
					}
				}
			}
		}
	}

	public boolean isInsideBoard(int x, int y) {
		if (x > 0 && y > 0) {
			if (x < width-1 && y < height-1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns random boolean, but can be weighed with a probability number
	 *
	 * @param propability
	 * @return boolean
	 */
	private boolean getRandomBoolean(double propability) {
		return 0 + (1 - 0) * randomFill.nextDouble() < propability;
	}

	/**
	 * Returns a list of all static entities
	 *
	 * @return
	 */
	public List<Object> getStaticEntities() {
		return this.staticEntities;
	}

	/**
	 * Returns a list of all dynamic entities
	 *
	 * @return
	 */
	public List<Object> getDynamicEntities() {
		return this.dynamicEntities;
	}

	/**
	 * Returns a hitbox of a certain grid coordinate
	 *
	 * @param x
	 * @param y
	 * @return Hitbox
	 */
	public Hitbox getHitbox(int x, int y) {
		if (x < 0) {x = 0;}
		if (y < 0) {y = 0;}
		if (x >= width) {x = width-1;}
		if (y >= height) {y = height-1;}
		return this.hitboxBoard[x][y];
	}

	/**
	 * Returns an entity of given grid position
	 *
	 * @param x grid position
	 * @param y grid position
	 * @return Entity
	 */
	public Object getEntity(int x, int y) {
		return this.entityBoard[x][y];
	}

	public int getWidth() { return this.width; }
	public int getHeight() { return this.height; }
	public long getSeed() { return this.seed; }
}
