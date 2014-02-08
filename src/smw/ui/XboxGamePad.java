package smw.ui;

import smw.Utilities;
import ch.aplu.xboxcontroller.XboxController;
import ch.aplu.xboxcontroller.XboxControllerAdapter;

public class XboxGamePad extends PlayerControlBase {

  private XboxController xboxController;
  private int xbDirection;
  private boolean isDownPressed;
  private boolean isUpPressed;
  private boolean isJumping;
  private boolean isRunning;
  private boolean isPaused;
  private boolean isActionPressed;
  public boolean isConnected;
  
  
  /**
   * Sets up the Xbox controller for the provided player number.
   * @param player Player number.
   */
  public XboxGamePad(int player) {
    setType(PlayerControlBase.ControllerType.XBOX);
    // Create the controller and verify connection.
    xboxController = new XboxController(Utilities.is64bit() ? "xboxcontroller64" : "xboxcontroller", player, 50, 50);
    isConnected = xboxController.isConnected();
    if (!isConnected) { 
      xboxController.release();
      return;
    }
    xboxController.setLeftThumbDeadZone(0.25);
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
        //TODO mk need to add isUpPressed
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
        //TODO add isUpPressed
        if ((direction > 160.0) && (direction < 200.0)) {
          isDownPressed = true;
          xbDirection = 0;
        } else {
          isDownPressed = false;
          if ((direction > 45.0) && (direction < 135.0)) {
            xbDirection = 1;
          } else if ((direction > 225.0) && (direction < 315.0)) {
            xbDirection = -1;
          } else {
            xbDirection = 0;
          }
        }
      }
      public void leftThumbMagnitude(double magnitude) {
        // Need to clear direction when dead zone is hit.
        if (magnitude < 0.26) {
          xbDirection = 0;
          //TODO add isUpPressed
          isDownPressed = false;
        }
      }
      public void start(boolean pressed) {
        // If start is pressed, toggle the pause indicatation.
        if (pressed) {
          isPaused = !isPaused;
        }
      }
      // TODO - which one should be the powerup button?
      public void buttonB(boolean pressed) {
        isActionPressed = pressed;
      }
      public void buttonY(boolean pressed) {
        isActionPressed = pressed;
      }
      /* Probably won't use these buttons in this game!
      public void back(boolean pressed) {
        System.out.println("BACK");
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
  }

  @Override
  public int getDirection() {
    return xbDirection;
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
  public boolean isUp() {
    return isUpPressed;
  }
  
  @Override
  public boolean isConnected() {
    return isConnected;
  }
  
  @Override
  public boolean isActionPressed() {
    return isActionPressed;
  }
  
  @Override
  public boolean isPaused() {
    return isPaused;
  }
  
  @Override
  public void release() {
    xboxController.release();
  }

  // The following inherited methods do nothing, it's handled by the XboxControllerAdapter setup in the constructor.
  @Override
  public void poll() {}

  @Override
  public void setLeftButton() {}

  @Override
  public void setRightButton() {}

  @Override
  public void setDownButton() {}
  
  @Override
  public void setUpButton() {}

  @Override
  public void setJumpButton() {}

  @Override
  public void setRunButton() {}
}