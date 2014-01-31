package smw.level.MovingPlatform;

import smw.level.Path;

public class StraightContinuousPath implements Path{
  
  static final int X = 0;
  static final int Y = 1;
  
  float[] currentPos = new float[2];
  float[] velocity = new float[2];
  
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
  
  public void move(float timeDif){
    move(X, timeDif);
    move(Y, timeDif);
  }
  
  public int getX(){
    return get(X);
  }
  
  public int getY(){
    return get(Y);
  }
  
  int get(int i){
    return (int) currentPos[i];
  }
  
  void move(int i, float timeDif){
    currentPos[i] += velocity[i]*timeDif;
    
    //TODO mk better way to do the wrapping....
    int max = (i == X) ? 640 : 480; //TODO mk at LEAST use the constants
    
    if(currentPos[i] < 0){
      currentPos[i] += max;
    }
    else{
      currentPos[i] = currentPos[i] % max;
    }
  }
}
