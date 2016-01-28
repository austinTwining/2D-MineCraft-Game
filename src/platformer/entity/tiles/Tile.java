package platformer.entity.tiles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import platformer.entity.Entity;
import platformer.game.Main;

public class Tile extends Entity {

	private TileID id;
	private float z;
	private int health;

	public Tile(float x, float y, float z, TileID id) {
		super(x, y);
		this.z = z;
		this.id = id;
		this.width = id.getTexture().getWidth();
		this.height = id.getTexture().getHeight();
		this.health = id.getHealth();
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(id.getTexture(), (int) (x), (int) (y), null);

		if (z == 1)
			g.drawImage(id.tint, (int) x, (int) y, null);

		if (Main.DEBUG) {
			g.setColor(Color.RED);
			((Graphics2D) g).draw(getBounds());
		}
	}

	@Override
	public void update(ArrayList<Entity> entities, ArrayList<Tile> tiles, ArrayList<String> changes, float xOffset, float yOffset) {
	}

	public TileID getID() {
		return id;
	}

	public float getZ() {
		return z;
	}

	public int getHealth() {
		return health;
	}
	
	public String IDtoString(){
		switch(id){
		case Dirt_Grass_tile:
			return "Dirt_Grass_tile";
		case Dirt_tile:
			return "Dirt_tile";
		case Greystone_tile:
			return "Greystone_tile";
		case Redstone_tile:
			return "Redstone_tile";
		case Brick_Grey_tile:
			return "Brick_Grey_tile";
		case Brick_Red_tile:
			return "Brick_Red_tile";
		case Stone_Iron_tile:
			return "Stone_Iron_tile:";
		case Stone_Coal_tile:
			return "Stone_Coal_tile";
		case Stone_Diamond_tile:
			return "Stone_Diamond_tile";
		case Stone_Gold_tile:
			return "Stone_Gold_tile";
		case Stone_Silver_tile:
			return "Stone_Silver_tile";
		default:
			return "VOID";
		}
	}

	public void removeHealth(int amount) {
		health -= amount;
	}
}
