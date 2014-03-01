package smw.world.MovingPlatform;

public abstract class Path{
  
  static final int X = 0;
  static final int Y = 1;
  
  protected float[] currentPos = new float[2];
  protected float[] prev = new float[2];
  
  abstract void move(int axis, float timeDif);
  
  public void move(float timeDif){
    prev[X] = getX();
    prev[Y] = getY();
    
    move(X, timeDif);
    move(Y, timeDif);
  }
  
  public float getX(){
    return get(X);
  }
  
  public float getY(){
    return get(Y);
  }

  public float getXChange(){
    return currentPos[X] - prev[X];
  }
  
  public float getYChange(){
    if(currentPos[Y] - prev[Y] > 100){
      int todo = 0;
    }
    return currentPos[Y] - prev[Y];
  }
  
  float get(int i){
    return currentPos[i];
  }
}
