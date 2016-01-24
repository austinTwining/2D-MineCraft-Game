package platformer.state;

import java.awt.Graphics;
import java.util.ArrayList;

public class GameStateManager {
	
	public static final int STATE0 = 0;
	public static final int STATE1 = 1;
	public static final int STATE2 = 2;
	public static final int STATE3 = 3;
	
	private ArrayList<State> gameStates = new ArrayList<State>();
	private int currentState = STATE0;
	
	public GameStateManager(){
		gameStates.add(new LevelOneState(this));
	}
	
	public void setState(int state){
		currentState = state;
	}
	
	public void draw(Graphics g){
		gameStates.get(currentState).draw(g);
	}
	
	public void update(){
		gameStates.get(currentState).update();
	}

}
