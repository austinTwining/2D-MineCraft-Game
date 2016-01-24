package platformer.entity.tiles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import platformer.entity.Entity;
import platformer.game.Main;

public class Tile extends Entity{
	
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
		g.drawImage(id.getTexture(), (int)(x), (int)(y), null);
		
		if(z == 1) g.drawImage(id.tint, (int)x, (int)y, null);
		
		if(Main.DEBUG){
			g.setColor(Color.RED);
			((Graphics2D) g).draw(getBounds());
		}
	}

	@Override
	public void update(ArrayList<Entity> entities, ArrayList<Tile> tiles, float xOffset, float yOffset) {
	}
	
	public TileID getID(){
		return id;
	}
	
	public float getZ(){
		return z;
	}
	public int getHealth(){
		return health;
	}
	public void removeHealth(int amount){
		health -= amount;
	}
}
