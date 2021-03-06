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

	private List<Entity> staticEntities = new ArrayList <Entity>();
	private List<Entity> dynamicEntities = new ArrayList <Entity>();
	private Hitbox[][] hitboxBoard;
	private Entity[][] entityBoard;

	private long seed;
	private double density;
	private int area;
	private int width, height;
	private int areaWidth, areaHeight;
	private double targetB = 0;
	private double targetR = 0;

	private Random random = new Random();

	/**
	 * Initializes a new level based on seed
	 *
	 * @param seed
	 * @param width
	 * @param height
	 */
	Level(long seed, int width, int height, double density) {
		this.seed = seed;
		this.random.setSeed(seed);
		this.width = width;
		this.areaWidth = width - 2;
		this.height = height;
		this.areaHeight = height - 2;
		this.area = width * height;
		this.density = density;
		this.hitboxBoard = new Hitbox[height][width];
		this.entityBoard = new Entity[height][width];

		initSurfaceEntities();
		initMissionEntities();
		initWorldEntities(density);
	}

	/**
	 * Rebuilds (refreshes) the entire level
	 *
	 */
	public void rebuild() {
		staticEntities.clear();
		dynamicEntities.clear();
		initSurfaceEntities();
		initMissionEntities();
		initWorldEntities(density);
	}

	/**
	 * Initializes the surface of the board, also known as ground
	 *
	 */
	private void initSurfaceEntities() {
		Entity entity;
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
		Entity entity;
		double toBePlacedB = Math.floor(area / 16);
		double toBePlacedR = Math.floor(area / 16);
		int count = 0;
		int limit = area * 2;
		while (toBePlacedB > 0 && count < limit) {
			count++;
			int x = getRandomInt(areaWidth, 2);
			int y = getRandomInt(areaHeight, 2);
			if (hitboxBoard[x][y] == Hitbox.NONE) {
				entity = new EntityFlowerBlue(Side.BLUE, x, y);
				targetR++;
				dynamicEntities.add(entity);
				entityBoard[x][y] = entity;
				hitboxBoard[x][y] = Hitbox.BREAKABLE;
				toBePlacedB--;
			}
		}
		count = 0;
		while (toBePlacedR > 0 && count < limit) {
			count++;
			int x = getRandomInt(areaWidth, 2);
			int y = getRandomInt(areaHeight, 2);
			if (hitboxBoard[x][y] == Hitbox.NONE) {
				entity = new EntityFlowerRed(Side.RED, x, y);
				targetB++;
				dynamicEntities.add(entity);
				entityBoard[x][y] = entity;
				hitboxBoard[x][y] = Hitbox.BREAKABLE;
				toBePlacedR--;
			}
		}
	}

	/**
	 * Returns a random integer based on a seed value
	 *
	 * @param max
	 * @param min
	 * @return integer
	 */
	private int getRandomInt(int max, int min) {
		return random.nextInt((max-min) + min) + 1;
	}

	/**
	 * Populates world board with static entities, like trees and rocks
	 *
	 * @param density the density orders how filled the final board will be
	 */
	private void initWorldEntities(double density) {
		Entity entity;
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
					if (!placed && getWillBePlaced(WorldFillProbability.BIGROCK)) {
						placed = true;
						entity = new EntityBigRock(i, j);
						setRandomRenderAngle(entity);
						staticEntities.add(entity);
						entityBoard[i][j] = entity;
						hitboxBoard[i][j] = Hitbox.STATIC;
					}
					if (!placed && getWillBePlaced(WorldFillProbability.SMALLROCK)) {
						placed = true;
						entity = new EntitySmallRock(i, j);
						setRandomRenderAngle(entity);
						dynamicEntities.add(entity);
						entityBoard[i][j] = entity;
						hitboxBoard[i][j] = Hitbox.BREAKABLE;
					}
					if (!placed && getWillBePlaced(WorldFillProbability.TREE)) {
						placed = true;
						entity = new EntityTree(i, j);
						setRandomRenderAngle(entity);
						staticEntities.add(entity);
						entityBoard[i][j] = entity;
						hitboxBoard[i][j] = Hitbox.STATIC;
					}
				}
			}
		}
	}

	/**
	 * Returns whether the given WorldFillProbability entity will be placed
	 *
	 * @param WorldFillProbability
	 * @return true to yes
	 */
	private boolean getWillBePlaced(WorldFillProbability probability) {
		if (getRandomInt(100,0) < probability.getPropability()) {
			return true;
		}
		return false;
	}

	/**
	 * Returns whether the given coordinate is inside the level
	 *
	 * @param x horizontal grid coordinate
	 * @param y vertical grid coordinate
	 * @return true to is inside
	 */
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
		return 0 + (1 - 0) * random.nextDouble() < propability;
	}

	/**
	 * Returns a list of all static entities
	 *
	 * @return
	 */
	public List<Entity> getStaticEntities() {
		return this.staticEntities;
	}

	/**
	 * Returns a list of all dynamic entities
	 *
	 * @return
	 */
	public List<Entity> getDynamicEntities() {
		return this.dynamicEntities;
	}

	/**
	 * Returns a hitbox of a certain grid coordinate
	 *
	 * @param x
	 * @param y
	 * @return Hitbox
	 */
	public Hitbox getHitbox(int[] pos) {
		return getHitbox(pos[0], pos[1]);
	}

	/**
	 * Returns hitbox of the given coordinate
	 *
	 * @param x
	 * @param y
	 * @return
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
	public Entity getEntity(int[] pos) {
		return this.entityBoard[pos[0]][pos[1]];
	}

	/**
	 * Updates target count of the certain Side
	 *
	 * @param side
	 * @param count
	 */
	public void updateTargetCount(Side side, double count) {
		if (side == Side.BLUE) {targetB = targetB + count;}
		if (side == Side.RED) {targetR = targetR + count;}
	}

	/**
	 * The exact count of targets left
	 *
	 * @param side
	 * @return
	 */
	public double getTargetCount(Side side) {
		if (side == Side.BLUE) {return targetB;}
		if (side == Side.RED) {return targetR;}
		return 0;
	}

	/**
	 * Randomize entity's render angle (local)
	 *
	 * @param entity
	 */
	public void setRandomRenderAngle(Entity entity) {
		entity.setRenderAngle(getRandomInt(360, 0));
	}

	/**
	 * Returns whether there are more targets left of the certain Side
	 *
	 * @param side
	 * @return boolean
	 */
	public boolean hasTargetsLeft(Side side) {
		if (side == Side.BLUE && targetB < 1) {return false;}
		if (side == Side.RED && targetR < 1) {return false;}
		return true;
	}

	public int getWidth() { return this.width; }
	public int getHeight() { return this.height; }
	public long getSeed() { return this.seed; }
}
