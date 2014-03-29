package smw.world.hazards;

import smw.ui.screen.GameFrame;

public class MovingFireBall extends FireBall{
  final float VELOCITY_X;
  final float VELOCITY_Y;

  public MovingFireBall(int x, int y, float velocity, float angleRadians) {
    super(x, y);
    
    VELOCITY_X = velocity*((float)Math.cos(angleRadians));
    VELOCITY_Y = -velocity*((float)Math.sin(angleRadians));    
  }
  
  @Override
  public void update(float timeDif_ms) {
    super.update(timeDif_ms);
    
    x += VELOCITY_X*timeDif_ms;
    y += VELOCITY_Y*timeDif_ms;
    
    //TODO be smarter about wrapping
    if(x + width < 0){
      x += width + GameFrame.res_width;
    }
    else if( x > GameFrame.res_width){
      x -= GameFrame.res_width + width;
    }
  }
  
  //Let X wrap
  @Override
  public boolean shouldBeRemoved() {
    return y > GameFrame.res_height || y + height < 0;
  }
}
