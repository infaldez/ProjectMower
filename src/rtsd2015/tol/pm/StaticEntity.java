package rtsd2015.tol.pm;

public class StaticEntity {
	private int[] position;
	private int health;
	
	StaticEntity() {
		setPos(0,0);
	}
	
	void setPos(int x, int y) {
		position[0] = x;
		position[1] = y;
	}
	
	int[] getPos() {
		return position;
	}
	
	/**
	 * Decrease entity health
	 * 
	 * @param health points
	 * @return true if alive, false if dead
	 */
	void decreaseHealth(int h) {
		health -= h;
	}
	
	/**
	 * Determine whether entity is alive or not
	 * 
	 * @return
	 */
	private boolean isAlive() {
		if (health <= 0) {
			return false;
		} else {
			return true;
		}
	}
	
}
