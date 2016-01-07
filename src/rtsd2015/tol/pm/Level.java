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

	private static List<Object> staticEntities = new ArrayList <Object>();
	private static boolean[][] boardSpaceOccupied;

	private long seed;
	private int area;
	private int width, height;
	private double freeStaticSpace = 0;
	private double treeDensity = 0.5;
	private double bigRockDensity = 0.2;
	private double smallRockDensity = 0.3;

	Random random;

	/**
	 * Initializes a new level based on seed
	 *
	 * @param seed
	 * @param width
	 * @param height
	 */
	Level(long seed, int width, int height, double density) {
		this.seed = seed;
		this.random = new Random(seed);
		this.width = width;
		this.height = height;
		this.area = width * height;
		Level.boardSpaceOccupied = new boolean[height][width];

		initStaticSpace(density);
		initSurfaceEntities();
		initStaticEntities();
	}

	/**
	 * Initializes the surface of the board, also known as ground
	 *
	 */
	private void initSurfaceEntities() {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (i == 0 || j == 0 || i == width-1 || j == height-1) {
					staticEntities.add(new EntityDirt(i, j));
				} else {
					staticEntities.add(new EntityGrass(i, j));
				}
			}
		}
	}

	/**
	 * Initializes the density what the object fill will follow
	 *
	 * @param density 0.0-100.0
	 */
	private void initStaticSpace(double density) {
		if (density > 100) {density = 100;};
		if (density < 0) {density = 0;};
		this.freeStaticSpace = Math.floor(area * (density / 100));
	}

	/**
	 * Initializes the static entities like trees and rocks
	 *
	 */
	private void initStaticEntities() {
		double treeCount = Math.floor(freeStaticSpace * treeDensity);
		double smallRockCount = Math.floor(freeStaticSpace * smallRockDensity);
		double bigRockCount = Math.floor(freeStaticSpace * bigRockDensity);
		int x, y;
		boolean populated;
		while (freeStaticSpace > 0) {
			freeStaticSpace--;
			populated = false;
			x = (int) Math.floor(this.random.nextDouble() * width);
			y = (int) Math.floor(this.random.nextDouble() * height);
			if (!boardSpaceOccupied[x][y] && x != 0 && y != 0 && x != height-1 && y != width-1) {
				boardSpaceOccupied[x][y] = true;
				if (treeCount > 0) {
					treeCount--;
					staticEntities.add(new EntityTree(x, y));
					populated = true;
				}
				if (smallRockCount > 0 && !populated) {
					smallRockCount--;
					staticEntities.add(new EntitySmallRock(x, y));
					populated = true;
				}
				if (bigRockCount > 0 && !populated) {
					bigRockCount--;
					staticEntities.add(new EntityBigRock(x, y));
					populated = true;
				}
			}
		}
	}

	/**
	 * returns a list of all static entities
	 *
	 * @return
	 */
	public List<Object> getStaticEntities() {
		return Level.staticEntities;
	}

	public int getWidth() { return this.width; }
	public int getHeight() { return this.height; }
	public long getSeed() { return this.seed; }
}
