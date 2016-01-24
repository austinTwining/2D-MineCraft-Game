package platformer.entity.tiles;

import java.awt.image.BufferedImage;

import platformer.game.Main;

public enum TileID {
	Dirt_Grass_tile(Main.imageLoader.loadImage("/Tiles64/dirt_grass.png"), true, 1),
	Dirt_tile(Main.imageLoader.loadImage("/Tiles64/dirt.png"), true, 1),
	Greystone_tile(Main.imageLoader.loadImage("/Tiles64/greystone.png"), true, 1),
	Redstone_tile(Main.imageLoader.loadImage("/Tiles64/redstone.png"), true, 1),
	Brick_Grey_tile(Main.imageLoader.loadImage("/Tiles64/brick_grey.png"), true, 1),
	Brick_Red_tile(Main.imageLoader.loadImage("/Tiles64/brick_red.png"), true, -1);
	
	private final BufferedImage texture;
	private boolean isSolid;
	
	private int health;
	
	public final BufferedImage tint;
	
	private TileID(BufferedImage texture, boolean solid, int health) {
		this.texture = texture;
		this.isSolid = solid;
		this.health = health;
		tint = Main.imageLoader.loadImage("/Tiles64/Z1tint.png");
	}
	
	public BufferedImage getTexture() {
		return texture;
	}
	
	public boolean isSolid(){
		return isSolid;
	}
	public int getHealth(){
		return health;
	}
}
