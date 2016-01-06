package rtsd2015.tol.pm;

import rtsd2015.tol.pm.enums.Tile;

public class EntityGrass extends Entity {
	EntityGrass(int x, int y) {
		breakable = true;
		setGridPos(x, y);
		setTile(Tile.GRASS);
	}
}
