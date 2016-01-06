package rtsd2015.tol.pm;

import rtsd2015.tol.pm.enums.Tile;

public class EntityDirt extends Entity {
	EntityDirt(int x, int y) {
		breakable = true;
		setGridPos(x, y);
		setTile(Tile.DIRT);
	}
}
