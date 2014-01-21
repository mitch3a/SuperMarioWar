package smw.ui;

import java.awt.event.KeyListener;

//This is a temporary implementation to just get a block moving 
public abstract class PlayerControl implements KeyListener {//TODO this implements is only for now
  
  public void setup(){
    System.out.println("Set your left button");
    setLeft();
    System.out.println("Set your right button");
    setRight();  
    System.out.println("Set your jump button");
    setJump();
    System.out.println("Set your run button");
    setRun();
  }

  abstract public void setLeft();
  abstract public void setRight();
  abstract public void setJump();
  abstract public void setRun();
  
  abstract public float getDirection();
  abstract public boolean isJumping();
  abstract public boolean isRunning();

}
