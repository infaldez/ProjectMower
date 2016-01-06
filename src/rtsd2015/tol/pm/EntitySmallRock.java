package rtsd2015.tol.pm;

import rtsd2015.tol.pm.enums.Tile;

public class EntitySmallRock extends Entity {
	EntitySmallRock(int x, int y) {
		breakable = true;
		setGridPos(x, y);
		setTile(Tile.SMALL_ROCK);
	}
}
