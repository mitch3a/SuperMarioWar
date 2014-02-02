package smw.world.MovingPlatform;


public class StraightSegmentPath extends Path{
  
  float[] startPos   = new float[2];
  float[] endPos     = new float[2];
  float[] velocity   = new float[2];

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
  
  void move(int axis, float timeDif){
    
    currentPos[axis] += velocity[axis]*timeDif;
    
    //////////////////////////////////////////////
    //Make sure we didn't go beyond the start/end
    if(currentPos[axis]> endPos[axis]){
      //////////////////////////////////////////////////
      //we really want (end - how far we went beyond end) 
      //             = (end - (current - end) 
      //             = 2*end - current
      currentPos[axis]  = 2*endPos[axis] - currentPos[axis];
      velocity[axis] = (-1)*velocity[axis];
    }
    else if(currentPos[axis] < startPos[axis]){
      //////////////////////////////////////////////////
      //we really want (start + how far we went beyond start) 
      //             = (start + (start - current) 
      //             = 2*end - current
      currentPos[axis]  = 2*startPos[axis] - currentPos[axis];
      velocity[axis] = (-1)*velocity[axis];
    }
  }
}
