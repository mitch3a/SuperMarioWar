package smw.world.MovingPlatform;

public abstract class SpiralPath extends Path{ 

  /*
   * This constructor expects the centerX/Y to be relative to the top left
   * of the moving platform and NOT the center of the moving platform
   */
  float radius, totalAngle, centerX, centerY, angle, velocity, ratio, startingAngle;
  public float offsetX;
  public float offsetY;
  public boolean isDone = false;
  
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
    
    if(ratio < 0){
      isDone = true;
    }
    
    updateAngle(timeDif);
    setPosition();
  }
  
  abstract void updateAngle(float timeDif);
  
  void setPosition(){
    offsetX = (float)(radius*ratio*Math.cos(angle));
    offsetY = (float)(radius*ratio*Math.sin(angle));
    
    currentPos[X] = offsetX + centerX;
    currentPos[Y] = offsetY + centerY;
  }
  
  //TODO this is a little hacky because it inherits the getX, getY.
  //     made offsets public and stored for spawn animation to transpose
  //     the one that starts at angle 0
  public int getX90(){
    return (int)(centerX + offsetY);
  }
  
  public int getY90(){
    return (int)(centerY - offsetX);
  }
  
  public int getX180(){
    return (int)(centerX - offsetX);
  }
  
  public int getY180(){
    return (int)(centerY - offsetY);
  }
  
  public int getX270(){
    return (int)(centerX - offsetY);
  }
  
  public int getY270(){
    return (int)(centerY + offsetX);
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
