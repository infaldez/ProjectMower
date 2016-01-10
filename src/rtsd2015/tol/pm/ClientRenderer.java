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

	protected Client client;
	protected Game game;
	protected Level level;
	protected List<Entity> staticEntities;
	protected List<Entity> dynamicEntities;
	protected List<InterfaceText> interfaceTexts;
	protected List<EntityPlayer> playerEntities;

	private static GraphicsContext gc_dynamic;
	private static GraphicsContext gc_static;
	private static GraphicsContext gc_ui;
	private static String resources = "/resources/";
	private static int render_w = 0;
	private static int render_y = 0;
	private static boolean render = true;

	private int[] grid = new int[2];
	private double tileSize, tileOffsetX, tileOffsetY;
	private final int TARGET_FPS = 30;
	private final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
	private long now;
	private long lastLoopTime = System.nanoTime();
	private long updateLength;
	private double delta;

	private EnumMap<Tile, Image> tileImages;

	ClientRenderer(Client client) {
		this.client = client;
		updateGameReference();
	}

	/**
	 * Updates the game reference, meant to reload everything
	 *
	 */
	public void updateGameReference(){
		this.game = client.getGame();
		this.grid = game.getGrid();
		gc_dynamic = Launcher.getAppCanvas(0).getGraphicsContext2D();
		gc_static = Launcher.getAppCanvas(1).getGraphicsContext2D();
		gc_ui = Launcher.getAppCanvas(2).getGraphicsContext2D();
		this.level = game.getLevel();
		this.staticEntities = level.getStaticEntities();
		this.dynamicEntities = level.getDynamicEntities();
		this.playerEntities = game.getPlayers();
		this.interfaceTexts = game.getInterfaceTexts();
	}

	/**
	 * Initializes the renderer
	 * @throws InterruptedException
	 *
	 */
	private void init() throws InterruptedException {
		try {
			gc_ui.setFill(Color.WHITE);
			gc_ui.setFont(new Font("Verdana", 14));
			gc_ui.fillText("Initializing ...", 16, 16);
			buildTextures();
			Thread.sleep(500);
		} catch (Exception e) {
			System.out.println("Renderer initalization Exception!");
			e.printStackTrace(System.err);
		}
	}

	/**
	 * Flushes the rendering (reloads)
	 *
	 * @param all
	 */
	public void flush(boolean all) {
		gc_static.clearRect(0, 0, render_w, render_y);
		gc_dynamic.clearRect(0, 0, render_w, render_y);
		gc_ui.clearRect(0, 0, render_w, render_y);
		if (all) {
			this.level = game.getLevel();
			this.staticEntities = level.getStaticEntities();
			this.dynamicEntities = level.getDynamicEntities();
			this.playerEntities = game.getPlayers();
			this.interfaceTexts = game.getInterfaceTexts();
			tileImages.clear();
			buildTextures();
		}
		initTileDimensions();
		drawStaticResources();
	}

	/**
	 * Makes sure the content fills out the entire screen space
	 *
	 * @throws InterruptedException
	 */
	private void updateViewPortDimensions() throws InterruptedException {
		int new_w = Launcher.getContentSpace()[0];
		int new_y = Launcher.getContentSpace()[1];
		if (render_w != new_w || render_y != new_y) {
			try {
				Thread.sleep(100);
				Launcher.updateAppViewPortSize();
				gc_static.clearRect(0, 0, render_w, render_y);
				render_w = new_w;
				render_y = new_y;
				initTileDimensions();
				drawStaticResources();
			} catch (Exception e) {
				System.out.println("Renderer ViewPort update Exception!");
				e.printStackTrace(System.err);
			}
		}
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
		tileImages.put(Tile.FLOWER_BLUE, new Image(resources + "flower_blue.png"));
		tileImages.put(Tile.FLOWER_RED, new Image(resources + "flower_red.png"));
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

	/**
	 * Draws the in-game UI
	 *
	 */
	private void drawUI() {
		for (InterfaceText itext : interfaceTexts) {
			int interfaceTextsOffsetX = itext.getPosX();
			int interfaceTextsOffsetY = itext.getPosY() + 16;
			drawText(gc_ui, itext.getTextString(), itext.getTextFont(), itext.getTextColor(), interfaceTextsOffsetX, interfaceTextsOffsetY);
		}
		if (Launcher.getAppDebug()) {
			debug();
		}
	}

	/**
	 * Draws text to the given graphics context
	 *
	 * @param gc
	 * @param str
	 * @param font
	 * @param color
	 * @param x horizontal position
	 * @param y vertical position
	 */
	private void drawText(GraphicsContext gc, String str, Font font, Color color, int x, int y) {
		gc.setFill(color);
		gc.setFont(font);
		gc.fillText(str, x, y);
	}

	/**
	 * Draws debug information
	 *
	 */
	private void debug() {
		now = System.nanoTime();
		updateLength = now - lastLoopTime;
		lastLoopTime = now;
		delta = updateLength / ((double) OPTIMAL_TIME);
		int p1_posX = game.getPlayers().get(0).getGridPos()[0];
		int p1_posY = game.getPlayers().get(0).getGridPos()[1];
		int p2_posX = game.getPlayers().get(1).getGridPos()[0];
		int p2_posY = game.getPlayers().get(1).getGridPos()[1];
		drawText(gc_ui, resources, Font.font("Verdana", 14), Color.WHITE, 16, render_y - 96);
		drawText(gc_ui, "PL1 Pos: (" + p1_posX + "." + p1_posY + ")", Font.font("Verdana", 14), Color.WHITE, 16, render_y - 80);
		drawText(gc_ui, "PL2 Pos: (" + p2_posX + "." + p2_posY + ")", Font.font("Verdana", 14), Color.WHITE,16, render_y - 64);
		drawText(gc_ui, "PL1 HBox: " + level.getHitbox(p1_posX, p1_posY), Font.font("Verdana", 14), Color.WHITE,16, render_y - 48);
		drawText(gc_ui, "PL2 HBox: " + level.getHitbox(p2_posX, p2_posY), Font.font("Verdana", 14), Color.WHITE,16, render_y - 32);
		drawText(gc_ui, "FPS: " + Math.round(TARGET_FPS * delta), Font.font("Verdana", 14), Color.WHITE,16, render_y - 16);
		drawText(gc_ui, " Tick: " + Math.round(game.getTick()), Font.font("Verdana", 14), Color.WHITE,70, render_y - 16);
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
	 * To enable (true) or disable (false) rendering
	 * Note: if you disable the renderer, you must create a new renderer to re-enable it
	 *
	 * @param r
	 */
	public void setRender(boolean r) {
		render = r;
	}

	/**
	 * Returns whether the rendering is active (true) or not (false)
	 *
	 * @return true to active rendering
	 */
	public boolean getRender() {
		return render;
	}

	private void drawStaticResources() {
		for (Entity entity : staticEntities) {
			drawImage(false, true, tileImages.get(entity.getTile()), entity.getRenderAngle(), entity.getGridPos()[0], entity.getGridPos()[1], tileSize, tileSize);
		}
	}

	/**
	 * The main render-loop
	 *
	 */
	@Override
	public void run() {
		try {
			init();
			while (render) {
				// Save the following attributes onto a stack and clear the frame
				gc_dynamic.save();
				gc_dynamic.clearRect(0, 0, render_w, render_y);
				gc_ui.clearRect(0, 0, render_w, render_y);

				updateViewPortDimensions();

				// Draw dynamic resources
				for (Entity entity : dynamicEntities) {
					if (entity.isAlive()) {
						drawImage(true, true, tileImages.get(entity.getTile()), entity.getDir().getDirections(), entity.getGridPos()[0], entity.getGridPos()[1], tileSize, tileSize);
					}
				}

				for (Entity entity : playerEntities) {
					if (entity.isAlive()) {
						drawImage(true, true, tileImages.get(entity.getTile()), entity.getDir().getDirections(), entity.getGridPos()[0], entity.getGridPos()[1], tileSize, tileSize);
					}
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
