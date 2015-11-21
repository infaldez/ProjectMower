package rtsd2015.tol.pm;

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

	public int id;
	protected int[] position;
	protected int speed = 0;
	protected int health = 0;
	protected Facing dir = Facing.NORTH;
	protected Side side = Side.GAIA;
	protected boolean breakable = true;

	/**
	 * Initialize entity
	 * 
	 */
	Entity() {
		position = new int[2];
		id = entityCount;
		entityCount++;
	}

	/**
	 * Set entity position
	 * 
	 * @param x
	 * @param y
	 */
	void setPos(int x, int y) {
		position[0] = x;
		position[1] = y;
	}

	/**
	 * Return entity position
	 * 
	 * @return position (x, y)
	 */
	int[] getPos() {
		return position;
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
		switch(dir) {
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
	}
	
	/**
	 * Return entity health
	 * 
	 * @return health
	 */
	int getHealth() {
		return health;
	}

	/**
	 * Determine whether entity is alive or not
	 * 
	 * @return
	 */
	boolean isAlive() {
		if (health <= 0 && breakable == true) {
			return false;
		} else {
			return true;
		}
	}

}