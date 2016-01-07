package rtsd2015.tol.pm;

import java.util.EnumMap;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import rtsd2015.tol.pm.enums.Tile;

public class ClientRenderer implements Runnable {

	Launcher mainApp;
	Game game;
	Tile[][] board;
	Level level;
	List<EntityPlayer> playerEntities;
	List<Object> surfaceEntities;
	List<Object> staticEntities;
	private static GraphicsContext gc_dynamic;
	private static GraphicsContext gc_static;
	private static String resources = "file:src/rtsd2015/tol/pm/resources/";
	private int render_w;
	private int render_y;
	private int[] grid = new int[2];
	private double tileSize, tileOffsetX, tileOffsetY;
	private boolean render = true;
	private final int TARGET_FPS = 30;
	private final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
	private long now;
	private long lastLoopTime = System.nanoTime();
	private long updateLength;
	private double delta;

	private EnumMap<Tile, Image> tileImages;

	ClientRenderer(Launcher mainApp, Game game, int x, int y) {
		this.mainApp = mainApp;
		this.game = game;
		this.grid = game.getGrid();
		this.render_w = mainApp.getContentSpace()[0];
		this.render_y = mainApp.getContentSpace()[1];
		gc_dynamic = mainApp.getCanvas(0).getGraphicsContext2D();
		gc_static = mainApp.getCanvas(1).getGraphicsContext2D();
		level = game.getLevel();
		staticEntities = level.getStaticEntities();
		playerEntities = game.getPlayers();
	}

	private void init() {
		gc_dynamic.setFill(Color.WHITE);
		gc_dynamic.setFont(new Font("Consolas", 12));
		gc_dynamic.fillText("Initializing ...", 16, 16);
		buildTextures();
		initTileDimensions();
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

	private void drawUI() {
		gc_dynamic.fillText("Player 1: " + playerEntities.get(0).getScore(), 16, 16);
		gc_dynamic.fillText("Player 2: " + playerEntities.get(1).getScore(), 16, 32);
		if (mainApp.getDebug()) {
			debug();
		}
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
	private void drawImage(boolean dynamic, boolean offset, Image image, double angle, double x, double y, double w, double h) {
		if (offset) {
			x = tileOffsetX + x * w;
			y = tileOffsetY + y * h;
		}
		Rotate r = new Rotate(angle, x + (w / 2), y + (h / 2));
		if (dynamic) {
			gc_dynamic.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
			gc_dynamic.drawImage(image, x, y, w, h);
		} else {
			gc_static.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
			gc_static.drawImage(image, x, y, w, h);
		}
	}

	/**
	 * Draws debug information
	 *
	 * @param delta
	 */
	private void debug() {
		now = System.nanoTime();
		updateLength = now - lastLoopTime;
		lastLoopTime = now;
		delta = updateLength / ((double) OPTIMAL_TIME);
		gc_dynamic.fillText("FPS: " + Math.round(TARGET_FPS * delta), 16, 504);
	}

	public void setRender(boolean r) {
		this.render = r;
	}

	public boolean getRender() {
		return this.render;
	}

	@Override
	public void run() {
		try {
			init();
			// Draw static resources
			for (Object obj : staticEntities) {
				Entity entity = (Entity) obj;
				drawImage(false, true, tileImages.get(entity.getTile()), 0, entity.getGridPos()[0], entity.getGridPos()[1], tileSize, tileSize);
			}
			while (render) {
				// Save the following attributes onto a stack and clear the frame
				gc_dynamic.save();
				gc_dynamic.clearRect(0, 0, render_w, render_y);

				// Define this cycle


				// Draw dynamic resources
				for (EntityPlayer obj : playerEntities) {
					Entity entity = (Entity) obj;
					drawImage(true, true, tileImages.get(entity.getTile()), entity.getDir().getDirections(), entity.getGridPos()[0], entity.getGridPos()[1], tileSize, tileSize);
				}

				// Pop the state off of the stack, set the following attributes to their value at the time when that state was pushed onto the stack
				gc_dynamic.restore();

				// Draw interface elements
				drawUI();

				// Wait for the next cycle
				Thread.sleep((System.nanoTime() - System.nanoTime() + OPTIMAL_TIME) / 1000000);
			}
		} catch (Exception e) {
			System.out.println("Renderer Exception!");
			e.printStackTrace(System.err);
		}
	}
}
