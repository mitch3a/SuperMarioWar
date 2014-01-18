package smw;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import smw.ui.screen.GameFrame;

public class InputHandler implements KeyListener {

  //TODO - these are just temp for testing (or default?)
  public final int left = KeyEvent.VK_LEFT;
  public final int right = KeyEvent.VK_RIGHT;
  public final int up = KeyEvent.VK_UP;
  public final int down = KeyEvent.VK_DOWN;
  public final int jump = KeyEvent.VK_UP;
  
  // Keys to indicate if a control is pressed.
  public boolean leftKey = false;
  public boolean rightKey = false;
  public boolean upKey = false;
  public boolean downKey = false;
  public boolean jumpKey = false;
  
  public InputHandler(GameFrame gameFrame) {
    // TODO - this should take one of the "Game" classes as a param and add a key listener to it, not sure if this is the
    // right one or not! also to use this class, the Player class should own it and check for key pressed when
    // the player class is 'updated' in the update method of the game loop!
    gameFrame.addKeyListener(this);
  }
  
  void handleKeyEvent(int key, boolean pressed) {
    if(key == left){
      leftKey = pressed;
      return;
    }
    
    if(key == right){
      rightKey = pressed;
      return;
    }
    
    if(key == jump){
      jumpKey = pressed;
    }
  }
  
  @Override
  public void keyPressed(KeyEvent e) {
    handleKeyEvent(e.getKeyCode(), true);
  }

  @Override
  public void keyReleased(KeyEvent e) {
    handleKeyEvent(e.getKeyCode(), false);
  }

  @Override
  public void keyTyped(KeyEvent e) {
    // TODO - I don't think we need to implement anything here?
  }

}
