package rtsd2015.tol.pm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Game level, including all static entities
 *
 * @author Ari
 */
public class Level {
	private static int[] area;
	static List<EntityTree> trees = new ArrayList<>();

	/**
	 * Initialize a new level The idea is to use a seed to create randomized
	 * levels
	 *
	 * @param size-x
	 * @param size-y
	 * @param seed
	 */
	Level(int x, int y) {
		area = new int[2];
		area[0] = x;
		area[1] = y;
		int treeAmount = area[0] / Math.round(10); // for testing purposes
		addTrees(treeAmount);
	}

	private void addTrees(int count) {
		while (count > 0) {
			int x = getSeedIntValue(area[0], count + 64); // some ugly tricks
			int y = getSeedIntValue(area[1], count + 128);
			trees.add(new EntityTree(x, y));
			count--;
		}
	}

	private int getSeedIntValue(int range, int noise) {
		Random generator = new Random(Game.seed + noise);
		int value = generator.nextInt(range);
		return value;
	}

}
