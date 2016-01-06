package rtsd2015.tol.pm;

import java.util.ArrayList;

import rtsd2015.tol.pm.enums.Facing;
import rtsd2015.tol.pm.enums.Side;
import rtsd2015.tol.pm.enums.Tile;

/**
 * The player entity
 *
 * @author Ari, Janne
 */
public class EntityPlayer extends Entity {

	public static int playerCount;
	private int score = 0;

	/**
	 * Initialize player
	 *
	 * @param side
	 */
	EntityPlayer(Side s) {
		side = s;
		health = 3;
		speed = 1;
		// TODO: Properties file
		switch (side) {
		case BLUE:
			setDir(Facing.EAST);
			setTile(Tile.PLAYER1);
			// TODO: Define BLUE player starting position
			break;
		case RED:
			setDir(Facing.WEST);
			setTile(Tile.PLAYER2);
			// TODO: Define RED player starting position
			break;
		default:
			setDir(Facing.NORTH);
			setTile(Tile.PLAYER1);
			break;
		}
		playerCount++;
	}

	public void setScore() {
		this.score++;
	}

	public int getScore() {
		return this.score;
	}

}