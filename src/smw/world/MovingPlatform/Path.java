package smw.world.MovingPlatform;

public abstract class Path{
  
  static final int X = 0;
  static final int Y = 1;
  
  protected float[] currentPos = new float[2];
  
  abstract void move(int axis, float timeDif);
  
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
}
