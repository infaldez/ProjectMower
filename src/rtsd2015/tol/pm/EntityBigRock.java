package rtsd2015.tol.pm;

import rtsd2015.tol.pm.enums.Hitbox;
import rtsd2015.tol.pm.enums.Tile;

public class EntityBigRock extends Entity {
	public EntityBigRock(int x, int y) {
		breakable = true;
		setGridPos(x, y);
		setTile(Tile.BIG_ROCK);
		setHitbox(Hitbox.STATIC);
	}
}
