package rtsd2015.tol.pm;

import rtsd2015.tol.pm.enums.Side;
import rtsd2015.tol.pm.enums.Tile;

public class EntityFlowerBlue extends Entity {
	public EntityFlowerBlue(Side s, int x, int y) {
		side = s;
		breakable = true;
		interactionScore = 1;
		setGridPos(x, y);
		setTile(Tile.FLOWER_BLUE);
	}
}
