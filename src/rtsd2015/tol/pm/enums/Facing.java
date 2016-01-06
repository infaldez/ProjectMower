package rtsd2015.tol.pm.enums;

/**
 * Different direction possibilities for players and other entities.
 *
 * @author Ari
 */
public enum Facing {
	NORTH(0),
	EAST(90),
	SOUTH(180),
	WEST(270);

	private int dirs;

	private Facing(int dirs) {
		this.dirs = dirs;
	}

	public int getDirections() {
		return dirs;
	}

	public static final Facing values[] = values();

}
