package smw.world.MovingPlatform;

public abstract class Path{
  
  static final int X = 0;
  static final int Y = 1;
  
  protected float[] currentPos = new float[2];
  protected int[] prev = new int[2];
  
  abstract void move(int axis, float timeDif);
  
  public void move(float timeDif){
    prev[X] = getX();
    prev[Y] = getY();
    
    move(X, timeDif);
    move(Y, timeDif);
  }
  
  public int getX(){
    return get(X);
  }
  
  public int getY(){
    return get(Y);
  }

  public int getXChange(){
    return getX() - prev[X];
  }
  
  public int getYChange(){
    return getY() - prev[Y];
  }
  
  int get(int i){
    return (int) currentPos[i];
  }
}
