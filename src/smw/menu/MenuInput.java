package smw.menu;

import java.awt.event.KeyEvent;

import smw.Game;
import smw.ui.Keyboard;

/**
 * The MenuInput class provides an abstraction layer from the various user inputs to the game.
 * TODO - Eventually it will combine inputs from all devices and potentially provide hooks to provide "last input" from a given player.
 */
public class MenuInput {
  
  /** Defines the user input actions relevant to a menu. */
  public enum Action { NO_ACTION, UP, DOWN, LEFT, RIGHT, SELECT, ESC }

  /** The last unique user input action. */
  private Action lastAction = Action.NO_ACTION;
  
  /** Returns the last unique user input action. */
  public Action getLastAction() {
    return lastAction;
  }
  
  /** Updates the input state, must be called during game update. */
  public void update() {
    Keyboard kb = Game.keyboard;
    
    // TODO - eventually combine inputs from other devices
    boolean up = kb.getKeyPress(KeyEvent.VK_UP);
    boolean down = kb.getKeyPress(KeyEvent.VK_DOWN);
    boolean left = kb.getKeyPress(KeyEvent.VK_LEFT);
    boolean right = kb.getKeyPress(KeyEvent.VK_RIGHT);
    boolean select = kb.getKeyPress(KeyEvent.VK_ENTER);
    boolean escape = kb.getKeyPress(KeyEvent.VK_ESCAPE);

    // Determine what the current action is.
    Action currentAction = Action.NO_ACTION;
    if (up) {
      currentAction = Action.UP;
    } else if (down) {
      currentAction = Action.DOWN;
    } else if (left) {
      currentAction = Action.LEFT;
    } else if (right) {
      currentAction = Action.RIGHT;
    } else if (select) {
      currentAction = Action.SELECT;
    } else if (escape) {
      currentAction = Action.ESC;
    }
    
    if (lastAction != currentAction) {
      lastAction = currentAction;
    }
  }
}
