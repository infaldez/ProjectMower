package rtsd2015.tol.pm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rtsd2015.tol.pm.enums.Tile;

/**
 * Game level, including all static entities
 *
 * @author Ari
 */
public class Level {

	static List<EntityTree> trees = new ArrayList<>(); // TODO: Implement

	private long seed;
	private int area;
	private int width, height;
	protected Tile[][] board;
	private List<Object> worldEntities = new ArrayList <Object>();

	Random random;

	/**
	 * Initializes a new level based on seed
	 *
	 * @param seed
	 * @param width
	 * @param height
	 */
	Level(long seed, int width, int height) {
		this.seed = seed;
		this.random = new Random(seed);
		this.width = width;
		this.height = height;
		this.area = width * height;

		addGrass();

		int bigRocksNumber = area / 200;
		int treeNumber = area / 100;
		int smallRocksNumber = area / 50;

		placeTile(Tile.BIG_ROCK, bigRocksNumber);
		placeTile(Tile.TREE, treeNumber);
		placeTile(Tile.SMALL_ROCK, smallRocksNumber);

	}

	public Tile[][] getBoard() {
		return board;
	}

	private void placeTile(Tile tile) {
		placeTile(tile, 1);
	}

	private void placeTile(Tile tile, int count) {
		for (int i = 0; i < count; i++) {
			boolean placed = false;
			int x, y;
			while (!placed) {
				x = (int) Math.floor(this.random.nextDouble() * width);
				y = (int) Math.floor(this.random.nextDouble() * height);
				if (board[x][y] == Tile.GRASS) {
					board[x][y] = tile;
					placed = true;
					switch (tile) {
						case BIG_ROCK: worldEntities.add(new EntityBigRock(x,y));
						break;
						case TREE: worldEntities.add(new EntityTree(x,y));
						break;
						case SMALL_ROCK: worldEntities.add(new EntitySmallRock(x,y));
						break;
					}
				}
			}
		}
	}

	private void addGrass() {
		this.board = new Tile[height][width];
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				board[i][j] = Tile.GRASS;
				worldEntities.add(new EntityGrass(i,j));
			}
		}
	}
}
