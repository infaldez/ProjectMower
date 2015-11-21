package rtsd2015.tol.pm;

import rtsd2015.tol.pm.enums.Facing;
import rtsd2015.tol.pm.enums.Side;

/**
 * Base class for all entities
 * 
 * @author Ari
 */
public class Entity {

	public static int entityCount;

	protected int[] position;
	protected int speed = 0;
	protected int health = 0;
	protected Facing dir = Facing.NORTH;
	protected Side side = Side.GAIA;
	protected boolean breakable = true;

	/**
	 * Update entity counter
	 * 
	 */
	Entity() {
		entityCount++;
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
	 * Return entity position
	 * 
	 * @return position (x, y)
	 */
	protected int[] getPos() {
		return position;
	}
	
	/**
	 * Return side
	 * 
	 * @return Side
	 */
	protected Side getSide() {
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
	 * Increase entity health
	 * 
	 * @param amount
	 */
	protected void increaseHealth(int h) {
		health += h;
	}

	/**
	 * Decrease entity health
	 * 
	 * @param amount
	 */
	protected void decreaseHealth(int h) {
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
	protected boolean isAlive() {
		if (health <= 0 && breakable == true) {
			return false;
		} else {
			return true;
		}
	}

}