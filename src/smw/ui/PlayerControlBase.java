package smw.ui;

/****************************************************
 * This class is a base used to control a player. Any
 * form of input must extend this class.
 ***************************************************/
public abstract class PlayerControlBase{
	
	/***************************************************
	 * This enum is to keep track of which component 
	 * index each button is mapped to. 
	 * 
	 * NOTE: The internal index is used for indexing
	 *       into an array. It is NOT used to store
	 *       the component index itself
	 ***************************************************/
	public enum PlayerButton{
		LEFT(0), RIGHT(1), JUMP(2), RUN(3);
		
		final static int NUM_BUTTONS_USED = 4;
		final int index;
		PlayerButton(int i){
			index = i;
		}
	}
  
	/******************************************************
	 * This method is used to initialize the button mapping.
	 * It will prompt the user for each button, and wait
	 * for the user to feed it one. The first button pressed
	 * will be stored as that action.
	 ******************************************************/
  public void setup(){
    System.out.println("Set your left button");
    setLeftButton();
    System.out.println("Set your right button");
    setRightButton();  
    System.out.println("Set your jump button");
    setJumpButton();
    System.out.println("Set your run button");
    setRunButton();
  }
  
  /******************************************************
   * This method is to poll the control. This ensures
   * that when the GET methods are called, their state
   * is up to date.
   ****************************************************/
  abstract public void poll();

  /****************************************************
   * This method will wait for a button to be pressed and
   * Store it as the left button
   ****************************************************/
  abstract public void setLeftButton();
  
  /****************************************************
   * This method will wait for a button to be pressed and
   * Store it as the right button
   ****************************************************/
  abstract public void setRightButton();
  
  /****************************************************
   * This method will wait for a button to be pressed and
   * Store it as the jump button
   ****************************************************/
  abstract public void setJumpButton();
  
  /****************************************************
   * This method will wait for a button to be pressed and
   * Store it as the run button
   ****************************************************/
  abstract public void setRunButton();
  
  /****************************************************
   * This methods is used to grab the current direction.
   * @return (-1) for Left, (+1) for Right, (0) for neither
   ****************************************************/
  abstract public int getDirection();
  
  /****************************************************
   * This method is used to grab the state of the
   * jump button
   ****************************************************/
  abstract public boolean isJumping();  
  
  /****************************************************
   * This method is used to grab the state of the
   * run button
   ****************************************************/
  abstract public boolean isRunning();
}
