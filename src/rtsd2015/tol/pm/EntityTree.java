package rtsd2015.tol.pm;

import rtsd2015.tol.pm.enums.Hitbox;
import rtsd2015.tol.pm.enums.Tile;

public class EntityTree extends Entity {

	/**
	 * Initialize entity
	 *
	 * @param x
	 * @param y
	 */
	public EntityTree(int x, int y) {
		breakable = false;
		setGridPos(x, y);
		setTile(Tile.TREE);
		setHitbox(Hitbox.STATIC);
	}
}