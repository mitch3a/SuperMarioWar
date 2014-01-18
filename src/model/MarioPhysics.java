package model;

import ui.PlayerControl;
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
	
	PlayerControl playerControl;
	boolean isJumping; 
	boolean isSkidding;
	
	public MarioPhysics(PlayerControl playerControl){
		previousTime_ms = 0;
		velocityX = 0;
		velocityY = 0;
		remainderX = 0;
		remainderY = 0;
		accelerationX = 0;
		accelerationY = JUMPING_STRONG_GRAVITY[SpeedsForJumping.SLOW.index];
		isJumping = false;
		isSkidding = false;
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
	

  void updateX(float timeDif) {
    timeDif = 1.0f;
    int direction = playerControl.getDirection();
    
    if(velocityX < -MAX_RUNNING_VELOCITY_TEMP){
      int i = 0;
      int a = 2*i;
    }
    
    if(velocityX > MAX_RUNNING_VELOCITY_TEMP){
      int i = 0;
      int a = 2*i;
    }
    
    if( direction == 0){
      if(isSkidding){
        if(velocityX > 0){
          velocityX -= SKIDDING_DECELERATION_TEMP*timeDif;
          if(velocityX < 0){
            velocityX = 0;
            isSkidding = false;
          }
        }
        else{
          velocityX += SKIDDING_DECELERATION_TEMP*timeDif;
          if(velocityX < 0){
            velocityX = 0;
            isSkidding = false;
          }
        }
      }
      //Player no longer moving so decelerate (but don't go passed 0!)
      else if(velocityX > 0){
        velocityX = Math.max(0, velocityX - (RELEASE_DECELERATION_TEMP*timeDif));
      }
      else if(velocityX < 0){
        velocityX = Math.min(0, velocityX + (RELEASE_DECELERATION_TEMP*timeDif));
      }
    }
    else if(direction > 0){
      //Player wants to move right
      if(velocityX > 0){
        isSkidding = false;
        //And currently moving right
        if(playerControl.isRunning()){
          velocityX = Math.min(MAX_RUNNING_VELOCITY_TEMP, velocityX + (RUNNING_ACCELERATION_TEMP*timeDif));
        }
        else{
          velocityX = Math.min(MAX_WALKING_VELOCITY_TEMP, velocityX + (WALKING_ACCELERATION_TEMP*timeDif));
        }
      }
      else if(velocityX < 0){
        //And is currently moving left
        if(velocityX > -SKID_TURNAROUND_SPEED){
          velocityX = MIN_WALKING_VELOCITY_TEMP;
        }
        else{
          velocityX += SKIDDING_DECELERATION_TEMP*timeDif;
          isSkidding = true;
        }        
      }
      else{
        //And is currently not moving
        velocityX = MIN_WALKING_VELOCITY_TEMP;
      }
    }
    else{
      //Player wants to move left
      if(velocityX < 0){
        //And currently moving left
        isSkidding = false;
        if(playerControl.isRunning()){
          velocityX = Math.max(-MAX_RUNNING_VELOCITY_TEMP, velocityX - (RUNNING_ACCELERATION_TEMP*timeDif));
        }
        else{
          velocityX = Math.max(-MAX_WALKING_VELOCITY_TEMP, velocityX - (WALKING_ACCELERATION_TEMP*timeDif));
        }
      }
      else if(velocityX > 0){
        //And is currently moving right
        if(velocityX < SKID_TURNAROUND_SPEED){
          velocityX = -MIN_WALKING_VELOCITY_TEMP;
        }
        else{
          velocityX -= SKIDDING_DECELERATION_TEMP*timeDif;
          isSkidding = true;
        }        
      }
      else{
        //And is currently not moving
        velocityX = -MIN_WALKING_VELOCITY_TEMP;
      }
    }
  }
  
  void updateY(float timeDif_ms){
    timeDif_ms = 1.0f;
    velocityY = velocityY + (accelerationY*timeDif_ms);
  }
  
  public void collideWithFloor(){
    accelerationY = 0;
    velocityY = 0;
  }

	public void update(){
		long currentTime_ms = System.currentTimeMillis();
		float timeDif = (float) ((currentTime_ms - previousTime_ms)/1000.0);
		
		//For first "move" call, it's safe to assume we're not moving
		if(previousTime_ms != 0){
		  updateX(timeDif);
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

