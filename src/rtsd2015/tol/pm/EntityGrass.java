package rtsd2015.tol.pm;

import rtsd2015.tol.pm.enums.Tile;

public class EntityGrass extends Entity {
	public EntityGrass(int x, int y) {
		breakable = false;
		setGridPos(x, y);
		setTile(Tile.GRASS);
	}
}
