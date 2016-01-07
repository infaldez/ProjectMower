package rtsd2015.tol.pm;

import java.lang.ThreadLocal;

import rtsd2015.tol.pm.enums.Facing;
import rtsd2015.tol.pm.enums.Hitbox;
import rtsd2015.tol.pm.enums.Movement;
import rtsd2015.tol.pm.enums.Side;
import rtsd2015.tol.pm.enums.Tile;

/**
 * Base class for all entities
 *
 * @author Ari
 */
public class Entity {

	// ThreadLocal to separate client and server thread
	public ThreadLocal<Integer> entityCount = new ThreadLocal<Integer>() {
		@Override protected Integer initialValue() {
			return new Integer(0);
		}
	};

	protected int id;
	protected int[] position = new int[2];
	protected int[] gridpos = new int[2];
	protected int speed;
	protected int health;
	protected Facing dir = Facing.NORTH;
	protected Hitbox hitbox = Hitbox.NONE;
	protected Side side;
	protected Tile tile;
	protected boolean breakable = true;
	protected boolean alive = true;

	/**
	 * Initialize entity
	 *
	 */
	Entity() {
		id = entityCount.get();
		entityCount.set(id + 1);
	}

	/**
	 * Return entity id.
	 *
	 * @return id
	 */
	int getId() {
		return id;
	}

	/**
	 * Set entity position
	 *
	 * @param x
	 * @param y
	 */
	protected void setPos(int x, int y) {
		this.position[0] = x;
		this.position[1] = y;
	}

	/**
	 * Set entity position, based on grid coorinates
	 *
	 * @param x
	 * @param y
	 */
	protected void setGridPos(int x, int y) {
		this.gridpos[0] = x;
		this.gridpos[1] = y;
	}

	public void setGridPosX(int x) {
		this.gridpos[0] = x;
	}

	public void setGridPosY(int y) {
		this.gridpos[1] = y;
	}

	/**
	 * Return entity position
	 *
	 * @return position (x, y)
	 */
	public int[] getPos() {
		return position;
	}

	public int[] getGridPos() {
		return gridpos;
	}

	protected void setTile(Tile tile) {
		this.tile = tile;
	}

	public Tile getTile() {
		return this.tile;
	}

	/**
	 * Return entity speed
	 *
	 * @return speed
	 */
	int getSpeed() {
		return speed;
	}

	/**
	 * Set entity speed
	 * @param newSpeed
	 */
	void setSpeed(int newSpeed) {
		this.speed = newSpeed;
	}

	/**
	 * Return side
	 *
	 * @return Side
	 */
	Side getSide() {
		return side;
	}

	/**
	 * Set a new direction for the entity
	 *
	 * @param newDir
	 */
	void setDir(Facing d) {
		this.dir = d;
	}

	/**
	 * Return entity direction
	 *
	 * @return Facing
	 */
	Facing getDir() {
		return dir;
	}

	/**
	 * Move entity
	 *
	 * @param Movement
	 * @param steps
	 */
	void move(Movement m, int steps) {
		if (m == Movement.BACKWARD) {
			steps *= -1;
		}
		switch (dir) {
		case NORTH:
			position[1] -= steps;
			break;
		case EAST:
			position[0] += steps;
			break;
		case SOUTH:
			position[1] += steps;
			break;
		case WEST:
			position[1] -= steps;
			break;
		}
	}

	/**
	 * Turn entity
	 *
	 * @param turn-direction
	 */
	void turn(Movement t) {
		switch (t) {
		case LEFT:
			// TODO: Implement turning
			break;
		case RIGHT:
			// TODO: Implement turning
			break;
		default:
			break;
		}
	}

	/**
	 * Increase entity health
	 *
	 * @param amount
	 */
	void increaseHealth(int h) {
		this.health += h;
	}

	/**
	 * Decrease entity health
	 *
	 * @param amount
	 */
	void decreaseHealth(int h) {
		health -= h;
		if (health <= 0 && breakable == true) {
			this.alive = false;
		}
	}

	/**
	 * Return entity health
	 *
	 * @return health
	 */
	int getHealth() {
		return health;
	}

	void setHealth(int newHealth) {
		this.health = newHealth;
	}

	/**
	 * Determine whether entity is alive or not
	 *
	 * @return
	 */
	boolean isAlive() {
		return alive;
	}

	/**
	 * Sets hit-box type for the entity
	 *
	 * @param h
	 */
	public void setHitbox(Hitbox h) {
		this.hitbox = h;
	}

	/**
	 * Returns the hit-box type
	 *
	 * @return Hitbox
	 */
	public Hitbox getHitbox() {
		return hitbox;
	}

}