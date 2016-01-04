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
	private static GraphicsContext gc;
	private static int canvasWidth = 800;
	private static int canvasHeight = 520;

	private static double tileSize, tileOffsetX, tileOffsetY;

	private static EnumMap<Tile, Image> tileImages;

	protected static int seed;
	protected static int gridY, gridX; // Dimensions in tiles

	private static Timer timer;
	private static Level lvl;
	private static GameEngine gameEngine;
	private static List<EntityPlayer> players = new ArrayList<>();

	private static int limitedCycles = 1280; // For testing purposes only
	private static long lastFpsTime;
	private static long frameId;

	/**
	 * Initializes a new game
	 *
	 * @throws InterruptedException
	 */
	Game(Launcher mainApp) throws InterruptedException {

		this.mainApp = mainApp;
		loadTextures();

		// Initialize a new level
		seed = 2320; // TODO: Add seed variable

		gridY = 16;
		gridX = 16;
		gameEngine = new GameEngine(seed, gridX, gridY);

		// Initialize players
		players.add(new EntityPlayer(Side.BLUE));
		players.add(new EntityPlayer(Side.RED));

		// Initialize in-game timer
		timer = new Timer();

		// Get the main rendering space
		this.gc = mainApp.getCanvas();
		gc.clearRect(0, 0, canvasWidth, canvasHeight);

		// Calculate tile dimensions, tiles are squares
		double tileMaxHeight = (double) canvasHeight / (double) gridY;
		double tileMaxWidth = (double) canvasWidth / (double) gridX;
		tileSize = Math.min(tileMaxWidth, tileMaxHeight);
		tileOffsetY = ((double) canvasHeight - (tileSize * gridY)) / 2;
		tileOffsetX = ((double) canvasWidth - (tileSize * gridX)) / 2;

		System.out.println(tileSize + " " + tileOffsetY + " " + tileOffsetX);

		// Finally, start the game loop
		gameLoop();
	}



	/**
	 * The main gameloop
	 *
	 * @throws InterruptedException
	 */
	private static void gameLoop() throws InterruptedException {

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
	};

	private static void drawTiles() {
		Tile[][] board = gameEngine.getBoard();
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
