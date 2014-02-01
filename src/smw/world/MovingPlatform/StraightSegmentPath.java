package smw.world.MovingPlatform;


public class StraightSegmentPath implements Path{
  
  static final int X = 0;
  static final int Y = 1;
  
  float[] currentPos = new float[2];
  float[] velocity   = new float[2];
  
  float[] startPos = new float[2];
  float[] endPos   = new float[2];

  public StraightSegmentPath(float velocity, float startX, float startY, float endX, float endY){
    this.currentPos[X] = startX;
    this.currentPos[Y] = startY;
    
    //Calculate velocities for X/Y
    float xLength = endX - startX;
    float yLength = endY - startY;
    
    double angle =(xLength != 0) ? Math.tan(((float)yLength)/xLength) : (float) (Math.PI/2);
    
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
    
    //Make the "start" the "lesser" one
    if(startX < endX){
      this.startPos[X] = startX;
      this.endPos[X]   = endX;
    }
    else{
      this.velocity[X] = (-1)*this.velocity[X];
      this.startPos[X] = endX;
      this.endPos[X]   = startX;
    }

    //Make the "start" the "lesser" one
    if(startY < endY){
      this.startPos[Y] = startY;
      this.endPos[Y]   = endY;
    }
    else{
      this.velocity[Y] = (-1)*this.velocity[Y];
      this.startPos[Y] = endY;
      this.endPos[Y]   = startY;
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
    
    //////////////////////////////////////////////
    //Make sure we didn't go beyond the start/end
    if(currentPos[i]> endPos[i]){
      //////////////////////////////////////////////////
      //we really want (end - how far we went beyond end) 
      //             = (end - (current - end) 
      //             = 2*end - current
      currentPos[i]  = 2*endPos[i] - currentPos[i];
      velocity[i] = (-1)*velocity[i];
    }
    else if(currentPos[i] < startPos[i]){
      //////////////////////////////////////////////////
      //we really want (start + how far we went beyond start) 
      //             = (start + (start - current) 
      //             = 2*end - current
      currentPos[i]  = 2*startPos[i] - currentPos[i];
      velocity[i] = (-1)*velocity[i];
    }
  }
}
