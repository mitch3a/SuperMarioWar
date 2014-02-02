package smw.world.MovingPlatform;


public class StraightContinuousPath extends Path{
  
  float[] velocity   = new float[2];
  
  public StraightContinuousPath(float velocity, float startX, float startY, float angle){
    
    this.currentPos[X] = startX;
    this.currentPos[Y] = startY;

    if(angle == (float) (Math.PI/2)){
      this.velocity[X] = 0;
      this.velocity[Y] = velocity;
    }
    else if(angle == (float) (3*Math.PI/2)){
      this.velocity[X] = 0;
      this.velocity[Y] = (-1)*velocity;
    }
    else if(angle == (float) 0){
      this.velocity[X] = velocity;
      this.velocity[Y] = 0;
    }
    else if(angle == (float) (Math.PI)){
      this.velocity[X] = (-1)*velocity;
      this.velocity[Y] = 0;
    }
    else{
      this.velocity[X] = (float) (velocity*Math.acos(angle));
      this.velocity[Y] = (float) (velocity*Math.asin(angle));
    }
  }
  
  void move(int axis, float timeDif){
    currentPos[axis] += velocity[axis]*timeDif;
    
    //TODO mk better way to do the wrapping....
    int max = (axis == X) ? 640 : 480; //TODO mk at LEAST use the constants
    
    if(currentPos[axis] < 0){
      currentPos[axis] += max;
    }
    else{
      currentPos[axis] = currentPos[axis] % max;
    }
  }
}
