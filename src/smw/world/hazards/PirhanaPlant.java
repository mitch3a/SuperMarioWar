package smw.world.hazards;

import smw.entity.Player;

public abstract class PirhanaPlant extends AnimatedHazard{
  
  enum State {
    in, movingOut, out, movingIn;
  }
  
  static final int TIME_PER_FRAME = 150;
  float velocityMove = 0.1f;
  float maxTimeIn = 1000;
  float maxTimeOut = 1500;
  
  float timeInCurrentState = 0;
  State state = State.in;
  float offset = 0;
  
  final int MAX_OFFSET;

  protected PirhanaPlant(int x, int y, int width, int height, int maxOffset, int tileSheetX, int tileSheetY) {
    super(x, y, "pirhanaplant.png", width, height, TIME_PER_FRAME, tileSheetX,
        tileSheetY);
    
    MAX_OFFSET = maxOffset;
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
    super.update(timeDif_ms);
    
    switch(state){
      case in:  timeInCurrentState += timeDif_ms;
                if(maxTimeIn < timeInCurrentState){
                  state = State.movingOut;
                  timeInCurrentState = 0;
                }
                break;
               
      case out: timeInCurrentState += timeDif_ms;
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
 static abstract class VerticalPlant extends PirhanaPlant{
   final int maxHeight;
   
  
   protected VerticalPlant(int x, int y, int height, int tileSheetX, int tileSheetY) {
     super(x, y, 32, height, height, tileSheetX, tileSheetY);
     
     maxHeight = height;
   }
    
   @Override
   public void nextFrame() {
     offsetX = (offsetX == 0) ? (int)width : 0;
   }    
 }
  
 /**
  * Abstract class for a plant that moves up out of a pipe
  */
  public abstract static class Up extends VerticalPlant{
    final int startingY;
    
    public Up(int x, int y, int height, int tileSheetX, int tileSheetY) {
      super(x, y, height, tileSheetX, tileSheetY);
      
      startingY = y;
    }
    
    @Override
    public void update(float timeDif_ms) {
      super.update(timeDif_ms);
      y = startingY - (int)offset;
      height = (int)offset;
    }
  }
  
  /**
   *  Tall Pirhana plant (red) that moves up out of
   *  a pipe
   */
  public static class TallUp extends Up{
    public TallUp(int x, int y) {
      super(x, y + 32, 64, 0, 96);
    }
  }
  
  /**
   *  Short Pirhana plant (green) that moves up out of
   *  a pipe
   */
  public static class ShortUp extends Up{
    public ShortUp(int x, int y) {
      super(x, y + 32, 48, 0, 160);
    }
  }
  
  /**
   * Abstract class for a plant that moves down out of a pipe
   */
  public static class Down extends VerticalPlant{
    final int startingHeight;
    
    public Down(int x, int y, int height, int tileSheetX, int tileSheetY) {
      super(x, y, height, tileSheetX, tileSheetY);
      
      startingHeight = height;
    }
    
    @Override
    public void update(float timeDif_ms) {
      super.update(timeDif_ms);
      
      height = (int)offset;
      offsetY = (int)(startingHeight - (int)offset);
    }
  }
  
  /**
   *  Tall Pirhana plant (red) that moves down out of
   *  a pipe
   */
  public static class TallDown extends Down{
    public TallDown(int x, int y) {
      super(x, y, 64, 64, 96);
    }
  }
  
  /**
   *  Short Pirhana plant (green) that moves down out of
   *  a pipe
   */
  public static class ShortDown extends Down{
    public ShortDown(int x, int y) {
      super(x, y, 48, 64, 160);
    }
  }
}
