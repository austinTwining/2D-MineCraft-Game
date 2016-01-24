package platformer.state;

import java.awt.Graphics;

public abstract class State {
	protected GameStateManager gsm;
	
	public State(GameStateManager gsm){
		this.gsm = gsm;
	}
	
	public abstract void draw(Graphics g);
	public abstract void update();
}
