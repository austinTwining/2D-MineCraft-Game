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
	private PerlinNoise stoneNoise;
	private PerlinNoise ironNoise;
	private PerlinNoise coalNoise;
	private PerlinNoise diamondNoise;
	private PerlinNoise goldNoise;
	private PerlinNoise silverNoise;

	private ArrayList<String> changes;// structure "X Y Z changetype tile"

	private float camX = 0;
	private float camY = 0;

	private int screenLeftTX;
	private int screenRightTX;
	private int screenTopTY;
	private int screenBottomTY;
	private int prevTX, prevTY;

	// game vars
	private int bedrockLevel = 80;
	private float oreAmp = 100f;
	// iron
	private int ironTop = 28;
	private int ironBottom = 31;
	private float ironFrequency = 0.5f;
	// coal
	private int coalTop = 23;
	private int coalBottom = 27;
	private float coalFrequency = 0.5f;
	// diamond
	private int diamondTop = 76;
	private int diamondBottom = 79;
	private float diamondFrequency = 0.05f;
	// gold
	private int goldTop = 56;
	private int goldBottom = 58;
	private float goldFrequency = 0.08f;
	// silver
	private int silverTop = 48;
	private int silverBottom = 50;
	private float silverFrequency = 0.1f;

	public LevelOneState(GameStateManager gsm) {
		super(gsm);
		entities = new ArrayList<Entity>();
		tiles = new ArrayList<Tile>();
		changes = new ArrayList<String>();

		mainNoise = new PerlinNoise();
		stoneNoise = new PerlinNoise();

		ironNoise = new PerlinNoise();
		ironNoise.setFrequency(ironFrequency);
		ironNoise.setAmplitude(oreAmp);

		coalNoise = new PerlinNoise();
		coalNoise.setFrequency(coalFrequency);
		coalNoise.setAmplitude(oreAmp);

		diamondNoise = new PerlinNoise();
		diamondNoise.setFrequency(diamondFrequency);
		diamondNoise.setAmplitude(oreAmp);

		goldNoise = new PerlinNoise();
		goldNoise.setFrequency(goldFrequency);
		goldNoise.setAmplitude(oreAmp);

		silverNoise = new PerlinNoise();
		silverNoise.setFrequency(silverFrequency);
		silverNoise.setAmplitude(oreAmp);

		player = new Player(600, mainNoise.generateHeight((int) Math.ceil(600 / 64) - 4) - 180);

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
				int topUnder = (int) Math.ceil(stoneNoise.generateHeight((screenLeftTX) + x)) + 18;
				int ironGen = (int) ((int) Math.ceil(ironNoise.generateHeight((screenLeftTX) + x))
						+ Math.ceil((ironBottom - ironTop) / 2)) + ironTop;
				int coalGen = (int) ((int) Math.ceil(coalNoise.generateHeight((screenLeftTX) + x))
						+ Math.ceil((coalBottom - coalTop) / 2)) + coalTop;
				int diamondGen = (int) ((int) Math.ceil(diamondNoise.generateHeight((screenLeftTX) + x))
						+ Math.ceil((diamondBottom - diamondTop) / 2)) + diamondTop;
				int goldGen = (int) ((int) Math.ceil(goldNoise.generateHeight((screenLeftTX) + x))
						+ Math.ceil((goldBottom - goldTop) / 2)) + goldTop;
				int silverGen = (int) ((int) Math.ceil(silverNoise.generateHeight((screenLeftTX) + x))
						+ Math.ceil((silverBottom - silverTop) / 2)) + silverTop;

				boolean topChange = false;

				for (int i = 0; i < changes.size(); i++) {
					String[] changesSplit = changes.get(i).split(" ");
					int tempx = Integer.parseInt(changesSplit[0]);
					int tempy = Integer.parseInt(changesSplit[1]);
					int tempz = Integer.parseInt(changesSplit[2]);
					char tempChangeType = changesSplit[3].charAt(0);
					String tempTileChange = changesSplit[4];
					if (tempx == (screenLeftTX) - x && tempy == top) {
						if (tempChangeType == 'p')
							tiles.add(new Tile(tempx * 64, tempy * 64, tempz, TileID.valueOf(tempTileChange)));
						topChange = true;
					}
				}

				if (!topChange)
					tiles.add(new Tile(((screenLeftTX) - x) * 64, (float) top * 64, 0, TileID.Dirt_Grass_tile));

				for (int y = 0; y < mapHeight + 3; y++) {
					for (int z = 0; z < 2; z++) {
						boolean changed = false;

						for (int i = 0; i < changes.size(); i++) {
							String[] changesSplit = changes.get(i).split(" ");
							int tempx = Integer.parseInt(changesSplit[0]);
							int tempy = Integer.parseInt(changesSplit[1]);
							int tempz = Integer.parseInt(changesSplit[2]);
							char tempChangeType = changesSplit[3].charAt(0);
							String tempTileChange = changesSplit[4];
							if (tempx == screenLeftTX - x && tempy == screenTopTY + y && tempz == z) {
								if (tempChangeType == 'p')
									tiles.add(new Tile(tempx * 64, tempy * 64, tempz, TileID.valueOf(tempTileChange)));
								changed = true;
							}
						}

						if (!changed) {
							if (screenTopTY + y >= ironTop && screenTopTY + y <= ironBottom
									&& screenTopTY + y == ironGen) {
								tiles.add(new Tile(((screenLeftTX) - x) * 64, (float) (screenTopTY + y) * 64, z,
										TileID.Stone_Iron_tile));
							} else if (screenTopTY + y >= coalTop && screenTopTY + y <= coalBottom
									&& screenTopTY + y == coalGen) {
								tiles.add(new Tile(((screenLeftTX) - x) * 64, (float) (screenTopTY + y) * 64, z,
										TileID.Stone_Coal_tile));
							} else if (screenTopTY + y >= diamondTop && screenTopTY + y <= diamondBottom
									&& screenTopTY + y == diamondGen) {
								tiles.add(new Tile(((screenLeftTX) - x) * 64, (float) (screenTopTY + y) * 64, z,
										TileID.Stone_Diamond_tile));
							} else if (screenTopTY + y >= goldTop && screenTopTY + y <= goldBottom
									&& screenTopTY + y == goldGen) {
								tiles.add(new Tile(((screenLeftTX) - x) * 64, (float) (screenTopTY + y) * 64, z,
										TileID.Stone_Gold_tile));
							} else if (screenTopTY + y >= silverTop && screenTopTY + y <= silverBottom
									&& screenTopTY + y == silverGen) {
								tiles.add(new Tile(((screenLeftTX) - x) * 64, (float) (screenTopTY + y) * 64, z,
										TileID.Stone_Silver_tile));
							} else {
								if (screenTopTY + y > top && screenTopTY + y < topUnder) {
									tiles.add(new Tile(((screenLeftTX) - x) * 64, (float) (screenTopTY + y) * 64, z,
											TileID.Dirt_tile));
								} else if (screenTopTY + y >= topUnder && screenTopTY + y < bedrockLevel) {
									tiles.add(new Tile(((screenLeftTX) - x) * 64, (float) (screenTopTY + y) * 64, z,
											TileID.Greystone_tile));
								} else if (screenTopTY + y >= bedrockLevel) {
									tiles.add(new Tile(((screenLeftTX) - x) * 64, (float) (screenTopTY + y) * 64, z,
											TileID.Redstone_tile));
								}
							}
						}
					}
				}
			}
		} else if (playerTX > prevTX) {
			// player moved right
			for (int x = 0; x < diff; x++) {
				int top = (int) Math.ceil(mainNoise.generateHeight((screenRightTX) + x)) + 8;
				int topUnder = (int) Math.ceil(stoneNoise.generateHeight((screenRightTX) + x)) + 18;
				int ironGen = (int) ((int) Math.ceil(ironNoise.generateHeight((screenRightTX) + x))
						+ Math.ceil((ironBottom - ironTop) / 2)) + ironTop;
				int coalGen = (int) ((int) Math.ceil(coalNoise.generateHeight((screenRightTX) + x))
						+ Math.ceil((coalBottom - coalTop) / 2)) + coalTop;
				int diamondGen = (int) ((int) Math.ceil(diamondNoise.generateHeight((screenRightTX) + x))
						+ Math.ceil((diamondBottom - diamondTop) / 2)) + diamondTop;
				int goldGen = (int) ((int) Math.ceil(goldNoise.generateHeight((screenRightTX) + x))
						+ Math.ceil((goldBottom - goldTop) / 2)) + goldTop;
				int silverGen = (int) ((int) Math.ceil(silverNoise.generateHeight((screenRightTX) + x))
						+ Math.ceil((silverBottom - silverTop) / 2)) + silverTop;

				boolean topChange = false;

				for (int i = 0; i < changes.size(); i++) {
					String[] changesSplit = changes.get(i).split(" ");
					int tempx = Integer.parseInt(changesSplit[0]);
					int tempy = Integer.parseInt(changesSplit[1]);
					int tempz = Integer.parseInt(changesSplit[2]);
					char tempChangeType = changesSplit[3].charAt(0);
					String tempTileChange = changesSplit[4];
					if (tempx == (screenRightTX) - x && tempy == top) {
						if (tempChangeType == 'p')
							tiles.add(new Tile(tempx * 64, tempy * 64, tempz, TileID.valueOf(tempTileChange)));
						topChange = true;
					}
				}

				if (!topChange)
					tiles.add(new Tile(((screenRightTX) + x) * 64, (float) top * 64, 0, TileID.Dirt_Grass_tile));

				for (int y = 0; y < mapHeight + 3; y++) {
					for (int z = 0; z < 2; z++) {
						boolean changed = false;

						for (int i = 0; i < changes.size(); i++) {
							String[] changesSplit = changes.get(i).split(" ");
							int tempx = Integer.parseInt(changesSplit[0]);
							int tempy = Integer.parseInt(changesSplit[1]);
							int tempz = Integer.parseInt(changesSplit[2]);
							char tempChangeType = changesSplit[3].charAt(0);
							String tempTileChange = changesSplit[4];
							if (tempx == (screenRightTX) - x && tempy == screenTopTY + y && tempz == z) {
								if (tempChangeType == 'p')
									tiles.add(new Tile(tempx * 64, tempy * 64, tempz, TileID.valueOf(tempTileChange)));
								changed = true;
							}
						}

						if (!changed) {
							if (screenTopTY + y >= ironTop && screenTopTY + y <= ironBottom
									&& screenTopTY + y == ironGen) {
								tiles.add(new Tile(((screenRightTX) + x) * 64, (float) (screenTopTY + y) * 64, z,
										TileID.Stone_Iron_tile));
							} else if (screenTopTY + y >= coalTop && screenTopTY + y <= coalBottom
									&& screenTopTY + y == coalGen) {
								tiles.add(new Tile(((screenRightTX) + x) * 64, (float) (screenTopTY + y) * 64, z,
										TileID.Stone_Coal_tile));
							} else if (screenTopTY + y >= diamondTop && screenTopTY + y <= diamondBottom
									&& screenTopTY + y == diamondGen) {
								tiles.add(new Tile(((screenRightTX) + x) * 64, (float) (screenTopTY + y) * 64, z,
										TileID.Stone_Diamond_tile));
							} else if (screenTopTY + y >= goldTop && screenTopTY + y <= goldBottom
									&& screenTopTY + y == goldGen) {
								tiles.add(new Tile(((screenRightTX) + x) * 64, (float) (screenTopTY + y) * 64, z,
										TileID.Stone_Gold_tile));
							} else if (screenTopTY + y >= silverTop && screenTopTY + y <= silverBottom
									&& screenTopTY + y == silverGen) {
								tiles.add(new Tile(((screenRightTX) + x) * 64, (float) (screenTopTY + y) * 64, z,
										TileID.Stone_Silver_tile));
							} else {
								if (screenTopTY + y > top && screenTopTY + y < topUnder) {
									tiles.add(new Tile(((screenRightTX) + x) * 64, (float) (screenTopTY + y) * 64, z,
											TileID.Dirt_tile));
								} else if (screenTopTY + y >= topUnder && screenTopTY + y < bedrockLevel) {
									tiles.add(new Tile(((screenRightTX) + x) * 64, (float) (screenTopTY + y) * 64, z,
											TileID.Greystone_tile));
								} else if (screenTopTY + y >= bedrockLevel) {
									tiles.add(new Tile(((screenRightTX) + x) * 64, (float) (screenTopTY + y) * 64, z,
											TileID.Redstone_tile));
								}
							}
						}
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
				for (int x = 0; x < mapWidth; x++) {
					int top = (int) Math.ceil(mainNoise.generateHeight((screenLeftTX) + x)) + 8;
					int topUnder = (int) Math.ceil(stoneNoise.generateHeight((screenLeftTX) + x)) + 18;
					int ironGen = (int) ((int) Math.ceil(ironNoise.generateHeight((screenLeftTX) + x))
							+ Math.ceil((ironBottom - ironTop) / 2)) + ironTop;
					int coalGen = (int) ((int) Math.ceil(coalNoise.generateHeight((screenLeftTX) + x))
							+ Math.ceil((coalBottom - coalTop) / 2)) + coalTop;
					int diamondGen = (int) ((int) Math.ceil(diamondNoise.generateHeight((screenLeftTX) + x))
							+ Math.ceil((diamondBottom - diamondTop) / 2)) + diamondTop;
					int goldGen = (int) ((int) Math.ceil(goldNoise.generateHeight((screenLeftTX) + x))
							+ Math.ceil((goldBottom - goldTop) / 2)) + goldTop;
					int silverGen = (int) ((int) Math.ceil(silverNoise.generateHeight((screenLeftTX) + x))
							+ Math.ceil((silverBottom - silverTop) / 2)) + silverTop;

					for (int z = 0; z < 2; z++) {
						boolean changed = false;

						for (int i = 0; i < changes.size(); i++) {
							String[] changesSplit = changes.get(i).split(" ");
							int tempx = Integer.parseInt(changesSplit[0]);
							int tempy = Integer.parseInt(changesSplit[1]);
							int tempz = Integer.parseInt(changesSplit[2]);
							char tempChangeType = changesSplit[3].charAt(0);
							String tempTileChange = changesSplit[4];
							if (tempx == (screenLeftTX) + x && tempy == screenTopTY + d && tempz == z) {
								if (tempChangeType == 'p')
									tiles.add(new Tile(tempx * 64, tempy * 64, tempz, TileID.valueOf(tempTileChange)));
								changed = true;
							}
						}

						if (!changed) {
							if (screenTopTY + d >= ironTop && screenTopTY + d <= ironBottom
									&& screenTopTY + d == ironGen) {
								tiles.add(new Tile(((screenLeftTX) + x) * 64, (float) (screenTopTY + d) * 64, z,
										TileID.Stone_Iron_tile));
							} else if (screenTopTY + d >= coalTop && screenTopTY + d <= coalBottom
									&& screenTopTY + d == coalGen) {
								tiles.add(new Tile(((screenLeftTX) + x) * 64, (float) (screenTopTY + d) * 64, z,
										TileID.Stone_Coal_tile));
								;
							} else if (screenTopTY + d >= diamondTop && screenTopTY + d <= diamondBottom
									&& screenTopTY + d == diamondGen) {
								tiles.add(new Tile(((screenLeftTX) + x) * 64, (float) (screenTopTY + d) * 64, z,
										TileID.Stone_Diamond_tile));
							} else if (screenTopTY + d >= goldTop && screenTopTY + d <= goldBottom
									&& screenTopTY + d == goldGen) {
								tiles.add(new Tile(((screenLeftTX) + x) * 64, (float) (screenTopTY + d) * 64, z,
										TileID.Stone_Gold_tile));
							} else if (screenTopTY + d >= silverTop && screenTopTY + d <= silverBottom
									&& screenTopTY + d == silverGen) {
								tiles.add(new Tile(((screenLeftTX) + x) * 64, (float) (screenTopTY + d) * 64, z,
										TileID.Stone_Silver_tile));
							} else {
								if (top < screenTopTY + d && topUnder + 1 > screenTopTY) {
									tiles.add(new Tile(((screenLeftTX) + x) * 64, (float) (screenTopTY + d) * 64, z,
											TileID.Dirt_tile));
								} else if (topUnder < screenTopTY + d) {
									tiles.add(new Tile(((screenLeftTX) + x) * 64, (float) (screenTopTY + d) * 64, z,
											TileID.Greystone_tile));
								} else if (top == screenTopTY + d && z == 0) {
									tiles.add(new Tile(((screenLeftTX) + x) * 64, (float) (screenTopTY + d) * 64, z,
											TileID.Dirt_Grass_tile));
								}
							}
						}
					}
				}
			}
		} else if (playerTY > prevTY) {
			// player moved down
			for (int d = 0; d < diff; d++) {
				for (int x = 0; x < mapWidth; x++) {
					int top = (int) Math.ceil(mainNoise.generateHeight((screenLeftTX) + x)) + 8;
					int topUnder = (int) Math.ceil(stoneNoise.generateHeight((screenLeftTX) + x)) + 18;
					int ironGen = (int) ((int) Math.ceil(ironNoise.generateHeight((screenLeftTX) + x))
							+ Math.ceil((ironBottom - ironTop) / 2)) + ironTop;
					int coalGen = (int) ((int) Math.ceil(coalNoise.generateHeight((screenLeftTX) + x))
							+ Math.ceil((coalBottom - coalTop) / 2)) + coalTop;
					int diamondGen = (int) ((int) Math.ceil(diamondNoise.generateHeight((screenLeftTX) + x))
							+ Math.ceil((diamondBottom - diamondTop) / 2)) + diamondTop;
					int goldGen = (int) ((int) Math.ceil(goldNoise.generateHeight((screenLeftTX) + x))
							+ Math.ceil((goldBottom - goldTop) / 2)) + goldTop;
					int silverGen = (int) ((int) Math.ceil(silverNoise.generateHeight((screenLeftTX) + x))
							+ Math.ceil((silverBottom - silverTop) / 2)) + silverTop;

					for (int z = 0; z < 2; z++) {
						boolean changed = false;

						for (int i = 0; i < changes.size(); i++) {
							String[] changesSplit = changes.get(i).split(" ");
							int tempx = Integer.parseInt(changesSplit[0]);
							int tempy = Integer.parseInt(changesSplit[1]);
							int tempz = Integer.parseInt(changesSplit[2]);
							char tempChangeType = changesSplit[3].charAt(0);
							String tempTileChange = changesSplit[4];
							if (tempx == (screenLeftTX) + x && tempy == screenBottomTY + d && tempz == z) {
								if (tempChangeType == 'p')
									tiles.add(new Tile(tempx * 64, tempy * 64, tempz, TileID.valueOf(tempTileChange)));
								changed = true;
							}
						}

						if(!changed){
							if (screenBottomTY - d >= ironTop && screenBottomTY - d <= ironBottom
									&& screenBottomTY - d == ironGen) {
								tiles.add(new Tile(((screenLeftTX) + x) * 64, (float) (screenBottomTY - d) * 64, z,
										TileID.Stone_Iron_tile));
							} else if (screenBottomTY - d >= coalTop && screenBottomTY - d <= coalBottom
									&& screenBottomTY - d == coalGen) {
								tiles.add(new Tile(((screenLeftTX) + x) * 64, (float) (screenBottomTY - d) * 64, z,
										TileID.Stone_Coal_tile));
							} else if (screenBottomTY - d >= diamondTop && screenBottomTY - d <= diamondBottom
									&& screenBottomTY - d == diamondGen) {
								tiles.add(new Tile(((screenLeftTX) + x) * 64, (float) (screenBottomTY - d) * 64, z,
										TileID.Stone_Diamond_tile));
							} else if (screenBottomTY - d >= goldTop && screenBottomTY - d <= goldBottom
									&& screenBottomTY - d == goldGen) {
								tiles.add(new Tile(((screenLeftTX) + x) * 64, (float) (screenBottomTY - d) * 64, z,
										TileID.Stone_Gold_tile));
							} else if (screenBottomTY - d >= silverTop && screenBottomTY - d <= silverBottom
									&& screenBottomTY - d == silverGen) {
								tiles.add(new Tile(((screenLeftTX) + x) * 64, (float) (screenBottomTY - d) * 64, z,
										TileID.Stone_Silver_tile));
							} else {
								if (screenBottomTY - d >= bedrockLevel) {
									tiles.add(new Tile(((screenLeftTX) + x) * 64, (float) (screenBottomTY - d) * 64, z,
											TileID.Redstone_tile));
								} else if (top < screenBottomTY - d && topUnder + 1 > screenBottomTY) {
									tiles.add(new Tile(((screenLeftTX) + x) * 64, (float) (screenBottomTY - d) * 64, z,
											TileID.Dirt_tile));
								} else if (topUnder < screenBottomTY - d) {
									tiles.add(new Tile(((screenLeftTX) + x) * 64, (float) (screenBottomTY - d) * 64, z,
											TileID.Greystone_tile));
								} else if (top == screenBottomTY - d && z == 0) {
									tiles.add(new Tile(((screenLeftTX) + x) * 64, (float) (screenBottomTY - d) * 64, z,
											TileID.Dirt_Grass_tile));
								}
							}
						}
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
			entities.get(i).update(entities, tiles, changes, camX, camY);
		}
		for (int j = 0; j < tiles.size(); j++) {
			tiles.get(j).update(entities, tiles, changes, camX, camY);
			if (tiles.get(j).getHealth() == 0) {
				saveChange(changes, (int) tiles.get(j).getX() / 64, (int) tiles.get(j).getY() / 64,
						(int) tiles.get(j).getZ() / 64, 'd', "VOID");
				tiles.remove(j);
			}
		}
		player.update(entities, tiles, changes, camX, camY);

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

	private void saveChange(ArrayList<String> changes, int x, int y, int z, char changeType, String tile) {
		String change = Integer.toString(x) + " " + Integer.toString(y) + " " + Integer.toString(z) + " " + changeType
				+ " " + tile;
		if (changes.size() > 0) {
			boolean prevChanged = false;

			for (int i = 0; i < changes.size(); i++) {
				String[] changesSplit = changes.get(i).split(" ");
				int tempx = Integer.parseInt(changesSplit[0]);
				int tempy = Integer.parseInt(changesSplit[1]);
				int tempz = Integer.parseInt(changesSplit[2]);
				if (tempx == x && tempy == y && tempz == z) {
					changes.remove(i);
					changes.add(change);
					prevChanged = true;
					System.out.println(change);
				}
			}

			if (!prevChanged) {
				changes.add(change);
				System.out.println(change);
			}
		} else {
			changes.add(change);
			System.out.println(change);
		}
	}
}
