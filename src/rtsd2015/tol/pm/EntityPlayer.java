package rtsd2015.tol.pm;

import java.util.ArrayList;

import rtsd2015.tol.pm.enums.Facing;
import rtsd2015.tol.pm.enums.Hitbox;
import rtsd2015.tol.pm.enums.Side;
import rtsd2015.tol.pm.enums.Tile;

/**
 * The player entity
 *
 * @author Ari, Janne
 */
public class EntityPlayer extends Entity {

	private static ThreadLocal<Integer> playerCount = new ThreadLocal<Integer>() {
		@Override protected Integer initialValue() {
			return new Integer(0);
		}
	};

	private long score = 0;

	/**
	 * Initialize player
	 *
	 * @param side
	 */
	public EntityPlayer(Side s, int x, int y) {
		super();
		side = s;
		health = 3;
		speed = 0;
		setGridPos(x, y);
		setHitbox(Hitbox.PLAYER);
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

		playerCount.set(playerCount.get() + 1);
	}
	
	public static void resetPlayers(){
		playerCount.set(0);
	}
	
	public static int getPlayerCount() {
		return playerCount.get();
	}

	public void setScore(long s) {
		this.score = this.score + s;
	}

	public long getScore() {
		return this.score;
	}

	public void changePos() {
		this.gridpos[0] = this.newgridpos[0];
		this.gridpos[1] = this.newgridpos[1];
	}

	public void move() {
		switch(dir)
		{
		case NORTH:
			this.newgridpos[1] = this.gridpos[1] - speed;
		break;
		case SOUTH:
			this.newgridpos[1] = this.gridpos[1] + speed;
		break;
		case EAST:
			this.newgridpos[0] = this.gridpos[0] + speed;
		break;
		case WEST:
			this.newgridpos[0] = this.gridpos[0] - speed;
		break;
		}
	}

}