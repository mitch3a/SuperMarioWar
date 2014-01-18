package ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

import model.Player;

//This is a temporary implementation to just get a block moving..
public class PlayerControl implements KeyListener{
	Player player;
	
	final int left;
	final int right;
	final int up;
	final int down;
	final int jump;
	final int run; //TODO this might (probably) become action button
	
	final HashMap<Integer, Boolean> keyMap = new HashMap<Integer, Boolean>();
	
	public PlayerControl(int left,
	                     int right,
	                     int up, 
	                     int down,
	                     int jump,
	                     int run){
		
		this.left  = left;
		this.right = right;
		this.up    = up;
		this.down  = down;
		this.jump  = jump;
		this.run   = run;
		
	  keyMap.put(this.left, false);
	  keyMap.put(this.right, false);
	  keyMap.put(this.up, false);
	  keyMap.put(this.down, false);
	  keyMap.put(this.jump, false);
	  keyMap.put(this.run, false); 
	}
	
	//TODO this is BAAAHHHDDDDD
	public int getDirection(){
	  if(keyMap.get(right)){
	    return (keyMap.get(left)) ?  0 : 1;
	  }
	  else{
	    return (keyMap.get(left)) ? -1 : 0;
	  }
	}
	
	public boolean isJumping(){
	  return keyMap.get(jump);
	}
	
	public boolean isRunning(){
	  return keyMap.get(run);
	}
	
	@Override
  public void keyPressed(KeyEvent e){
          int code = e.getKeyCode();
          e.consume();
          
          keyMap.put(code, true);
  }
	
	@Override
  public void keyReleased(KeyEvent e){
          int code = e.getKeyCode();
          e.consume();
          
          keyMap.put(code, false);
  }

  @Override
  public void keyTyped(KeyEvent e) {
    e.consume();
  }
}
