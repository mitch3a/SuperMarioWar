package smw.world.MovingPlatform;

public abstract class SpiralPath extends Path{ 

  /*
   * This constructor expects the centerX/Y to be relative to the top left
   * of the moving platform and NOT the center of the moving platform
   */
  float radius, totalAngle, centerX, centerY, angle, velocity, ratio, startingAngle;
  
  //TODO mk Currently the velocity is angle speed. This means it slows down as it gets closer.
  //     might want that functionality as well
  public SpiralPath(float velocity, float angle, float numRotations, float radius, float centerX, float centerY){
    this.radius  = radius;
    this.totalAngle = (float) (numRotations*2*Math.PI);
    this.centerX  = centerX;
    this.centerY  = centerY;
    this.angle    = angle;
    this.startingAngle = angle;
    this.velocity = velocity/2.0f;
    this.ratio = 1;
    
    setPosition();
  }

  @Override
  void move(int axis, float timeDif){
    ratio = 1 - ((angle - startingAngle)/totalAngle);
    updateAngle(timeDif);
    setPosition();
  }
  
  abstract void updateAngle(float timeDif);
  
  void setPosition(){
    currentPos[X] = (float) (radius*ratio*Math.cos(angle) + centerX);
    currentPos[Y] = (float) (radius*ratio*Math.sin(angle) + centerY);
  }
  
  public static class AngularVelocity extends SpiralPath{
   
    public AngularVelocity(float velocity, float angle, float numRotations, float radius, float centerX, float centerY) {
      super(velocity, angle, numRotations, radius, centerX, centerY);
    }
   
    void updateAngle(float timeDif){
      angle += velocity*timeDif;
    }
  }
  
  public static class RegularVelocity extends SpiralPath{
    
    final float circumference;
    
    public RegularVelocity(float velocity, float angle, float numRotations, float radius, float centerX, float centerY) {
      super(velocity, angle, numRotations, radius, centerX, centerY);
      
      circumference = (float) (2*Math.PI*radius);
    }
   
    void updateAngle(float timeDif){
      angle += (velocity*timeDif/(circumference*ratio));
    }
  }
}
