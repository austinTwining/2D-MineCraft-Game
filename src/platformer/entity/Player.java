package platformer.entity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import platformer.entity.tiles.Tile;
import platformer.entity.tiles.TileID;
import platformer.game.Main;

public class Player extends Entity {

	private BufferedImage texture = Main.imageLoader.loadImage("/Characters64/maleplayer/player_male50%.png");
	
	private BufferedImage head = Main.imageLoader.loadImage("/Characters64/maleplayer/male_head50%.png");
	private BufferedImage body = Main.imageLoader.loadImage("/Characters64/maleplayer/male_body50%.png");
	private BufferedImage arm = Main.imageLoader.loadImage("/Characters64/maleplayer/male_arm50%.png");
	private BufferedImage leg = Main.imageLoader.loadImage("/Characters64/maleplayer/male_leg50%.png");
	
	private boolean jumping = true;
	private float gravity = 0.98f;
	
	private TileID currentBlock = TileID.Dirt_tile;

	public Player(float x, float y) {
		super(x, y);
		this.width = texture.getWidth();
		this.height = texture.getHeight();
		this.velX = 0;
		this.velY = 0;
	}
	@Override
	public void draw(Graphics g) {
		//draw player texture
		animatePlayer(g);
		//draw hitboxes
		if(Main.DEBUG){
			g.setColor(Color.RED);
			((Graphics2D) g).draw(getBoundsLeft());
			((Graphics2D) g).draw(getBoundsRight());
			((Graphics2D) g).draw(getBoundsTop());
			((Graphics2D) g).draw(getBoundsBottom());
		}
	}
	
	private void animatePlayer(Graphics g){
		g.drawImage(rotateImage(0, texture), (int) x, (int) y, null);
	}
	
	long lastTimeMouse1 = System.currentTimeMillis();
	double nsPerMousePress1 = 250;
	
	long lastTimeMouse2 = System.currentTimeMillis();
	double nsPerMousePress2 = 250;

	@Override
	public void update(ArrayList<Entity> entities, ArrayList<Tile> tiles, float xOffset, float yOffset) {
		int bottomTX = (int) Math.ceil(x / 64);
		int bottomTY = (int) Math.ceil(y / 64);
		boolean onGround = false;
		
		for(int j = 0; j < entities.size(); j++){
			//entity collision
		}
		for(int k = 0; k < tiles.size(); k++){
			
			if(tiles.get(k).getX() / 64 == bottomTX - 1 && tiles.get(k).getY() / 64 == bottomTY + 1 && tiles.get(k).getZ() == 0) onGround = true;
			
			//tile collision
			if(tiles.get(k).getID().isSolid() && tiles.get(k).getZ() != 1){
				if(getBoundsBottom().intersects(tiles.get(k).getBounds())){
					velY = 0;
					y = tiles.get(k).getY() - height;
					jumping = false;
					onGround = true;
				}
				if(getBoundsTop().intersects(tiles.get(k).getBounds())){
					velY = 0;
					y = tiles.get(k).getY() + 64;
				}
				
				if(getBoundsLeft().intersects(tiles.get(k).getBounds())){
					velX = 0;
					x = tiles.get(k).getX() + 64;
				}
				if(getBoundsRight().intersects(tiles.get(k).getBounds())){
					velX = 0;
					x = tiles.get(k).getX() - width;
				}
			}
		}
		//keybord
		//jump
		if(Main.isKeyPressed(KeyEvent.VK_SPACE)) jump();
		//left&right
		if(Main.isKeyPressed(KeyEvent.VK_A)) velX = -5; 
		if(Main.isKeyPressed(KeyEvent.VK_D)) velX = 5; 
		if(!(Main.isKeyPressed(KeyEvent.VK_A) || Main.isKeyPressed(KeyEvent.VK_D))) velX = 0;
		//block select
		if(Main.isKeyPressed(KeyEvent.VK_1)) currentBlock = TileID.Dirt_tile;
		if(Main.isKeyPressed(KeyEvent.VK_2)) currentBlock = TileID.Dirt_Grass_tile;
		if(Main.isKeyPressed(KeyEvent.VK_3)) currentBlock = TileID.Greystone_tile;
		if(Main.isKeyPressed(KeyEvent.VK_4)) currentBlock = TileID.Redstone_tile;
		if(Main.isKeyPressed(KeyEvent.VK_5)) currentBlock = TileID.Brick_Grey_tile;
		if(Main.isKeyPressed(KeyEvent.VK_6)) currentBlock = TileID.Brick_Red_tile;
		//mouse
		//destroy block
		if(Main.isMouseButtonPressed(MouseEvent.BUTTON1)){
			long now = System.currentTimeMillis();
			
			// button debouncing
			if(now - lastTimeMouse1 >= nsPerMousePress1){
				//get the tile coords of the mouse
				int tx = (int) Math.ceil((Main.getMouseX() + xOffset) / 64) - 1;
				int ty = (int) Math.ceil((Main.getMouseY() + yOffset) / 64) - 1;
				System.out.println(tx + " | " + ty + " | " + bottomTY);
				// go through the tiles array to see if there is any tiles at mouses tileX and tileY
				for(int i = 0; i < tiles.size(); i++){
					// remove tile if there it is in the foreground
					if(tiles.get(i).getX() / 64 == tx && tiles.get(i).getY() / 64 == ty && tiles.get(i).getZ() == 0){
						if(tiles.get(i).getHealth() > 0){
							tiles.get(i).removeHealth(1);
						}
						break;
					// if the tile is in the background go through the list again and see if there is one in front of it
					}else if(tiles.get(i).getX() / 64 == tx && tiles.get(i).getY() / 64 == ty && tiles.get(i).getZ() == 1){
						boolean isForeground = false;
						for(int t = 0; t < tiles.size(); t++){
							if(tiles.get(t).getX() / 64 == tx && tiles.get(t).getY() / 64 == ty && tiles.get(t).getZ() == 0){
								isForeground = true;
							}
						}
						// if none in front of it remove it
						if(!isForeground && tiles.get(i).getHealth() > 0){
							tiles.get(i).removeHealth(1);
						}
					}
				}
				
				lastTimeMouse1 = now;
			}
		}
		
		//place block
		if(Main.isMouseButtonPressed(MouseEvent.BUTTON3)){
			long now = System.currentTimeMillis();
			
			// button debouncing
			if(now - lastTimeMouse2 >= nsPerMousePress2){
				//get the tile coords of the mouse
				int tx = (int) Math.ceil((Main.getMouseX() + xOffset) / 64) - 1;
				int ty = (int) Math.ceil((Main.getMouseY() + yOffset) / 64) - 1;
				boolean isFull1 = false;
				boolean isFull0 = false;
				System.out.println(tx + " | " + ty + " | " + bottomTY);
				// go through the tiles array and see if the tiles are full where you clicked
				for(int i = 0; i < tiles.size(); i++){
					if(tiles.get(i).getX() / 64 == tx && tiles.get(i).getY() / 64 == ty && tiles.get(i).getZ() == 1){
						isFull1 = true;
					}else if(tiles.get(i).getX() / 64 == tx && tiles.get(i).getY() / 64 == ty && tiles.get(i).getZ() == 0){
						isFull0 = true;
					}
				}
				// place block in background if there is no tiles in that tile
				if(!isFull1 && !isFull0){
					Tile tempTile = new Tile(tx * 64, ty * 64, 1, currentBlock);
					tiles.add(tempTile);
					System.out.println(tempTile.getX() / 64 + " | " + tempTile.getY() / 64);
				}
				//place in the foreground if there is a tile in the background
				else if(isFull1 && !isFull0){
					Tile tempTile = new Tile(tx * 64, ty * 64, 0, currentBlock);
					tiles.add(tempTile);
					System.out.println(tempTile.getX() / 64 + " | " + tempTile.getY() / 64);
				}
				
				lastTimeMouse2 = now;
			}
		}
		
		// check if it needs to be affected by gravity
		if (jumping || !onGround) {
			velY += gravity;
		}

		// add the velocity to the position
		x += velX;
		y += velY;
	}

	// makes the player jump
	public void jump() {
		if (!jumping) {
			velY = -15;
			jumping = true;
		}
	}
	
	public TileID getCurrentBlock(){
		return currentBlock;
	}
	
	//player bounding boxes
	public Rectangle getBoundsLeft(){return new Rectangle((int)x, (int)y + 5, 5, height - 10);}
	public Rectangle getBoundsRight(){return new Rectangle((int)x + (width - 5), (int)y + 5, 5, height - 10);}
	public Rectangle getBoundsTop(){return new Rectangle((int)x + (width / 4), (int)y, width/2, height/2);}
	public Rectangle getBoundsBottom(){return new Rectangle((int)x + (width / 4), (int)y + (height/2), width/2, height/2);}

}
