package smw.world.hazards;

import smw.Game;
import smw.entity.Player;

//TODO don't need the animation part
public abstract class FirePirhanaPlant extends AnimatedHazard{
  
  enum State {
    in, movingOut, out, movingIn;
  }
  
  enum Direction{
    upLeft, downLeft, upRight, downRight;
  }
  
  float[] fireballAngles = {(float) (5*Math.PI/6), (float) (-5*Math.PI/6), (float) (Math.PI/6), (float) (-Math.PI/6)};
  
  static final int TIME_PER_FRAME = 0;
  float velocityMove = 0.1f;
  float maxTimeIn = 1000;
  float maxTimeOut = 1500;
  static final float TIME_OUT_BEFORE_FIREBALL = 1000;
  static final float FIREBALL_VELOCITY = 0.1f;
  boolean fireballShot = false;
  float timeInCurrentState = 0;
  State state = State.in;
  float offset = 0;
  Direction direction = Direction.downRight;
  final int fireballStartX;
  final int fireballStartY;
  
  final int MAX_OFFSET;

  protected FirePirhanaPlant(int x, int y, int middleHeadX, int middleHeadY, int width, int height, int maxOffset, int tileSheetX, int tileSheetY) {
    super(x, y, "pirhanaplant.png", width, height, TIME_PER_FRAME, tileSheetX, tileSheetY);
    
    fireballStartX = middleHeadX;
    fireballStartY = middleHeadY;
    
    MAX_OFFSET = maxOffset;
    setDirectionOffset();
  }
  
  public void switchDirection(){
    int i = (int)(4*Math.random());
    
    //TODO this will go away, but just for fun
    switch(i){
      case 0: direction = Direction.upLeft;
              break;
      case 1: direction = Direction.downLeft;
              break;
      case 2: direction = Direction.upRight;
              break;
      case 3: direction = Direction.downRight;
              break;
    }
    
    setDirectionOffset();
  }
  
  void setDirectionOffset(){
    switch(direction){
      case upLeft:    offsetX = 0;
                      break;
      case downLeft:  offsetX = 32;
                      break;
      case upRight:   offsetX = 64;
                      break;
      case downRight: offsetX = 96;
                      break;
    }
  }
  
  @Override
  public boolean shouldBeRemoved() {
    return false;
  }

  @Override
  public boolean kills(Player player) {
    return intersects(player);
  }

  @Override
  public void update(float timeDif_ms) {    
    //TODO don't need this, nothing animateds super.update(timeDif_ms);
    
    switch(state){
      case in:  timeInCurrentState += timeDif_ms;
                if(maxTimeIn < timeInCurrentState){
                  state = State.movingOut;
                  timeInCurrentState = 0;
                  fireballShot = false;
                  switchDirection();
                }
                break;
               
      case out: timeInCurrentState += timeDif_ms;
                if(!fireballShot && TIME_OUT_BEFORE_FIREBALL < timeInCurrentState){
                  fireballShot = true;
                  MovingFireBall movingFireBall = new MovingFireBall(fireballStartX, fireballStartY, FIREBALL_VELOCITY, fireballAngles[direction.ordinal()]);
                  Game.world.queueAnimatedHazard(movingFireBall, 2);
                }
                if(maxTimeOut < timeInCurrentState){
                  state = State.movingIn;
                  timeInCurrentState = 0;
                }
                break;
                
      case movingIn:  offset -= velocityMove*timeDif_ms;
                      if(offset < 0){
                        offset = 0;
                        state = State.in;
                      }
                      break;
      case movingOut: offset += velocityMove*timeDif_ms;
                      if(offset >= MAX_OFFSET){
                        offset = MAX_OFFSET;
                        state = State.out;
                      }
                      break;
    }
  }
  
  /**
   * Abstract class for a plant that moves up or down
   */
 static abstract class VerticalPlant extends FirePirhanaPlant{
   final int maxHeight;
   
   protected VerticalPlant(int x, int y, int middleHeadX, int middleHeadY, int tileSheetX, int tileSheetY) {
     super(x, y, middleHeadX, middleHeadY, 32, 48, 48, tileSheetX, tileSheetY);
     
     maxHeight = 48;
   }
    
   @Override
   public void nextFrame() {
     //TODO nothing, but should probably just not have this be animated
   }    
 }
  
 /**
  * Abstract class for a plant that moves up out of a pipe
  */
  public static abstract class Up extends VerticalPlant{
    final int startingY;
    
    public Up(int x, int y, int tileSheetX, int tileSheetY) {
      super(x, y + 32, x, y - 16, tileSheetX, tileSheetY);
      
      startingY = y + 32;
    }
    
    @Override
    public void update(float timeDif_ms) {
      super.update(timeDif_ms);
      y = startingY - (int)offset;
      height = (int)offset;
    }
  }
  
  /**
   * Red fireball Pirhana Plant that goes up out of a pipe
   */
  public static class RedUp extends Up{
    public RedUp(int x, int y){
      super(x, y, 0, 48);
    }
  }
  
  /**
   * Green fireball Pirhana Plant that goes up out of a pipe
   */
  public static class GreenUp extends Up{
    public GreenUp(int x, int y){
      super(x, y, 0, 0);
    }
  }
  
  /**
   * Abstract class for a plant that moves down out of a pipe
   */
  public static abstract class Down extends VerticalPlant{
    final int startingHeight;
    
    public Down(int x, int y, int tileSheetX, int tileSheetY) {
      super(x, y, x, y + 16, tileSheetX, tileSheetY);
      
      startingHeight = (int) height;
    }
    
    @Override
    public void update(float timeDif_ms) {
      super.update(timeDif_ms);
      
      height = (int)offset;
      offsetY = (int)(startingHeight - (int)offset);
    }
  }
 
  
  /**
   * Red fireball Pirhana Plant that goes down out of a pipe
   */
  public static class RedDown extends Down{
    public RedDown(int x, int y){
      super(x, y, 128, 48);
    }
  }
  
  /**
   * Green fireball Pirhana Plant that goes down out of a pipe
   */
  public static class GreenDown extends Down{
    public GreenDown(int x, int y){
      super(x, y, 128, 0);
    }
  }
  
}
