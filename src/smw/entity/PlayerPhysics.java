package smw.entity;

import smw.Game;
import smw.gfx.Sprite.Direction;
import smw.ui.PlayerControlBase;

public class PlayerPhysics {
	
	
	//These values (actual Velocity/Acceleration) are courtesy of http://s276.photobucket.com/user/jdaster64/media-full//smb_playerphysics.png.html
	//They are represented as (one hex value per the following): 0x (Blocks) (Pixels) (Subpixels) (Subsubpixels) (Subsubsubpixels) for 1/60 seconds per frame
	
	//TODO these values haven't been implemented yet.
	/*
	#define VELMOVING_CHICKEN 2.9f    //speed of the chicken in the gamemode capturethechicken
	#define VELTURBOJUMP  10.2f   //velocity for turbo jumping
	#define VELSUPERJUMP  13.0f;    //super jump (jumping off of orange note blocks)
	#define VELPUSHBACK   5.0f
	#define VELMAXBREAKBLOCK 3.0f
	#define VELBLOCKBOUNCE  3.0f
	#define VELBLOCKREPEL   3.0f
	#define FIREBALLBOUNCE  5.0f
	#define HAMMERTHROW     8.5f
	#define VELAIRFRICTION  0.06f
	#define VELSTOPJUMP     5.0f
	#define BOUNCESTRENGTH  0.5f
	#define TAGGEDBOOST    1.0f
	#define VELSUPERSTOMP 10.0f
	#define VELTAILSHAKE  1.0f
	#define VELKURIBOBOUNCE 3.0f
	#define VELENEMYBOUNCE  7.0f
	#define MAXSIDEVELY    10.0f
	*/ 
  
  /**
   * The following values are based on the original SMW Source. The comments
   * to the right of the value are where it came from. Because the original
   * SMW Source was distance/frame and this is distance/millisecond, some
   * values had to be converted.
   */
  //TODO I don't like this layout of left/right. Maybe a struct to hold them all
	static final float[] MOVING_X_ADD = { 0.03125f, -0.03125f};         // .5/16
	
	static final float[] SLOW_DOWN_X =  {-0.0125f,   0.0125f};  // .2/16
	static final float[] MAX_MOVING_X_SLOW = {4.0f, -4.0f};
	static final float[] MAX_MOVING_X_FAST = {5.5f, -5.5f};
	
	static final float[] MOVING_X_ADD_ICE = { 0.0078125f, -0.0078125f}; // (.5/16)/4
  static final float[] SLOW_DOWN_X_ICE =  {-0.00375f,    0.00375f};   // .06/16
  static final float[] SLOW_DOWN_X_AIR =  {-0.00375f,    0.00375f};   // .06/16
   
	static final float JUMPING_VELOCITY = -9.0f;
	static final float GRAVITY = 0.40f/16f;
	
	static final float MAX_VELOCITY_Y = 20.0f;
	
  static final float DEATH_ACCELERATION =.5f;
  static final float DEATH_STARTING_VELOCITY = -10;
  
  static final float NOTE_BLOCK_REPEL_X = 5.0f;
  static final float NOTE_BLOCK_BOUNCE = 3.0f;
  static final float NOTE_BLOCK_BOUNCE_JUMP_POWER = -8.1f;
		
	float velocityX, velocityY, dx, dy;
	float jumpingAccelerationX, accelerationY;
	
	//private Player player;
	public PlayerControlBase playerControl;
	boolean isJumping;
	boolean isFalling;
	boolean isSkidding;
	boolean canJump;
	boolean isSlippingOnIce;
	
	//TODO mk I don't like using the direction as an index... i think its slow, but I'm doing this
	// for now becuase it's easier to read and speed might not be an issue
	Direction currentVelocityDirection;
	
	public PlayerPhysics(PlayerControlBase playerControl, Player player){
		velocityX = 0;
		velocityY = 0;
		dx = 0;
		dy = 0;
		jumpingAccelerationX = 0;
		accelerationY = GRAVITY;
		isJumping = false;
		isFalling = false;
		isSkidding = false;
		isSlippingOnIce = true;
		canJump = true;
		currentVelocityDirection = Direction.RIGHT;
		this.playerControl = playerControl;
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
		
		if(this.isFalling || this.isJumping){
		  velocityX += SLOW_DOWN_X_AIR[currentVelocityDirection.index]*timeDif;
		}
		else if(isSlippingOnIce){
		  velocityX += SLOW_DOWN_X_ICE[currentVelocityDirection.index]*timeDif;
		}
		else{
		  velocityX += SLOW_DOWN_X[currentVelocityDirection.index]*timeDif;
		}
		
		if(velocityDeceleratedTooFar()){
			velocityX = 0;
			isSkidding = false;
		}
	}
		
	void updateX(float timeDif) {
	  int direction = playerControl.getDirection();

	  if( direction == 0){
	  	passivelySlowDownX(timeDif);
	  }
    else{
      //Move in the direction
    	currentVelocityDirection = (direction > 0) ? Direction.RIGHT : Direction.LEFT;      
      float maxVelocity = (playerControl.isRunning()) ? MAX_MOVING_X_FAST[currentVelocityDirection.index] :
                                                        MAX_MOVING_X_SLOW[currentVelocityDirection.index];
      
      velocityX += MOVING_X_ADD[currentVelocityDirection.index]*timeDif;
      velocityX = (currentVelocityDirection == Direction.RIGHT) ? Math.min(maxVelocity, velocityX) :
                                                                  Math.max(maxVelocity, velocityX);
    }
  }
	
	//TODO mk close but not quite there?
	void setJumpingAccelerationX(){
	  /* TODO
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
		*/
	}
  
  void updateY(float timeDif_ms){
    if(!isFalling && !isJumping && playerControl.isJumping() && canJump){
    	velocityY = JUMPING_VELOCITY;
    	setJumpingAccelerationX();
    	isJumping = true;
    	Game.soundPlayer.sfxJump();
    	canJump = false;
    }
    
    if(isJumping){
    	accelerationY = GRAVITY;
    }
    else{
      //The assumption is that if we are not jumping,
      //then we are falling unless we hit a floor during
      //collision checking
      isFalling = true;
    }
    
    float newVelocity = velocityY + (accelerationY * timeDif_ms);
    velocityY = (newVelocity > MAX_VELOCITY_Y) ? MAX_VELOCITY_Y : newVelocity;
    dy = velocityY*timeDif_ms/1000;
  }
  
  public void collideWithCeiling(){
    //TODO not sure if we even need to do anything here
    velocityY = 0;
  }
  
  public void collideWithFloor(){
  	jumpingAccelerationX = 0;
  	velocityY = 0;
    isJumping = false;
    isSlippingOnIce = false;
    
    //Don't want to allow a new jumping until we get a NEW
    //jump command
    if(!playerControl.isJumping()){
      canJump = true;
    }
    
    isFalling = false;
  }
  
  public void startFalling(){
    isFalling = true;
  }
  
  public void collideWithWall() {
    velocityX = 0;
    isSkidding = false;
  }
  

  public void slipOnIce() {
    isSlippingOnIce = true;
  }
  
  public void death(){
    velocityX = 0;
    velocityY = DEATH_STARTING_VELOCITY;
    accelerationY = DEATH_ACCELERATION;
  }
  
  public void updateForDeath(){
    //TODO not sure if this is the BEST way to do this
    /*
    float newVelocity = velocityY + (accelerationY * timeDif_ms);
    velocityY = (newVelocity > MAX_VELOCITY_Y) ? MAX_VELOCITY_Y : newVelocity;
    */
  }

  public void poll(){
  	playerControl.poll();
  }
  
	public void update(float timeDif){		
		updateX(timeDif);
		updateY(timeDif);
	}

  public float getVelocityX() {
		return velocityX;
	}
	
	public float getVelocityY() {
		return velocityY;//dy;
	}
	
  public void collideWithNoteBlockTop() {
    velocityY = NOTE_BLOCK_BOUNCE;
    
  }
  
  public void collideWithNoteBlockBottom() {
    velocityY = -NOTE_BLOCK_BOUNCE;
    accelerationY = GRAVITY;
    isFalling = true;
    isJumping = false;
    canJump = false;
    
    if(playerControl.isJumping()){
      Game.soundPlayer.sfxSpringJump();
      velocityY += NOTE_BLOCK_BOUNCE_JUMP_POWER;
    }
  }
  
  public void collideWithNoteBlockLeft() {
    velocityX = NOTE_BLOCK_REPEL_X;
    currentVelocityDirection = Direction.RIGHT;
  }
  
  public void collideWithNoteBlockRight() {
    velocityX = -NOTE_BLOCK_REPEL_X;
    currentVelocityDirection = Direction.LEFT;
  }
}
