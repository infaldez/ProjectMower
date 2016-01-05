package rtsd2015.tol.pm;

import java.util.EnumMap;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import rtsd2015.tol.pm.enums.Tile;

public class ClientRenderer implements Runnable {

	Launcher mainApp;
	Game game;
	Canvas canvas;
	GraphicsContext gc;
	Tile[][] board;
	private int render_w;
	private int render_y;
	private int[] grid = new int[2];
	private double tileSize, tileOffsetX, tileOffsetY;
	private boolean render = true;

	private EnumMap<Tile, Image> tileImages;

	ClientRenderer(Launcher mainApp, Game game, int x, int y) {
		this.mainApp = mainApp;
		this.game = game;
		this.grid = game.getGrid();
		this.canvas = mainApp.getCanvas();
		this.render_w = mainApp.getContentSpace()[0];
		this.render_y = mainApp.getContentSpace()[1];
		this.gc = canvas.getGraphicsContext2D();

	}

	private void init() {
		gc.setFill(Color.WHITE);
		gc.setFont(new Font("Consolas", 12));
		gc.fillText("Initializing ...", 16, 16);
		buildTextures();
		initTileDimensions();
	}

	private GraphicsContext getGraphicsContext() {
		return gc;
	}

	/**
	 * Clears the viewport
	 *
	 */
	private void clearViewport() {
		gc.clearRect(0, 0, render_w, render_y);
	}

	/**
	 * Loads all the textures
	 *
	 */
	private void buildTextures() {
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
	 * Setups tile dimensions
	 *
	 */
	private void initTileDimensions() {
		double tileMaxWidth = (double) render_w / (double) grid[0];
		double tileMaxHeight = (double) render_y / (double) grid[1];
		tileSize = Math.min(tileMaxWidth, tileMaxHeight);
		tileOffsetX = ((double) render_w - (tileSize * grid[0])) / 2;
		tileOffsetY = ((double) render_y - (tileSize * grid[1])) / 2;
	}

	private void drawBoard(Tile[][] board) {
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

	private void debug(double delta) {
		gc.fillText("Delta: " + delta, 16, 16);
	}

	@Override
	public void run() {
		try {
			init();

			long lastLoopTime = System.nanoTime();
			final int TARGET_FPS = 16;
			final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;

			long now;
			long updateLength;
			double delta;

			Level level = game.getLevel();

			while (render) {
				// Define this cycle
				now = System.nanoTime();
				updateLength = now - lastLoopTime;
				lastLoopTime = now;
				delta = updateLength / ((double) OPTIMAL_TIME);

				// Clear viewport
				clearViewport();

				// Show debug
				if (mainApp.getDebug()) {
					debug(delta);
				}

				// Draw resources
				drawBoard(level.getBoard());

				// Wait for the next cycle
				Thread.sleep((lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000);
			}
		} catch (Exception e) {
			System.out.println("Renderer Exception!");
			e.printStackTrace(System.err);
		}
	}
}
