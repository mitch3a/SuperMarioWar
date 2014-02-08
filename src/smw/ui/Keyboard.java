package smw.ui;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

//This is a temporary implementation to just get a block moving
public class Keyboard extends PlayerControlBase implements KeyListener{
  //TODO this should probably tie into the PlayerControlBase
	int left;
  int right;
  int down;
  int jump;
  int run;
  
  //TODO this isn't the smartest way to do this. Maybe reconsider this
  //     and other PlayerControlBases for something better and similar
  int mostRecentlyPushed;
  
  //TODO this is ending up with all keys. Could probably use an array. Didn't
  //     feel like changing it until we ruled out replacing it with actual polling
  final HashMap<Integer, Boolean> keyMap = new HashMap<Integer, Boolean>();

  public Keyboard(Component component,
                	int left,
                  int right,
                  int down,
                  int jump,
                  int run) {
    this.left  = left;
    this.right = right;
    this.down  = down;
    this.jump  = jump;
    this.run   = run;
    
    component.addKeyListener(this);
    keyMap.put(this.left, false);
    keyMap.put(this.right, false);
    keyMap.put(this.down, false);
    keyMap.put(this.jump, false);
    keyMap.put(this.run, false); 
  }
  
  public void poll(){
  	//TODO mk once keyboard actually polls (instead of just listening all the time) then this can be abstract
  }
  
  @Override
  public int getDirection() {
    if(keyMap.get(right)){
      return (keyMap.get(left)) ?  0 : 1;
    }
    else{
      return (keyMap.get(left)) ? -1 : 0;
    }
  }
  
  @Override
  public boolean isJumping() {
    return keyMap.get(jump);
  }
  
  @Override
  public boolean isRunning() {
    return keyMap.get(run);
  }
  
  @Override
  public boolean isDown() {
    return keyMap.get(down);
  }
  
  /*******************************************
   * This method waits until an unused key is 
   * pressed. That key is stored in mostRecentlyPushed
   *******************************************/
  void waitForNextKey(){
    int sizeBefore = keyMap.size();
    while(sizeBefore == keyMap.size()) {
      //Wait until something gets set
    }
  }

  public void setLeftButton() {
    waitForNextKey();
    left = mostRecentlyPushed;
  }

  public void setRightButton() {
    waitForNextKey();
    right = mostRecentlyPushed;
  }
  
  public void setDownButton() {
    waitForNextKey();
    right = mostRecentlyPushed;
  }

  public void setJumpButton() {
    waitForNextKey();
    jump = mostRecentlyPushed;
  }

  public void setRunButton() {
    waitForNextKey();
    run = mostRecentlyPushed;
  }
  
  public void keyPressed(KeyEvent e) {
    int code = e.getKeyCode();
    e.consume();
    mostRecentlyPushed = code;
    keyMap.put(code, true);
  }
  
  public void keyReleased(KeyEvent e) {
    int code = e.getKeyCode();
    e.consume();
    
    keyMap.put(code, false);
  }

  public void keyTyped(KeyEvent e) {
    e.consume();
  }

  @Override
  public boolean isConnected() {
    return true; // TODO - probably always have a keyboard connected?
  }

  @Override
  public void release() { }

  @Override
  public boolean isPaused() {
    // TODO - Assign pause button and use it! (probably esc button)
    return false;
  }

  @Override
  public boolean isActionPressed() {
    // TODO - Assign action/powerup button (or would be just use run button?)
    return false;
  }
}

