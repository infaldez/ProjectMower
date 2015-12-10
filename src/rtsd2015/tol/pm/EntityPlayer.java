package rtsd2015.tol.pm;

import java.util.ArrayList;

import rtsd2015.tol.pm.enums.Facing;
import rtsd2015.tol.pm.enums.Side;

/**
 * The player entity
 * 
 * @author Ari
 */
public class EntityPlayer extends Entity {

	public static int playerCount;

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
			// TODO: Define BLUE player starting position
			break;
		case RED:
			setDir(Facing.WEST);
			// TODO: Define RED player starting position
			break;
		default:
			setDir(Facing.NORTH);
			break;
		}
		playerCount++;
	}

}