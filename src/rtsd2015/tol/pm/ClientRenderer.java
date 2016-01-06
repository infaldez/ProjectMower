package rtsd2015.tol.pm;

import java.util.EnumMap;
import java.util.List;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import rtsd2015.tol.pm.enums.Tile;

public class ClientRenderer implements Runnable {

	Launcher mainApp;
	Game game;
	Canvas canvas;
	GraphicsContext gc;
	Tile[][] board;
	Level level;
	List<EntityPlayer> players;
	private String resources = "file:src/rtsd2015/tol/pm/resources/";
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
		tileImages.put(Tile.DIRT, new Image(resources + "dirt.png"));
		tileImages.put(Tile.GRASS, new Image(resources + "grass.png"));
		tileImages.put(Tile.TREE, new Image(resources + "tree.png"));
		tileImages.put(Tile.BIG_ROCK, new Image(resources + "big_rock.png"));
		tileImages.put(Tile.SMALL_ROCK, new Image(resources + "small_rock.png"));
		tileImages.put(Tile.PLAYER1, new Image(resources + "player1.png"));
		tileImages.put(Tile.PLAYER2, new Image(resources + "player2.png"));
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

	private void drawBoard() {
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

	private void drawUI() {
		gc.fillText("Player 1: " + players.get(0).getScore(), 16, 16);
		gc.fillText("Player 2: " + players.get(1).getScore(), 16, 32);
	}

	/**
	 * Sets the transform for the GraphicsContext to rotate around a pivot point
	 *
	 * @param angle the angle of rotation
	 * @param px the x pivot coordinate for the rotation (in canvas coordinates)
	 * @param py the y pivot coordinate for the rotation (in canvas coordinates)
	 */
	private void rotate(double angle, double px, double py) {
		Rotate r = new Rotate(angle, px, py);
		gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
	}

	/**
	 * Draws a turnable image on a graphics context
	 *
	 * The image is drawn at (tlpx, tlpy) rotated by angle pivoted around the point:
	 * (tlpx + image.getWidth() / 2, tlpy + image.getHeight() / 2)
	 *
	 * @param image the image to be drawn
	 * @param angle the angle of rotation
	 * @param tlpx the top left x coordinate where the image will be plotted (in canvas coordinates)
	 * @param tlpy the top left y coordinate where the image will be plotted (in canvas coordinates)
	 */
	private void drawRotatedImage(Image image, double angle, double tlpx, double tlpy) {
		gc.save();
		rotate(angle, tlpx + image.getWidth() / 2, tlpy + image.getHeight() / 2);
		gc.drawImage(image, tlpx, tlpy);
		gc.restore();
	}

	/**
	 * Draws debug information
	 *
	 * @param delta
	 */
	private void debug(double delta) {
		gc.fillText("Delta: " + delta, 16, 504);
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
			board = level.getBoard();
			players = game.getPlayers();

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
				drawBoard();
				drawRotatedImage(tileImages.get(Tile.PLAYER1), players.get(0).getDir().getDirections(), players.get(0).getPos()[0], players.get(0).getPos()[1]);
				drawRotatedImage(tileImages.get(Tile.PLAYER2), players.get(1).getDir().getDirections(), players.get(1).getPos()[0], players.get(1).getPos()[1]);
				drawUI();

				// Wait for the next cycle
				Thread.sleep((lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000);
			}
		} catch (Exception e) {
			System.out.println("Renderer Exception!");
			e.printStackTrace(System.err);
		}
	}
}
