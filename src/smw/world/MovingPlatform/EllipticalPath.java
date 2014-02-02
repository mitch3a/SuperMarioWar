package smw.world.MovingPlatform;

public class EllipticalPath extends Path{ 

  /*
   * This constructor expects the centerX/Y to be relative to the top left
   * of the moving platform and NOT the center of the moving platform
   */
  float radiusX, radiusY, centerX, centerY, angle, velocity;
  
  public EllipticalPath(float velocity, float angle, float radiusX, float radiusY, float centerX, float centerY){
    this.radiusX  = radiusX;
    this.radiusY  = radiusY;
    this.centerX  = centerX;
    this.centerY  = centerY;
    this.angle    = angle;
    this.velocity = velocity;
    
    setPosition();
  }

  @Override
  void move(int axis, float timeDif){
    angle += velocity*timeDif;
    setPosition();
  }
  
  void setPosition(){
    currentPos[X] = (float) (radiusX*Math.cos(angle) + centerX);
    currentPos[Y] = (float) (radiusY*Math.sin(angle) + centerY);
  }
}
