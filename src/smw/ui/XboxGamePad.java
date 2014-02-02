package smw.ui;

import smw.Utilities;
import ch.aplu.xboxcontroller.XboxController;
import ch.aplu.xboxcontroller.XboxControllerAdapter;

public class XboxGamePad extends PlayerControlBase {

  private XboxController xboxController = null;
  
  private int thumbDir;
  private int xbDirection;
  private double leftThumbDirection;
  private boolean isDownPressed;
  private boolean isJumping;
  private boolean isRunning;
  
  public boolean isConnected;
  
  public XboxGamePad(int player) {
    // Create the controller and verify connection.
    xboxController = new XboxController(Utilities.is64bit() ? "xboxcontroller64" : "xboxcontroller", player, 50, 50);
    isConnected = xboxController.isConnected();
    if (!isConnected) { 
      xboxController.release();
      return;
    }
    // Setup the input listener.
    xboxController.addXboxControllerListener(new XboxControllerAdapter() {
      public void buttonA(boolean pressed) {
        isJumping = pressed;
      }
      public void buttonX(boolean pressed) {
        isRunning = pressed;
      }
      public void dpad(int direction, boolean pressed) {
        /* D-Pad Mapping
         * 7 0 1
         *  \|/
         * 6- -2
         *  /|\
         * 5 4 3
         */
        switch (direction) {
          case 1:
          case 2:
          case 3:
            // Moving left case.
            xbDirection = pressed ? 1 : 0;
            break;
          case 4:
            // Moving down case.
            isDownPressed = pressed;
            break;
          case 5:
          case 6:
          case 7:
            // Moving right case.
            xbDirection = pressed ? -1 : 0;
            break;
        }
      }
      public void leftThumbDirection(double direction) {
        leftThumbDirection = direction;
      }
      public void leftThumbMagnitude(double magnitude) {
        //System.out.println(magnitude);
        int mag = (int)(magnitude * 100.0);
        System.out.println(mag);
        // TODO - seems like ~0.25 would be a good value to start moving... maybe?
        //System.out.println("dir="+leftThumbDirection+" mag="+magnitude+" dir="+thumbDir);
        if (mag > 10) {
          if ((leftThumbDirection > 70.0) && (leftThumbDirection < 92.0)) {
            //thumbDir = (magnitude >= 0.20) ? 1 : 0;
            thumbDir = 1;
            //mag
            //System.out.println(thumbDir);
          } else if ((leftThumbDirection > 260.0) && (leftThumbDirection < 315.0)) {
            thumbDir = -1;
          } else if ((leftThumbDirection > 187.0) && (leftThumbDirection < 193.0)) {
            isDownPressed = true;
          }
        } else {
          thumbDir = 0;
        }
      }
      public void start(boolean pressed) {
        System.out.println("START");
        // TODO - PAUSE GAME
      }
      /* Probably won't use these buttons in this game!
      public void back(boolean pressed) {
        System.out.println("BACK");
      }
      public void buttonB(boolean pressed) {
        System.out.println("B");
      }
      public void buttonY(boolean pressed) {
        System.out.println("Y");
      }
      public void leftShoulder(boolean pressed) {
        System.out.println("LB");
      }
      public void leftThumb(boolean pressed) {
        System.out.println("L3");
      }
      public void leftTrigger(double value) {
        System.out.println("LT");
      }
      public void rightShoulder(boolean pressed) {
        System.out.println("RB");
      }
      public void rightThumb(boolean pressed) {
        System.out.println("R3");
      }
      public void rightThumbDirection(double direction) {
        System.out.println("");
      }
      public void rightThumbMagnitude(double magnitude) {
        System.out.println("");
      }
      public void rightTrigger(double value) {
        System.out.println("RT");
      }
      */
    });
    // TODO - do we want a dead zone setup?
    //xboxController.setLeftThumbDeadZone(0.2);
  }

  @Override
  public int getDirection() {
    return (xbDirection != 0) ? xbDirection : thumbDir;
  }

  @Override
  public boolean isJumping() {
    return isJumping;
  }

  @Override
  public boolean isRunning() {
    return isRunning;
  }

  @Override
  public boolean isDown() {
    return isDownPressed;
  }
  
  @Override
  public boolean isConnected() {
    return isConnected;
  }
  
  // The following inherited methods do nothing, it's handled by the XboxControllerAdapter setup in the ctor.
  @Override
  public void poll() {}

  @Override
  public void setLeftButton() {}

  @Override
  public void setRightButton() {}

  @Override
  public void setDownButton() {}

  @Override
  public void setJumpButton() {}

  @Override
  public void setRunButton() {}
}