package platformer.state;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import platformer.entity.Entity;
import platformer.entity.Player;
import platformer.entity.tiles.Tile;
import platformer.entity.tiles.TileID;
import platformer.game.Main;
import platformer.game.PerlinNoise;

public class LevelOneState extends State {

	private int mapWidth = 24, mapHeight = 13;// map dimension in tiles 19x13

	private ArrayList<Entity> entities;
	private ArrayList<Tile> tiles;
	private Player player;

	private PerlinNoise mainNoise;
	private PerlinNoise underNoise;

	private float camX = 0;
	private float camY = 0;

	private int screenLeftTX;
	private int screenRightTX;
	private int screenTopTY;
	private int screenBottomTY;
	private int prevTX, prevTY;
	
	//game vars
	private int bedrockLevel = 80;

	public LevelOneState(GameStateManager gsm) {
		super(gsm);
		entities = new ArrayList<Entity>();
		tiles = new ArrayList<Tile>();
		mainNoise = new PerlinNoise();
		underNoise = new PerlinNoise();
		player = new Player(600, mainNoise.generateHeight((int) Math.ceil(600/64) - 4) - 180);
		
		prevTX = (int) Math.ceil(player.getX() / 64);
		prevTY = (int) Math.ceil(player.getY() / 64);
		
		generateMap();
	}

	private void generateMap() {
		for (int x = 0; x < mapWidth; x++) {
			int top = (int) Math.ceil(mainNoise.generateHeight(x - 4)) + 8;
			tiles.add(new Tile((x - 4) * 64, (float) top * 64, 0, TileID.Dirt_Grass_tile));

			for (int y = 1; y < mapHeight; y++) {
				tiles.add(new Tile((x - 4) * 64, (float) (top + y) * 64, 1, TileID.Dirt_tile));
				tiles.add(new Tile((x - 4) * 64, (float) (top + y) * 64, 0, TileID.Dirt_tile));
			}
		}
	}

	private void generateLineX(int prevTX, int playerTX) {
		int diff = Math.abs(Math.abs(prevTX) - Math.abs(playerTX));

		if (playerTX < prevTX) {
			// player moved left
			for (int x = 0; x < diff; x++) {
				int top = (int) Math.ceil(mainNoise.generateHeight((screenLeftTX) - x)) + 8;
				int topUnder = (int)  Math.ceil(underNoise.generateHeight((screenLeftTX) + x)) + 18;
				tiles.add(new Tile(((screenLeftTX) - x) * 64, (float) top * 64, 0, TileID.Dirt_Grass_tile));
				
				//generate stone (TODO)
				
				for (int y = 0; y < mapHeight + 3; y++) {
					if(screenTopTY + y > top && screenTopTY + y < topUnder){
						tiles.add(new Tile(((screenLeftTX) - x) * 64, (float) (screenTopTY + y) * 64, 1, TileID.Dirt_tile));
						tiles.add(new Tile(((screenLeftTX) - x) * 64, (float) (screenTopTY + y) * 64, 0, TileID.Dirt_tile));
					}else if(screenTopTY + y >= topUnder && screenTopTY + y <= bedrockLevel){
						tiles.add(new Tile(((screenLeftTX) - x) * 64, (float) (screenTopTY + y) * 64, 1, TileID.Greystone_tile));
						tiles.add(new Tile(((screenLeftTX) - x) * 64, (float) (screenTopTY + y) * 64, 0, TileID.Greystone_tile));
					}
				}
			}
		} else if (playerTX > prevTX) {
			// player moved right
			for (int x = 0; x < diff; x++) {
				int top = (int) Math.ceil(mainNoise.generateHeight((screenRightTX) + x)) + 8;
				int topUnder = (int)  Math.ceil(underNoise.generateHeight((screenLeftTX) + x)) + 18;
				tiles.add(new Tile(((screenRightTX) + x) * 64, (float) top * 64, 0, TileID.Dirt_Grass_tile));

				//generate stone (TODO)
				
				for (int y = 0; y < mapHeight + 3; y++) {
					if(screenTopTY + y > top && screenTopTY + y < topUnder){
						tiles.add(new Tile(((screenRightTX) + x) * 64, (float) (screenTopTY + y) * 64, 1, TileID.Dirt_tile));
						tiles.add(new Tile(((screenRightTX) + x) * 64, (float) (screenTopTY + y) * 64, 0, TileID.Dirt_tile));
					}else if(screenTopTY + y >= topUnder && screenTopTY + y <= bedrockLevel){
						tiles.add(new Tile(((screenRightTX) + x) * 64, (float) (screenTopTY + y) * 64, 1, TileID.Greystone_tile));
						tiles.add(new Tile(((screenRightTX) + x) * 64, (float) (screenTopTY + y) * 64, 0, TileID.Greystone_tile));
					}
				}
			}
		}
	}

	private void generateLineY(int prevTY, int playerTY) {
		int diff = Math.abs(Math.abs(prevTY) - Math.abs(playerTY));

		if (playerTY < prevTY) {
			// player moved up
			for (int d = 0; d < diff; d++) {
				for(int x = 0; x < mapWidth; x++){
					int top = (int) Math.ceil(mainNoise.generateHeight((screenLeftTX) + x)) + 8;
					int topUnder = (int)  Math.ceil(underNoise.generateHeight((screenLeftTX) + x)) + 18;
					if(top < screenTopTY + d && topUnder + 1 > screenTopTY){
						tiles.add(new Tile(((screenLeftTX) + x) * 64, (float) (screenTopTY + d) * 64, 1, TileID.Dirt_tile));
						tiles.add(new Tile(((screenLeftTX) + x) * 64, (float) (screenTopTY + d) * 64, 0, TileID.Dirt_tile));
					}else if(topUnder < screenTopTY + d){
						tiles.add(new Tile(((screenLeftTX) + x) * 64, (float) (screenTopTY + d) * 64, 1, TileID.Greystone_tile));
						tiles.add(new Tile(((screenLeftTX) + x) * 64, (float) (screenTopTY + d) * 64, 0, TileID.Greystone_tile));
					}else if(top == screenTopTY + d){
						tiles.add(new Tile(((screenLeftTX) + x) * 64, (float) (screenTopTY + d) * 64, 0, TileID.Dirt_Grass_tile));
					}
				}
			}
		} else if (playerTY > prevTY) {
			// player moved down
			for (int d = 0; d < diff; d++) {
				for(int x = 0; x < mapWidth; x++){
					int top = (int) Math.ceil(mainNoise.generateHeight((screenLeftTX) + x)) + 8;
					int topUnder = (int)  Math.ceil(underNoise.generateHeight((screenLeftTX) + x)) + 18;
					if(screenBottomTY - d >= bedrockLevel){
						tiles.add(new Tile(((screenLeftTX) + x) * 64, (float) (screenBottomTY - d) * 64, 0, TileID.Brick_Red_tile));
					}else if(top < screenBottomTY - d && topUnder + 1 > screenBottomTY){
						tiles.add(new Tile(((screenLeftTX) + x) * 64, (float) (screenBottomTY - d) * 64, 1, TileID.Dirt_tile));
						tiles.add(new Tile(((screenLeftTX) + x) * 64, (float) (screenBottomTY - d) * 64, 0, TileID.Dirt_tile));
					}else if(topUnder < screenBottomTY - d){
						tiles.add(new Tile(((screenLeftTX) + x) * 64, (float) (screenBottomTY - d) * 64, 1, TileID.Greystone_tile));
						tiles.add(new Tile(((screenLeftTX) + x) * 64, (float) (screenBottomTY - d) * 64, 0, TileID.Greystone_tile));
					}else if(top == screenBottomTY - d){
						tiles.add(new Tile(((screenLeftTX) + x) * 64, (float) (screenBottomTY - d) * 64, 0, TileID.Dirt_Grass_tile));
					}
				}
			}
		}
	}

	@Override
	public void draw(Graphics g) {
		// split tiles array into background and foreground
		ArrayList<Tile> foreGround = new ArrayList<Tile>();
		ArrayList<Tile> backGround = new ArrayList<Tile>();
		for (int j = 0; j < tiles.size(); j++) {
			if (tiles.get(j).getZ() == 0)
				foreGround.add(tiles.get(j));
			else if (tiles.get(j).getZ() == 1)
				backGround.add(tiles.get(j));
		}
		((Graphics2D) g).translate((int) -camX, (int) -camY);
		// figure out which background tiles are visible and draw them
		for (int k = 0; k < backGround.size(); k++) {
			boolean visible = true;
			for (int l = 0; l < foreGround.size(); l++) {
				if (backGround.get(k).getX() == foreGround.get(l).getX()
						&& backGround.get(k).getY() == foreGround.get(l).getY()) {
					visible = false;
				}
			}
			if (visible)
				backGround.get(k).draw(g);
		}
		// draw entities
		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).draw(g);
		}
		player.draw(g);
		// draw foreground tiles
		for (int d = 0; d < foreGround.size(); d++) {
			foreGround.get(d).draw(g);
		}
		((Graphics2D) g).translate((int) camX, (int) camY);

		// draw tile bar
		g.drawImage(TileID.Dirt_tile.getTexture(), 383, (int) 32, null);
		if (player.getCurrentBlock() != TileID.Dirt_tile)
			g.drawImage(player.getCurrentBlock().tint, 383, 32, null);

		g.drawImage(TileID.Dirt_Grass_tile.getTexture(), 383 + 74, 32, null);
		if (player.getCurrentBlock() != TileID.Dirt_Grass_tile)
			g.drawImage(player.getCurrentBlock().tint, 383 + 74, 32, null);

		g.drawImage(TileID.Greystone_tile.getTexture(), 383 + (2 * 74), 32, null);
		if (player.getCurrentBlock() != TileID.Greystone_tile)
			g.drawImage(player.getCurrentBlock().tint, 383 + (2 * 74), 32, null);

		g.drawImage(TileID.Redstone_tile.getTexture(), 383 + (3 * 74), 32, null);
		if (player.getCurrentBlock() != TileID.Redstone_tile)
			g.drawImage(player.getCurrentBlock().tint, 383 + (3 * 74), 32, null);

		g.drawImage(TileID.Brick_Grey_tile.getTexture(), 383 + (4 * 74), 32, null);
		if (player.getCurrentBlock() != TileID.Brick_Grey_tile)
			g.drawImage(player.getCurrentBlock().tint, 383 + (4 * 74), 32, null);

		g.drawImage(TileID.Brick_Red_tile.getTexture(), 383 + (5 * 74), 32, null);
		if (player.getCurrentBlock() != TileID.Brick_Red_tile)
			g.drawImage(player.getCurrentBlock().tint, 383 + (5 * 74), 32, null);

		if (Main.DEBUG) {
			g.setColor(Color.RED);
			g.drawString("X: " + (int) Math.ceil(player.getX() / 64) + " | Y: " + (int) Math.ceil(player.getY() / 64),
					32, 32);
			g.drawString(
					"ScreenLeftX: " + (int) ((Math.ceil(player.getX() / 64) - Math.ceil((Main.WIDTH / 2) / 64)) - 1)
							+ " | ScreenRigtX: "
							+ (int) ((Math.ceil(player.getX() / 64) + Math.ceil((Main.WIDTH / 2) / 64)) + 1),
					32, 64);
			g.drawString(
					"ScreenTopY: " + (int) ((Math.ceil(player.getY() / 64) - Math.ceil((Main.HEIGHT / 2) / 64)) - 1)
							+ " | ScreenBottomY: "
							+ (int) ((Math.ceil(player.getY() / 64) + Math.ceil((Main.HEIGHT / 2) / 64))),
					32, 80);
			g.drawString("Entities: " + entities.size() + " | Tiles: " + tiles.size(), 32, 96);

			int tx = (int) Math.ceil((Main.getMouseX() + camX) / 64) - 1;
			int ty = (int) Math.ceil((Main.getMouseY() + camY) / 64) - 1;
			int numTilesAtPosition = 0;
			for (int n = 0; n < tiles.size(); n++) {
				if (tiles.get(n).getX() / 64 == tx && tiles.get(n).getY() / 64 == ty) {
					numTilesAtPosition++;
				}
			}
			g.drawString(numTilesAtPosition + "", Main.getMouseX(), Main.getMouseY());
		}
	}

	@Override
	public void update() {
		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).update(entities, tiles, camX, camY);
		}
		for (int j = 0; j < tiles.size(); j++) {
			if(tiles.get(j).getHealth() == 0) tiles.remove(j);
			tiles.get(j).update(entities, tiles, camX, camY);
		}
		player.update(entities, tiles, camX, camY);

		camX = player.getX() - Main.WIDTH / 2;
		camY = player.getY() - Main.HEIGHT / 2;
		
		screenLeftTX = (int) (Math.ceil(player.getX() / 64) - Math.ceil((Main.WIDTH / 2) / 64)) - 2;
		screenRightTX = (int) (Math.ceil(player.getX() / 64) + Math.ceil((Main.WIDTH / 2) / 64)) + 1;
		screenTopTY = (int) (Math.ceil(player.getY() / 64) - Math.ceil((Main.HEIGHT / 2) / 64)) - 2;
		screenBottomTY = (int) (Math.ceil(player.getY() / 64) + Math.ceil((Main.HEIGHT / 2) / 64)) + 1;

		// destroy tiles
		for (int x = 0; x < tiles.size(); x++) {
			Tile tempTile = tiles.get(x);
			if ((tempTile.getX() / 64) > screenRightTX || (tempTile.getX() / 64) < screenLeftTX) {
				tiles.remove(tempTile);
			}
		}
		for (int y = 0; y < tiles.size(); y++) {
			Tile tempTile = tiles.get(y);
			if ((tempTile.getY() / 64) < screenTopTY - 1 || (tempTile.getY() / 64) > screenBottomTY) {
				tiles.remove(tempTile);
			}
		}

		if (prevTX != Math.ceil(player.getX() / 64)) {
			generateLineX(prevTX, (int) Math.ceil(player.getX() / 64));
			prevTX = (int) Math.ceil(player.getX() / 64);
		}
		if (prevTY != Math.ceil(player.getY() / 64)) {
			generateLineY(prevTY, (int) Math.ceil(player.getY() / 64));
			prevTY = (int) Math.ceil(player.getY() / 64);
		}
	}
}
