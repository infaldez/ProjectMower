package rtsd2015.tol.pm;

import java.lang.*;
import rtsd2015.tol.pm.enums.Facing;
import rtsd2015.tol.pm.enums.Movement;
import rtsd2015.tol.pm.enums.Side;

/**
 * Base class for all entities
 *
 * @author Ari
 */
public class Entity {

	public static int entityCount;

	protected int id;
	protected int[] position = new int[2];
	protected int[] gridpos = new int[2];
	protected int speed;
	protected int health;
	protected Facing dir;
	protected Side side;
	protected boolean breakable = true;
	protected boolean alive = true;

	/**
	 * Initialize entity
	 *
	 */
	Entity() {
		id = entityCount;
		entityCount++;
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
		position[0] = x;
		position[1] = y;
	}

	/**
	 * Set entity position, based on grid coorinates
	 *
	 * @param x
	 * @param y
	 */
	protected void setGridPos(int x, int y) {
		gridpos[0] = x;
		gridpos[1] = y;
	}

	/**
	 * Return entity position
	 *
	 * @return position (x, y)
	 */
	int[] getPos() {
		return position;
	}

	int[] getGridPos() {
		return gridpos;
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
		speed = newSpeed;
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
		dir = d;
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
		}
	}

	/**
	 * Increase entity health
	 *
	 * @param amount
	 */
	void increaseHealth(int h) {
		health += h;
	}

	/**
	 * Decrease entity health
	 *
	 * @param amount
	 */
	void decreaseHealth(int h) {
		health -= h;
		if (health <= 0 && breakable == true) {
			alive = false;
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
		health = newHealth;
	}

	/**
	 * Determine whether entity is alive or not
	 *
	 * @return
	 */
	boolean isAlive() {
		return alive;
	}
	
}