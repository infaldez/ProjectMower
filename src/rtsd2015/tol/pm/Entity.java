package rtsd2015.tol.pm;

public class Entity {

	public static int entityCount;

	private int[] position;
	protected int health;
	protected boolean unbreakable = false;

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
	 * Decrease entity health
	 * 
	 * @param amount
	 */
	protected void decreaseHealth(int h) {
		health -= h;
	}

	/**
	 * Determine whether entity is alive or not
	 * 
	 * @return
	 */
	protected boolean isAlive() {
		if (health <= 0 && unbreakable == false) {
			return false;
		} else {
			return true;
		}
	}

}