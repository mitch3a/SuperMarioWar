package smw.entity;

import smw.gfx.Sprite.Direction;
import smw.ui.PlayerControlBase;

public class PlayerPhysics {
//Jumping/Vertical
	enum SpeedsForJumping{
		SLOW(0), MEDIUM(1), FAST(2);
		
		final public int index;
		private SpeedsForJumping(int i){
			index = i;
		}
	}
	
	//Explained below
	public static final int   FRAMES_PER_SECOND = 200; //TODO mk this should be based on the timer
	public static final float FRAMES_CONVERSION = 60f;
	public static final float qC2 = 0x0800;
	public static final float qC = 1/qC2;//(FRAMES_CONVERSION/FRAMES_PER_SECOND)/qC2; //Quick converter. 
	
	//These values (actual Velocity/Acceleration) are courtesy of http://s276.photobucket.com/user/jdaster64/media-full//smb_playerphysics.png.html
	//They are represented as (one hex value per the following): 0x (Blocks) (Pixels) (Subpixels) (Subsubpixels) (Subsubsubpixels) for 1/60 seconds per frame
	
	//TODO mk these "TEMP" values should never be used... just wanted to keep the arrays below consistent for testing
	public static final float WALKING_ACCELERATION_TEMP  =  qC*0x0098;
	public static final float RUNNING_ACCELERATION_TEMP  =  qC*0x00E4;
	public static final float RELEASE_DECELERATION_TEMP  =  qC*0x00D0;
	public static final float SKIDDING_DECELERATION_TEMP =  qC*0x01A0;
	
	public static final float MIN_WALKING_VELOCITY_TEMP 		= 0x0130/qC2;
	public static final float MAX_WALKING_VELOCITY_TEMP 		= 0x1900/qC2;
	public static final float MAX_RUNNING_VELOCITY_TEMP 	  = 0X2900/qC2;
	public static final float THRESHOLD_FOR_JUMPING_SPEED   = 0X1D00/qC2; //TODO mk give this a better name... its for determining deceleration when holding back while jumping
		
	public static final float MAX_SLOW_JUMPING_SPEED   = 0x1000/qC2;
	public static final float MAX_MEDIUM_JUMPING_SPEED = 0x24FF/qC2;

	public static final float SKID_TURNAROUND_VELOCITY_TEMP =  0x0900/qC2;
	
	public static final float[] WALKING_ACCELERATION  =  { WALKING_ACCELERATION_TEMP, -WALKING_ACCELERATION_TEMP};
	public static final float[] RUNNING_ACCELERATION  =  { RUNNING_ACCELERATION_TEMP,	-RUNNING_ACCELERATION_TEMP};
	public static final float[] RELEASE_DECELERATION  =  {-RELEASE_DECELERATION_TEMP,  RELEASE_DECELERATION_TEMP};
	public static final float[] SKIDDING_DECELERATION =  {-SKIDDING_DECELERATION_TEMP, SKIDDING_DECELERATION_TEMP};
	
	public static final float[] MIN_WALKING_VELOCITY 		 =  {MIN_WALKING_VELOCITY_TEMP,			-MIN_WALKING_VELOCITY_TEMP};
	public static final float[] MAX_WALKING_VELOCITY		 =  {MAX_WALKING_VELOCITY_TEMP, 		-MAX_WALKING_VELOCITY_TEMP};
	public static final float[] MAX_RUNNING_VELOCITY 	   =  {MAX_RUNNING_VELOCITY_TEMP,     -MAX_RUNNING_VELOCITY_TEMP};
	
	public static final float[] SKID_TURNAROUND_VELOCITY 	   =  {SKID_TURNAROUND_VELOCITY_TEMP, -SKID_TURNAROUND_VELOCITY_TEMP};
	
	//AIR (not sure how to use this yet)
	public static final float AIR_ACCELERATION_FAST_TEMP  = qC*0x0098;
	public static final float AIR_ACCELERATION_SLOW_TEMP  = qC*0x00E4;
	
	public static final float AIR_DECELERATION_SLOW_TEMP   = qC*0x00E4;
	public static final float AIR_DECELERATION_MIDDLE_TEMP = qC*0x0098;
	public static final float AIR_DECELERATION_FAST_TEMP   = qC*0x0098;

	
	public static final float[] AIR_DECELERATION_SLOW		 =  {-AIR_DECELERATION_SLOW_TEMP, 	AIR_DECELERATION_SLOW_TEMP};
	public static final float[] AIR_DECELERATION_MIDDLE  =  {-AIR_DECELERATION_MIDDLE_TEMP, AIR_DECELERATION_MIDDLE_TEMP};
	public static final float[] AIR_DECELERATION_FAST		 =  {-AIR_DECELERATION_FAST_TEMP, 	AIR_DECELERATION_FAST_TEMP};
	
	public static final float[] AIR_ACCELERATION_FAST		 =  {AIR_ACCELERATION_FAST_TEMP, 		-AIR_ACCELERATION_FAST_TEMP};
	public static final float[] AIR_ACCELERATION_SLOW    =  {AIR_ACCELERATION_SLOW_TEMP,     -AIR_ACCELERATION_SLOW_TEMP};
	
	public static final float[] JUMPING_VELOCITY 			 =  {-0x4000/qC2, -0x4000/qC2, -0x5000/qC2 };
	public static final float[] JUMPING_WEAK_GRAVITY   =  {qC*0x0200, qC*0x01E0, qC*0x0280 }; //If you're not holding speed button
	public static final float[] JUMPING_STRONG_GRAVITY =  {qC*0x0700, qC*0x0600, qC*0x0900 }; //If you're not holding speed button
	
	
	long previousTime_ms;
	
	public float velocityX,     velocityY, remainderX, remainderY;
	float jumpingAccelerationX, accelerationY;
	
	PlayerControlBase playerControl;
	boolean isJumping; 
	boolean isSkidding;
	
	//TODO mk I don't like using the direction as an index... i think its slow, but I'm doing this
	// for now becuase it's easier to read and speed might not be an issue
	Direction currentVelocityDirection;
	
	public PlayerPhysics(PlayerControlBase playerControl){
		previousTime_ms = 0;
		velocityX = 0;
		velocityY = 0;
		remainderX = 0;
		remainderY = 0;
		jumpingAccelerationX = 0;
		accelerationY = JUMPING_STRONG_GRAVITY[SpeedsForJumping.SLOW.index];
		isJumping = false;
		isSkidding = false;
		currentVelocityDirection = Direction.RIGHT;
		this.playerControl = playerControl;
	}
	
	//////////////////////////////////////////////////////
	//PRIVATE METHODS
	int getSpeedIndex(){
		float speed = Math.abs(velocityX);
		
		if(speed < MAX_SLOW_JUMPING_SPEED){
			return SpeedsForJumping.SLOW.index;
		}
		
		if(speed < MAX_MEDIUM_JUMPING_SPEED){
			return SpeedsForJumping.MEDIUM.index;
		}
		
		return SpeedsForJumping.FAST.index;
	}
	
	boolean movingInDirection(Direction direction){
		return (direction == Direction.RIGHT) ? (velocityX > 0) : (velocityX < 0);
	}
	
	boolean velocityDeceleratedTooFar(){
		return (currentVelocityDirection == Direction.RIGHT) ? (velocityX < 0) : (velocityX > 0);
	}
	
	/** This method assumes the threshold and velocity have the same sign **/
	boolean speedLessThan(float threshold){
		return (currentVelocityDirection == Direction.RIGHT) ? (velocityX < threshold) : (velocityX > threshold);
	}
	
	boolean velocityAcceleratedTooFar(float threshold){
		return (currentVelocityDirection == Direction.RIGHT) ? (velocityX > threshold) : (velocityX < threshold);
	}
	
	void passivelySlowDownX(float timeDif){
		if(velocityX == 0){
			return;
		}
		
		if(isSkidding){
			velocityX += SKIDDING_DECELERATION[currentVelocityDirection.index]*timeDif;
	  }
	  else {
	  	velocityX += RELEASE_DECELERATION[currentVelocityDirection.index]*timeDif;
	  }
		
		if(velocityDeceleratedTooFar()){
			velocityX = 0;
			isSkidding = false;
		}
	}
	
	/* This method will accelerate in the current direction */
	void moveWithX(float timeDif){
		isSkidding = false;
		float maxVelocity;
		
    if(playerControl.isRunning()){
    	velocityX  += RUNNING_ACCELERATION[currentVelocityDirection.index]*timeDif;
    	maxVelocity = MAX_RUNNING_VELOCITY[currentVelocityDirection.index];
    }
    else{
    	velocityX  += WALKING_ACCELERATION[currentVelocityDirection.index]*timeDif;
    	maxVelocity = MAX_WALKING_VELOCITY[currentVelocityDirection.index];
    }
    
    if(velocityAcceleratedTooFar(maxVelocity)){
			velocityX = maxVelocity;
		}
	}
	
	/** this method assumes the newDirection != currentVelocityDirection **/
	void moveAgainstX(float timeDif, Direction newDirection){
		//This part is a little confusing. Details from source listed above. Basically,
		//if you're moving slow enough, you can instantly turn around, else you skid
		if(speedLessThan(SKID_TURNAROUND_VELOCITY[currentVelocityDirection.index])){
			velocityX = MIN_WALKING_VELOCITY[newDirection.index];
			currentVelocityDirection = newDirection;
		}
		else{
      velocityX += SKIDDING_DECELERATION[currentVelocityDirection.index]*timeDif;
      isSkidding = true;
    }  
	}
	
	void updateX(float timeDif) {
	  timeDif = 1.0f;
	  // Until something outside is added (wind?), velocityX is constant while in the air
	  if(isJumping){
	  	return;
	  }
	  int direction = playerControl.getDirection();

	  if( direction == 0){
	  	passivelySlowDownX(timeDif);
	  }
    else{
    	Direction newDirection = (direction > 0) ? Direction.RIGHT : Direction.LEFT;
    	if(newDirection == currentVelocityDirection){
  			moveWithX(timeDif);
  		}
  		else{
  			moveAgainstX(timeDif, newDirection);  
  		}
    }
  }
	
	//TODO mk close but not quite there?
	void setJumpingAccelerationX(){
		int direction = playerControl.getDirection();
		if( direction == 0){
			if(isSkidding){
				jumpingAccelerationX = SKIDDING_DECELERATION[currentVelocityDirection.index];
		  }
		  else {
		  	jumpingAccelerationX = RELEASE_DECELERATION[currentVelocityDirection.index];
		  }
		}
		else{
			boolean holdingForward = (direction > 0) ? (velocityX > 0) : (velocityX < 0);
			float speed = Math.abs(velocityX);
			
			if(holdingForward){
				if(speed < MAX_WALKING_VELOCITY_TEMP ){
					jumpingAccelerationX = AIR_ACCELERATION_SLOW[currentVelocityDirection.index];
				}
				else{
					jumpingAccelerationX = AIR_ACCELERATION_FAST[currentVelocityDirection.index];
				}
			}
			else { //Holding Backwards
				if(speed < MAX_WALKING_VELOCITY_TEMP ){
					jumpingAccelerationX = AIR_DECELERATION_SLOW[currentVelocityDirection.index];
				}
				else if(speed < THRESHOLD_FOR_JUMPING_SPEED ){
					jumpingAccelerationX = AIR_DECELERATION_MIDDLE[currentVelocityDirection.index];
				}
				else{
					jumpingAccelerationX = AIR_DECELERATION_FAST[currentVelocityDirection.index];
				}
			}
		}
	}
  
	//TODO mk this is CLOSE but not quite there
  void updateY(float timeDif_ms){
    timeDif_ms = 1.0f;
    if(!isJumping && playerControl.isJumping()){
    	int speedIndex = getSpeedIndex();
    	velocityY = JUMPING_VELOCITY[speedIndex];
    	setJumpingAccelerationX();
    	isJumping = true;
    }
    
    if(isJumping){
    	int speedIndex = getSpeedIndex();
    	if(playerControl.isJumping()){
    		accelerationY = JUMPING_WEAK_GRAVITY[speedIndex];
    	}
    	else{
    		accelerationY = JUMPING_STRONG_GRAVITY[speedIndex];
    	}
    }
    
    velocityY = velocityY + (accelerationY*timeDif_ms);
  }
  
  public void collideWithFloor(){
  	jumpingAccelerationX = 0;
    velocityY = 0;
    isJumping = false;
  }
  
  public void collideWithWall() {
    this.velocityX = 0;
    this.isSkidding = false;
  }

  public void poll(){
  	playerControl.poll();
  }
  
	public void update(){		
		long currentTime_ms = System.currentTimeMillis();
		float timeDif = (float) ((currentTime_ms - previousTime_ms)/1000.0);
		
		//For first "move" call, it's safe to assume we're not moving
		if(previousTime_ms != 0){
		  //if (canX)
		    updateX(timeDif);
		  //if (canY)
		    updateY(timeDif);
		}
				
		previousTime_ms = currentTime_ms;
	}

  public float getVelocityX() {
		return velocityX;
	}
	
	public float getVelocityY() {
		return velocityY;
	}
}
