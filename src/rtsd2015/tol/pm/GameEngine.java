package rtsd2015.tol.pm;
import rtsd2015.tol.pm.enums.Tile;

import java.util.Random;
import java.lang.*;
import java.util.ArrayList;

public class GameEngine {
	private int tick = 0;
	private long seed;
	Random random;
	private ArrayList<EntityPlayer> players = new ArrayList<EntityPlayer>();
	
	private int area;
	private int width, height;
	private Tile[][] board;
	
	GameEngine (long seed, int size) {
		this(seed, size, size);
	}

	GameEngine (long seed, int width, int height) {
		this.seed = seed;
		this.random = new Random(seed);
		
		this.width = width;
		this.height = height;
		this.area = width * height;

		// Initialize the board with grass
		this.board = new Tile [height][width];
		for (int i = 0; i < this.board.length ; i++) {
			for (int j = 0; j < this.board[i].length ; j++) {
				this.board[i][j] = Tile.GRASS;
			}
		}
		
		// Numbers of static entities
		// densities hard coded
		int bigRocksNumber = area / 200;
		int treeNumber = area / 100;
		int smallRocksNumber = area / 50;
		
		placeTile(Tile.BIG_ROCK, bigRocksNumber);
		placeTile(Tile.TREE, treeNumber);
		placeTile(Tile.SMALL_ROCK, smallRocksNumber);
		
	}
	
	/*
	 * Places given tile to a random location on the board
	 * */
	public void placeTile(Tile tile) {
		placeTile(tile, 1);
	}

	public void placeTile(Tile tile, int number) {
		for (int i = 0; i < number; i++) {
			boolean placed = false;
			int x,y;

			while (!placed) {
				// Try placing to random place until empty tile is found
				y = (int) Math.floor(this.random.nextDouble() * this.height);
				x = (int) Math.floor(this.random.nextDouble() * this.width);
				if (this.board[y][x] == Tile.GRASS) {
					this.board[y][x] = tile;
					placed = true;
				}
			}
		}
	}
	
	public Tile[][] getBoard() {
		return this.board;
	}
	
	public void tick() {
		tick++;
		// Handle events
		
		// Update entities
		
		// Calculate events collisions/scores/mowed lawn
		
	}
	
}
