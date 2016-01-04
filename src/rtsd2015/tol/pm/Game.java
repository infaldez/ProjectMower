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
import javafx.scene.text.Text;
import rtsd2015.tol.pm.enums.Movement;
import rtsd2015.tol.pm.enums.Side;
import rtsd2015.tol.pm.enums.Tile;

public class Game {

	// SHARED
	private static Launcher mainApp;
	private static long seed;
	private static int gridY, gridX;
	private static Timer timer;
	private static Level lvl;
	private static List<EntityPlayer> players = new ArrayList<>();
	private static int canvasWidth = 800;
	private static int canvasHeight = 520;
	private static double tileSize, tileOffsetX, tileOffsetY;

	// CLIENT
	private static GraphicsContext gc;
	private static EnumMap<Tile, Image> tileImages;
	private static int limitedCycles = 640; // For testing purposes only
	private static long lastFpsTime;
	private static long frameId;
	private static boolean debug = true;

	// LOGIC
	private static long tick = 0;

	/**
	 * Initializes a new game for both clients and a server
	 *
	 * @throws InterruptedException
	 */
	Game(Launcher app, int sd, boolean isClient) throws InterruptedException {
		mainApp = app;
		seed = sd;
		gridY = 24;
		gridX = 24;
		lvl = new Level(seed, gridX, gridY);

		players.add(new EntityPlayer(Side.BLUE));
		players.add(new EntityPlayer(Side.RED));

		timer = new Timer();

		initTileDimensions();

		if (isClient) {
			clientGameLoop();
		} else {
			serverGameLoop();
		}
	}

	/**
	 * The main game loop for clients, includes drawing
	 *
	 * @throws InterruptedException
	 */
	private static void clientGameLoop() throws InterruptedException {

		// Initialize rendering
		loadTextures();
		gc = mainApp.getCanvas();
		gc.clearRect(0, 0, canvasWidth, canvasHeight);

		// Initialize looping
		long lastLoopTime = System.nanoTime();
		final int TARGET_FPS = 16;
		final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;

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

			// True to draw debug text
			if (debug) {
				gc.fillText("Delta: " + delta + "(" + limitedCycles + ")", 16, 96);
				gc.fillText("Player(0) POS: " + players.get(0).getPos()[0] + "," + players.get(0).getPos()[1], 16, 112);
				gc.fillText("Player(0) DIR: " + players.get(0).getDir(), 16, 128);
				gc.fillText("Player(1) POS: " + players.get(1).getPos()[0] + "," + players.get(1).getPos()[1], 16, 144);
				gc.fillText("Player(1) DIR: " + players.get(1).getDir(), 16, 160);
				gc.fillText("EntityCount: " + Entity.entityCount, 16, 176);
			}

			// Wait for the next frame
			Thread.sleep((lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000);
		}
	}

	/**
	 * Loads in-game textures
	 *
	 */
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
	}

	/**
	 * Initializes in-game tiles' dimensions
	 *
	 */
	private static void initTileDimensions() {
		double tileMaxHeight = (double) canvasHeight / (double) gridY;
		double tileMaxWidth = (double) canvasWidth / (double) gridX;
		tileSize = Math.min(tileMaxWidth, tileMaxHeight);
		tileOffsetY = ((double) canvasHeight - (tileSize * gridY)) / 2;
		tileOffsetX = ((double) canvasWidth - (tileSize * gridX)) / 2;
	}

	/**
	 * Draws board tile graphics
	 *
	 */
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

	/**
	 * The main game loop for a server, includes logic
	 *
	 */
	private static void serverGameLoop() {
		tick++;
	}
}
