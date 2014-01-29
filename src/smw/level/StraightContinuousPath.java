class StraightSegmentPath{
  
  static final int X = 0;
  static final int Y = 1;
  
  float[] currentPos = new float[2];
  float[] velocity = new float[2];
  
  public StraightSegmentPath(float velocity, int startX, int startY, int endX, int endY){
    this.currentPos[X] = (float) startX;
    this.currentPos[Y] = (float) startY;
    
    //Calculate velocities for X/Y
    int xLength = endX - startX;
    int yLength = endY - startY;
    
    float velocityLength = Math.sqrt(xLength*xLength - (yLength*yLength));
    double angle = Math.tan(((float)yLength)/xLength);
    
    velocity[X] = velocityLength*Math.acos(angle);
    velocity[Y] = velocityLength*Math.asin(angle);
  }
  
  public void move(long timeDif){
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
    return (int) current[i];
  }
  
  void move(int i, timeDif){
    current[i] += velocity[i]*timeDif;
    
    //TODO mk better way to do the wrapping....
    int max = (i == X) ? 640 : 480; //TODO mk at LEAST use the constants
    
    if(current[i] < 0){
      current[i] += max;
    }
    else{
      current[i] = current[i] % max;
    }
  }
}
