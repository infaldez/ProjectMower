package rtsd2015.tol.pm;

import rtsd2015.tol.pm.enums.Tile;

public class EntityTree extends Entity {

	/**
	 * Initialize entity
	 *
	 * @param x
	 * @param y
	 */
	EntityTree(int x, int y) {
		breakable = false;
		setGridPos(x, y);
		setTile(Tile.TREE);
	}
}