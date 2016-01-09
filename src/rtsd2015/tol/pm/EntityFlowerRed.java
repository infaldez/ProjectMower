package rtsd2015.tol.pm;

import rtsd2015.tol.pm.enums.Side;
import rtsd2015.tol.pm.enums.Tile;

public class EntityFlowerRed extends Entity {
	public EntityFlowerRed(Side s, int x, int y) {
		side = s;
		breakable = true;
		interactionScore = 1;
		isTarget = true;
		setGridPos(x, y);
		setTile(Tile.FLOWER_RED);
	}

	public long getInteractionScore(Side side) {
		if(side == Side.BLUE){
			return -interactionScore;
		}
		return interactionScore;
	}

}