package rtsd2015.tol.pm;

import java.util.ArrayList;
import java.util.List;
import java.util.EnumMap;
import java.util.Random;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import rtsd2015.tol.pm.enums.Movement;
import rtsd2015.tol.pm.enums.Side;
import rtsd2015.tol.pm.enums.Tile;

public class Game {

	private Launcher mainApp;
	private static GameEngine gameEngine;

	// LOGIC
	private long tick = 0;
	private int area;
	private int width, height;
	private Tile[][] board;

	// CLIENT
	private static GraphicsContext gc;
	private static int canvasWidth = 800;
	private static int canvasHeight = 520;
	private static double tileSize, tileOffsetX, tileOffsetY;
	private static EnumMap<Tile, Image> tileImages;
	private static int limitedCycles = 1280; // For testing purposes only
	private static long lastFpsTime;
	private static long frameId;

	// SHARED
	protected static long seed;
	protected static int gridY, gridX;
	private static Timer timer;
	private static Level lvl;
	private static List<EntityPlayer> players = new ArrayList<>();

	/**
	 * Initializes a new game for both clients and server
	 *
	 * @throws InterruptedException
	 */
	Game(Launcher mainApp, int seed, boolean hasUI) throws InterruptedException {

		// Initial parameters for a new game
		this.mainApp = mainApp;
		this.seed = seed;
		this.gridY = 16;
		this.gridX = 16;
		//gameEngine = new GameEngine(seed, gridX, gridY);
		this.lvl = new Level(seed, gridX, gridY);

		// Initialize players
		players.add(new EntityPlayer(Side.BLUE));
		players.add(new EntityPlayer(Side.RED));

		// Initialize in-game timer
		timer = new Timer();

		// Calculate tile dimensions, tiles are squares
		double tileMaxHeight = (double) canvasHeight / (double) gridY;
		double tileMaxWidth = (double) canvasWidth / (double) gridX;
		tileSize = Math.min(tileMaxWidth, tileMaxHeight);
		tileOffsetY = ((double) canvasHeight - (tileSize * gridY)) / 2;
		tileOffsetX = ((double) canvasWidth - (tileSize * gridX)) / 2;

		// Finally, start the game loop
		if (hasUI) {
			loadTextures();
			this.gc = mainApp.getCanvas();
			gc.clearRect(0, 0, canvasWidth, canvasHeight);
			clientGameLoop();
		} else {
			serverGameLoop();
		}
	}

	/**
	 * The main gameloop for servers, including logic
	 *
	 */
	private static void serverGameLoop() {

	}

	/**
	 * The main gameloop for clients, including drawing
	 *
	 * @throws InterruptedException
	 */
	private static void clientGameLoop() throws InterruptedException {

		// Initialize game loop
		long lastLoopTime = System.nanoTime();
		final int TARGET_FPS = 16;
		final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;

		// Start timer
		timer.start();

		while (limitedCycles > 0) {

			// Define this cycle
			long now = System.nanoTime();
			long updateLength = now - lastLoopTime;
			lastLoopTime = now;
			double delta = updateLength / ((double) OPTIMAL_TIME);

			// Update counters
			lastFpsTime += updateLength;
			frameId++;
			limitedCycles--;

			// New rendering frame
			gc.clearRect(0, 0, canvasWidth, canvasHeight);
			drawTiles();

			// TODO: content of the cycle
			gc.strokeText("Delta: " + delta, 16, 96);
			System.out.println("Delta: " + delta + ", frames left: " + limitedCycles);
			System.out.println("Player(" + players.get(0).id + ") side: " + players.get(0).getSide() + " dir: "
					+ players.get(0).getDir() + " pos: " + players.get(0).getPos()[0] + ","
					+ players.get(0).getPos()[1]);
			System.out.println("Player(" + players.get(1).id + ") side: " + players.get(1).getSide() + " dir: "
					+ players.get(1).getDir() + " pos: " + players.get(1).getPos()[0] + ","
					+ players.get(1).getPos()[1]);
			System.out.println("EntityCount: " + Entity.entityCount);
			System.out.println("\b");

			// Wait for the next frame
			Thread.sleep((lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000);
		}
	}

	private static void loadTextures() {
		tileImages = new EnumMap<Tile, Image>(Tile.class);
		String imgPath = "file:src/rtsd2015/tol/pm/view/";
		tileImages.put(Tile.DIRT, new Image(imgPath + "dirt.png"));
		tileImages.put(Tile.GRASS, new Image(imgPath + "grass.png"));
		tileImages.put(Tile.TREE, new Image(imgPath + "tree.png"));
		tileImages.put(Tile.BIG_ROCK, new Image(imgPath + "big_rock.png"));
		tileImages.put(Tile.SMALL_ROCK, new Image(imgPath + "small_rock.png"));
		tileImages.put(Tile.PLAYER1, new Image(imgPath + "player1.png"));
		tileImages.put(Tile.PLAYER2, new Image(imgPath + "player2.png"));
	};

	private static void drawTiles() {
		Tile[][] board = lvl.getBoard();
		for (int i = 0; i < board.length ; i++) {
			for (int j = 0; j < board[i].length ; j++) {
				gc.drawImage(tileImages.get(board[i][j]),
						tileOffsetX + i * tileSize,
						tileOffsetY + j * tileSize,
						tileSize, tileSize
						);
			}
		}

	}
}
