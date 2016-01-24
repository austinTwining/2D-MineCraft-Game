package platformer.entity;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import platformer.entity.tiles.Tile;

public abstract class Entity {
	protected float x, y;
	protected float velX, velY;
	protected int width, height;

	public Entity(float x, float y){
		this.x = x;
		this.y = y;
	}
	
	public abstract void draw(Graphics g);
	public abstract void update(ArrayList<Entity> entities, ArrayList<Tile> tiles, float xOffset, float yOffset);
	
	protected BufferedImage rotateImageTest(double d, BufferedImage image, int rotPointX, int rotPointY){
		BufferedImage rotImage = image;
		AffineTransform tx = new AffineTransform();
		tx.rotate(Math.toRadians(d), rotPointX, rotPointY);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		return op.filter(rotImage, null);
	}
	protected BufferedImage rotateImage(double d, BufferedImage image){
		double sin = Math.abs(Math.sin(Math.toRadians(d)));
		double cos = Math.abs(Math.cos(Math.toRadians(d)));
		
		int w = image.getWidth(), h = image.getHeight();
		
		int neww = (int) Math.floor(w*cos + h*sin);
		int newh = (int) Math.floor(h*cos + w*sin);
		
		BufferedImage rotImage = new BufferedImage(neww, newh, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2D = rotImage.createGraphics();
		
		 g2D.translate((neww-w)/2, (newh-h)/2);
		 g2D.rotate(Math.toRadians(d), w/2, h/2);
		 g2D.drawRenderedImage(image, null);
		 g2D.dispose();
		 
		 return rotImage;
	}
	
	//hitbox for entity
	public Rectangle getBounds(){return new Rectangle((int)(x), (int)(y), width, height);}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getVelX() {
		return velX;
	}

	public void setVelX(float velX) {
		this.velX = velX;
	}

	public float getVelY() {
		return velY;
	}

	public void setVelY(float velY) {
		this.velY = velY;
	}
}
