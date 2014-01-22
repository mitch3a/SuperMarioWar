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
                  int jump,
                  int run){
    this.left  = left;
    this.right = right;
    this.jump  = jump;
    this.run   = run;
    
    component.addKeyListener(this);
    keyMap.put(this.left, false);
    keyMap.put(this.right, false);
    keyMap.put(this.jump, false);
    keyMap.put(this.run, false); 
  }
  
  public void poll(){
  	//TODO mk once keyboard actually polls (instead of just listening all the time) then this can be abstract
  }
  
  @Override
  public int getDirection(){
    if(keyMap.get(right)){
      return (keyMap.get(left)) ?  0 : 1;
    }
    else{
      return (keyMap.get(left)) ? -1 : 0;
    }
  }
  
  @Override
  public boolean isJumping(){
    return keyMap.get(jump);
  }
  
  @Override
  public boolean isRunning(){
    return keyMap.get(run);
  }
  
  /*******************************************
   * This method waits until an unused key is 
   * pressed. That key is stored in mostRecentlyPushed
   *******************************************/
  void waitForNextKey(){
    int sizeBefore = keyMap.size();
    while(sizeBefore == keyMap.size()){
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

  public void setJumpButton() {
    waitForNextKey();
    jump = mostRecentlyPushed;
  }

  public void setRunButton() {
    waitForNextKey();
    run = mostRecentlyPushed;
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
}

