package smw.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

//This is a temporary implementation to just get a block moving
public class Keyboard extends PlayerControl implements KeyListener{
  int left;
  int right;
  int up;
  int down;
  int jump;
  int run; //TODO this might (probably) become action button
  
  int mostRecentlyPushed;
  
  final HashMap<Integer, Boolean> keyMap = new HashMap<Integer, Boolean>();

  public Keyboard(int left,
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
  public float getDirection(){
    if(keyMap.get(right)){
      return (keyMap.get(left)) ?  0 : 1.0f;
    }
    else{
      return (keyMap.get(left)) ? -1.0f : 0;
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
          mostRecentlyPushed = code;
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
  
  void waitForNextKey(){
    int sizeBefore = keyMap.size();
    while(sizeBefore == keyMap.size()){
      //Wait until something gets set
    }
  }

  public void setLeft() {
    waitForNextKey();
    left = mostRecentlyPushed;
  }

  public void setRight() {
    waitForNextKey();
    right = mostRecentlyPushed;
  }

  public void setJump() {
    waitForNextKey();
    jump = mostRecentlyPushed;
  }

  public void setRun() {
    waitForNextKey();
    run = mostRecentlyPushed;
  }
}

