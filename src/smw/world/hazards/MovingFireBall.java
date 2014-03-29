package smw.world.hazards;

import smw.Game;
import smw.ui.screen.GameFrame;

public class MovingFireBall extends FireBall{
  final float VELOCITY_X;
  final float VELOCITY_Y;
  
  boolean partlyOff = false;

  public MovingFireBall(int x, int y, float velocity, float angleRadians) {
    super(x, y);
    
    VELOCITY_X = velocity*((float)Math.cos(angleRadians));
    VELOCITY_Y = -velocity*((float)Math.sin(angleRadians));    
  }
  
  /**
   * Use this method to make a copy. Convenient for wrapping 
   */
  public MovingFireBall(MovingFireBall previous, int newX) {
    super(newX, (int)previous.y);
    
    VELOCITY_X = previous.VELOCITY_X;
    VELOCITY_Y = previous.VELOCITY_Y;    
  }
  
  @Override
  public void update(float timeDif_ms) {
    super.update(timeDif_ms);
    
    x += VELOCITY_X*timeDif_ms;
    y += VELOCITY_Y*timeDif_ms;
    
    if(VELOCITY_X > 0){
      //Moving right. If moving off the screen, add another on the other side and this one will self-remove
      if(!partlyOff && x + width > GameFrame.res_width){
        Game.world.queueAnimatedHazard(new MovingFireBall(this, (int)(x - GameFrame.res_width)), 3);
        partlyOff = true;
      }
    }
    else{
      //Moving left. If moving off the screen, add another on the other side and this one will self-remove
      if(!partlyOff && x < 0){
        Game.world.queueAnimatedHazard(new MovingFireBall(this, (int)(x + GameFrame.res_width)), 3);
        partlyOff = true;
      }
    }
  }
  
  //Let X wrap
  @Override
  public boolean shouldBeRemoved() {
    //Don't need the partly off but prolly quicker overall
    return partlyOff && (x > GameFrame.res_width || x + width < 0 || y > GameFrame.res_height || y + height < 0);
  }
}
