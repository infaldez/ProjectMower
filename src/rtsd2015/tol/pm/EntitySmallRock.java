package rtsd2015.tol.pm;

import rtsd2015.tol.pm.enums.Hitbox;
import rtsd2015.tol.pm.enums.Tile;

public class EntitySmallRock extends Entity {
	public EntitySmallRock(int x, int y) {
		breakable = true;
		interactionScore = -1;
		setGridPos(x, y);
		setTile(Tile.SMALL_ROCK);
		setHitbox(Hitbox.BREAKABLE);
	}
}
