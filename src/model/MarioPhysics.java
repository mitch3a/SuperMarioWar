package model;

import model.Sprite.Direction;

public class MarioPhysics {
	
	//Jumping/Vertical
	enum SpeedsForJumping{
		SLOW(0), MEDIUM(1), FAST(2);
		
		final public int index;
		private SpeedsForJumping(int i){
			index = i;
		}
	}
	
	//Explained below
	public static final int   FRAMES_PER_SECOND = 200; //TODO this should be based on the timer
	public static final float FRAMES_CONVERSION = 60f;
	public static final float qC2 = 0x0500;
	public static final float qC = 1/qC2;//(FRAMES_CONVERSION/FRAMES_PER_SECOND)/qC2; //Quick converter. 
	
	//These values (actual Velocity/Acceleration) are courtesy of http://s276.photobucket.com/user/jdaster64/media-full//smb_playerphysics.png.html
	//They are represented as (one hex value per the following): 0x (Blocks) (Pixels) (Subpixels) (Subsubpixels) (Subsubsubpixels) for 1/60 seconds per frame
	
	//These TEMP values are just for readability. Should not be used outside of static declaration
	/*
	 * public static final int WALKING_ACCELERATION_TEMP  = (int)(qC*0x0098);
	public static final int RUNNING_ACCELERATION_TEMP  = (int)(qC*0x00E4);
	public static final int RELEASE_DECELERATION_TEMP  = (int)(qC*0x00D0);
	public static final int SKIDDING_DECELERATION_TEMP = (int)(qC*0x01A0);
	
	public static final int MIN_WALKING_VELOCITY_TEMP 		    = (int)(qC*0x0130);
	public static final int MAX_WALKING_VELOCITY_TEMP 		    = (int)(qC*0x1900);
	public static final int MAX_RUNNING_VELOCITY_TEMP 	  = (int)(qC*0X2900);
	public static final int SKID_TURNAROUND_VELOCITY_TEMP = (int)(qC*0x0900);
	
	public static final int MAX_SLOW_JUMPING_SPEED   = (int)(qC*0x1000);
	public static final int MAX_MEDIUM_JUMPING_SPEED = (int)(qC*0x24FF);
	
	public static final int[] WALKING_ACCELERATION  =  { WALKING_ACCELERATION_TEMP, -WALKING_ACCELERATION_TEMP};
	public static final int[] RUNNING_ACCELERATION  =  { RUNNING_ACCELERATION_TEMP,	-RUNNING_ACCELERATION_TEMP};
	public static final int[] RELEASE_DECELERATION  =  {-RELEASE_DECELERATION_TEMP,  RELEASE_DECELERATION_TEMP};
	public static final int[] SKIDDING_DECELERATION =  {-SKIDDING_DECELERATION_TEMP, SKIDDING_DECELERATION_TEMP};
	
	public static final int[] MIN_WALKING_VELOCITY 		 =  {MIN_WALKING_VELOCITY_TEMP,			-MIN_WALKING_VELOCITY_TEMP};
	public static final int[] MAX_WALKING_VELOCITY		 =  {MAX_WALKING_VELOCITY_TEMP, 		-MAX_WALKING_VELOCITY_TEMP};
	public static final int[] MAX_RUNNING_VELOCITY 	   =  {MAX_RUNNING_VELOCITY_TEMP,     -MAX_RUNNING_VELOCITY_TEMP};
	public static final int[] SKID_TURNAROUND_VELOCITY =  {SKID_TURNAROUND_VELOCITY_TEMP, -SKID_TURNAROUND_VELOCITY_TEMP};
	
	//AIR (not sure how to use this yet)
	//public static final short AIR_ACCELERATION_FAST = 0x00098; //If moving faster than
	//public static final short AIR_ACCELERATION_FAST = 0x00098;
	
	public static final int[] JUMPING_VELOCITY 			 =  {(int)(-qC*0x4000), (int)(-qC*0x4000), (int)(-qC*0x5000) };
	public static final int[] JUMPING_WEAK_GRAVITY   =  {(int) (qC*0x0200), (int) (qC*0x01E0), (int) (qC*0x0280) }; //If you're not holding speed button
	public static final int[] JUMPING_STRONG_GRAVITY =  {(int) (qC*0x0700), (int) (qC*0x0600), (int) (qC*0x0900) }; //If you're not holding speed button
	 */
	public static final float WALKING_ACCELERATION_TEMP  = qC*0x0098;
	public static final float RUNNING_ACCELERATION_TEMP  = qC*0x00E4;
	public static final float RELEASE_DECELERATION_TEMP  = qC*0x00D0;
	public static final float SKIDDING_DECELERATION_TEMP = qC*0x01A0;
	
	public static final float MIN_WALKING_VELOCITY_TEMP 		= 0x0130/qC2;
	public static final float MAX_WALKING_VELOCITY_TEMP 		= 0x1900/qC2;
	public static final float MAX_RUNNING_VELOCITY_TEMP 	  = 0X2900/qC2;
	
	public static final float MAX_SLOW_JUMPING_SPEED   = 0x1000/qC2;
	public static final float MAX_MEDIUM_JUMPING_SPEED = 0x24FF/qC2;
	public static final float SKID_TURNAROUND_SPEED    =  0x0900/qC2;
	
	public static final float[] WALKING_ACCELERATION  =  { WALKING_ACCELERATION_TEMP, -WALKING_ACCELERATION_TEMP};
	public static final float[] RUNNING_ACCELERATION  =  { RUNNING_ACCELERATION_TEMP,	-RUNNING_ACCELERATION_TEMP};
	public static final float[] RELEASE_DECELERATION  =  {-RELEASE_DECELERATION_TEMP,  RELEASE_DECELERATION_TEMP};
	public static final float[] SKIDDING_DECELERATION =  {-SKIDDING_DECELERATION_TEMP, SKIDDING_DECELERATION_TEMP};
	
	public static final float[] MIN_WALKING_VELOCITY 		 =  {MIN_WALKING_VELOCITY_TEMP,			-MIN_WALKING_VELOCITY_TEMP};
	public static final float[] MAX_WALKING_VELOCITY		 =  {MAX_WALKING_VELOCITY_TEMP, 		-MAX_WALKING_VELOCITY_TEMP};
	public static final float[] MAX_RUNNING_VELOCITY 	   =  {MAX_RUNNING_VELOCITY_TEMP,     -MAX_RUNNING_VELOCITY_TEMP};
	
	//AIR (not sure how to use this yet)
	/*
	public static final float AIR_ACCELERATION_FAST  = qC*0x0098;
	public static final float AIR_ACCELERATION_SLOW  = qC*0x00E4;
	public static final float MAX_AIR_SLOW_SPEED_TEMP = 0x1900/qC2;
	public static final float MAX_AIR_FAST_SPEED_TEMP = 0x2900/qC2;
	
	public static final float[] MAX_AIR_SLOW_SPEED		 =  {MAX_AIR_SLOW_SPEED_TEMP, 		-MAX_AIR_SLOW_SPEED_TEMP};
	public static final float[] MAX_AIR_FAST_SPEED     =  {MAX_AIR_FAST_SPEED_TEMP,     -MAX_AIR_FAST_SPEED_TEMP};
	*/
	
	public static final float[] JUMPING_VELOCITY 			 =  {-0x4000/qC2, -0x4000/qC2, -0x5000/qC2 };
	public static final float[] JUMPING_WEAK_GRAVITY   =  {qC*0x0200, qC*0x01E0, qC*0x0280 }; //If you're not holding speed button
	public static final float[] JUMPING_STRONG_GRAVITY =  {qC*0x0700, qC*0x0600, qC*0x0900 }; //If you're not holding speed button
	
	
	long previousTime_ms;
	
	float velocityX,     velocityY, remainderX, remainderY;
	float accelerationX, accelerationY;
	
	boolean activelyMovingX;
	boolean runButtonOn;
	boolean jumpSpeedSlow;
	Direction direction;
	
	public MarioPhysics(){
		previousTime_ms = 0;
		velocityX = 0;
		velocityY = 0;
		remainderX = 0;
		remainderY = 0;
		accelerationX = 0;
		accelerationY = JUMPING_STRONG_GRAVITY[SpeedsForJumping.SLOW.index];
		activelyMovingX = false;
		runButtonOn = true;
		direction = Direction.RIGHT;
		jumpSpeedSlow = false;
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

	void updateVelocityX(float timeDif_ms){
	  //TODO
		timeDif_ms = 1.0f;
		velocityX += accelerationX*timeDif_ms;
		
		System.out.println("Acceleration: " + accelerationX);
		
		if(activelyMovingX){
			if(runButtonOn) {
				velocityX = (direction == Direction.RIGHT) ? Math.min(MAX_RUNNING_VELOCITY[Direction.RIGHT.index], velocityX) : 
					                                           Math.max(MAX_RUNNING_VELOCITY[Direction.LEFT.index ], velocityX);
			}
			else{
				velocityX = (direction == Direction.RIGHT) ? Math.min(MAX_WALKING_VELOCITY[Direction.RIGHT.index], velocityX) : 
          																					 Math.max(MAX_WALKING_VELOCITY[Direction.LEFT.index ], velocityX);
			}
		}
		else{
			velocityX = (direction == Direction.RIGHT) ? Math.max(velocityX, 0) : Math.min(velocityX, 0);
		}

		//Don't want to move passed zero if we stopped trying to move
		if(!activelyMovingX){
			if(direction == Direction.RIGHT){
				velocityX = Math.max(0, velocityX);
			}
			else{
				velocityX = Math.min(0, velocityX);
			}
		}
	}
	
	void updateVelocityY(float timeDif_ms){
		//TODO
		timeDif_ms = 1.0f;
		velocityY = velocityY + (accelerationY*timeDif_ms);
	}
	
	//////////////////////////////////////////////////////
	//PUBLIC METHODS
	
	public boolean canJump(){
		return (accelerationY == 0);
	}
	
	public void performJump(){
		int speedIndex = getSpeedIndex();
		accelerationY = (runButtonOn) ? JUMPING_WEAK_GRAVITY[speedIndex] : JUMPING_STRONG_GRAVITY[speedIndex];
		velocityY = JUMPING_VELOCITY[speedIndex];
	}
	
	public void collideWithFloor(){
		accelerationY = 0;
		velocityY = 0;
	}
	
	public void freeFall(){
		int speedIndex = getSpeedIndex();
		accelerationY = (runButtonOn) ? JUMPING_WEAK_GRAVITY[speedIndex] : JUMPING_STRONG_GRAVITY[speedIndex];
	}
	
	public void moveRight(){
		if(!activelyMovingX && velocityX == 0){
			velocityX = MIN_WALKING_VELOCITY[Direction.RIGHT.index];
		}
		else if(velocityX < 0){
			
		}
		
		activelyMovingX = true;
		direction = Direction.RIGHT;
		accelerationX = (runButtonOn) ? RUNNING_ACCELERATION[direction.index] : WALKING_ACCELERATION[direction.index];
	}
	
	public void moveLeft(){
		if(activelyMovingX && velocityX == 0){
			velocityX = MIN_WALKING_VELOCITY[Direction.LEFT.index];
		}
		
		activelyMovingX = true;
		direction = Direction.LEFT;
		accelerationX = (runButtonOn) ? RUNNING_ACCELERATION[direction.index] : WALKING_ACCELERATION[direction.index];
	}
	
	public void stopMoving(){
		activelyMovingX = false;
		accelerationX = RELEASE_DECELERATION[direction.index];
	}
	
	public void runButtonChanged(boolean on){
		runButtonOn = on;
	}
	
	
	public void update(){
		long currentTime_ms = System.currentTimeMillis();
		float timeDif = (float) ((currentTime_ms - previousTime_ms)/1000.0);
		
		//For first "move" call, it's safe to assume we're not moving
		if(previousTime_ms != 0){
			updateVelocityX(timeDif);
			updateVelocityY(timeDif);
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

