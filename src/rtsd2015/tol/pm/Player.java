package rtsd2015.tol.pm;

import java.util.ArrayList;

import rtsd2015.tol.pm.enums.Facing;
import rtsd2015.tol.pm.enums.Side;

/**
 * The actual player entity
 * 
 * @author Ari
 */
public class Player {
	
	public static int playerCount;
	
	private int health;
	private int speed;
	private int score;
	private Side side;
	private Facing dir;
	private int[] position;

	/**
	 * Initialize a new player
	 * 
	 * @param Side
	 */
	Player(Side s) {
		side = s;
		switch (side) {
		case BLUE:
			setDir(Facing.EAST);
			break;
		case RED:
			setDir(Facing.WEST);
			break;
		}
		playerCount++;
	}

	/**
	 * Set player position
	 * 
	 * @param x
	 * @param y
	 */
	void setPos(int x, int y) {
		position[0] = x;
		position[1] = y;
	}

	/**
	 * Get player position
	 * 
	 * @return (x,y)
	 */
	int[] getPos() {
		return position;
	}

	/**
	 * Return player's side
	 * 
	 * @return Side
	 */
	Side getSide() {
		return side;
	}

	/**
	 * Set a new direction for the player
	 * 
	 * @param newDir
	 */
	void setDir(Facing d) {
		dir = d;
	}

	/**
	 * Return player's direction
	 * 
	 * @return Facing
	 */
	Facing getDir() {
		return dir;
	}
}
