package platformer.game;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import alibgame.ALibGame;
import alibgame.gfx.ImageLoader;
import platformer.state.GameStateManager;

public class Main extends ALibGame {
	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 1200, HEIGHT = 800;
	public static final String TITLE = "2D MineCraft Game";

	public static boolean DEBUG = false;

	public static ImageLoader imageLoader;

	BufferedImage skyBox;

	private GameStateManager gsm;

	public Main() {
		super(WIDTH, HEIGHT, TITLE);
		imageLoader = new ImageLoader(getClass());
		skyBox = imageLoader.loadImage("/skybox_clouds.png");
		gsm = new GameStateManager();
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(skyBox, 0, 0, null);
		gsm.draw(g);
	}

	long lastTimeDebug = System.currentTimeMillis();
	double nsPerDebugPress = 250;

	@Override
	public void update() {
		gsm.update();
		if (isKeyPressed(KeyEvent.VK_BACK_QUOTE)) {
			long now = System.currentTimeMillis();
			if (now - lastTimeDebug >= nsPerDebugPress) {
				if (DEBUG)
					DEBUG = false;
				else
					DEBUG = true;
				lastTimeDebug = now;
			}
		}
	}

	public static void main(String[] args) {
		new Main().start();
	}

}
