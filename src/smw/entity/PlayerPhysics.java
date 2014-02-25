package smw.entity;

import smw.Game;
import smw.gfx.Sprite.Direction;
import smw.ui.PlayerControlBase;

public class PlayerPhysics {
	
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

  static class PhysicsValues{
    //Constant for either direction
    static final float JUMPING_VELOCITY = -9.0f;
    static final float GRAVITY = 0.40f/16f;
    
    static final float MAX_VELOCITY_Y = 20.0f;
    
    static final float DEATH_ACCELERATION =.5f;
    static final float DEATH_STARTING_VELOCITY = -10;
    
    static final float NOTE_BLOCK_BOUNCE = 3.0f;
    static final float NOTE_BLOCK_BOUNCE_JUMP_POWER = -8.1f;
    
    //Varies by direction
    final float MOVING_X_ADD; 
    
    final float SLOW_DOWN_X;
    final float MAX_MOVING_X_SLOW;
    final float MAX_MOVING_X_FAST;
    
    final float MOVING_X_ADD_ICE;
    final float SLOW_DOWN_X_ICE;
    final float SLOW_DOWN_X_AIR;
    
    final float NOTE_BLOCK_REPEL_X;
    
    PhysicsValues(Direction direction){
      int factor = (direction == Direction.RIGHT) ? 1 : -1; 
      
      MOVING_X_ADD = factor*0.03125f;        // .5/16
      
      SLOW_DOWN_X =  factor*-0.0125f;          // .2/16
      MAX_MOVING_X_SLOW = factor*4.0f;
      MAX_MOVING_X_FAST = factor*5.5f;
      
      MOVING_X_ADD_ICE = factor*0.0078125f;    //(.5/16)/4
      SLOW_DOWN_X_ICE =  factor*(-0.00375f);   // .06/16
      SLOW_DOWN_X_AIR =  factor*(-0.00375f);   // .06/16
      
      NOTE_BLOCK_REPEL_X = factor*5.0f;
    }
  }
  
  static final PhysicsValues leftPhysicsValues  = new PhysicsValues(Direction.LEFT);
  static final PhysicsValues rightPhysicsValues = new PhysicsValues(Direction.RIGHT);
  private PhysicsValues currentPhysicsValues = rightPhysicsValues;
		
	float velocityX, velocityY;
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
		jumpingAccelerationX = 0;
		accelerationY = PhysicsValues.GRAVITY;
		isJumping = false;
		isFalling = false;
		isSkidding = false;
		isSlippingOnIce = true;
		canJump = true;
		setDirection(Direction.RIGHT);
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
		  velocityX += currentPhysicsValues.SLOW_DOWN_X_AIR*timeDif;
		}
		else if(isSlippingOnIce){
		  velocityX += currentPhysicsValues.SLOW_DOWN_X_ICE*timeDif;
		}
		else{
		  velocityX += currentPhysicsValues.SLOW_DOWN_X*timeDif;
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
    	setDirection((direction > 0) ? Direction.RIGHT : Direction.LEFT);      
      float maxVelocity = (playerControl.isRunning()) ? currentPhysicsValues.MAX_MOVING_X_FAST :
                                                        currentPhysicsValues.MAX_MOVING_X_SLOW;
      
      velocityX += currentPhysicsValues.MOVING_X_ADD*timeDif;
      velocityX = (currentVelocityDirection == Direction.RIGHT) ? Math.min(maxVelocity, velocityX) :
                                                                  Math.max(maxVelocity, velocityX);
    }
  }
	 
  void updateY(float timeDif_ms){
    if(!isFalling && !isJumping && playerControl.isJumping() && canJump){
    	velocityY = PhysicsValues.JUMPING_VELOCITY;
    	isJumping = true;
    	Game.soundPlayer.sfxJump();
    	canJump = false;
    }
    
    if(isJumping){
    	accelerationY = PhysicsValues.GRAVITY;
    }
    else{
      //The assumption is that if we are not jumping,
      //then we are falling unless we hit a floor during
      //collision checking
      isFalling = true;
    }
    
    float newVelocity = velocityY + (accelerationY * timeDif_ms);
    velocityY = (newVelocity > PhysicsValues.MAX_VELOCITY_Y) ? PhysicsValues.MAX_VELOCITY_Y : newVelocity;
  }
  
  public void collideWithCeiling(){
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
    velocityY = PhysicsValues.DEATH_STARTING_VELOCITY;
    accelerationY = PhysicsValues.DEATH_ACCELERATION;
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
		return velocityY;
	}
	
  public void collideWithNoteBlockTop() {
    velocityY = PhysicsValues.NOTE_BLOCK_BOUNCE;
    
  }
  
  public void collideWithNoteBlockBottom() {
    velocityY = -PhysicsValues.NOTE_BLOCK_BOUNCE;
    accelerationY = PhysicsValues.GRAVITY;
    isFalling = true;
    isJumping = false;
    canJump = false;
    
    if(playerControl.isJumping()){
      Game.soundPlayer.sfxSpringJump();
      velocityY += PhysicsValues.NOTE_BLOCK_BOUNCE_JUMP_POWER;
    }
  }
  
  public void collideWithNoteBlockLeft() {
    setDirection(Direction.RIGHT);
    velocityX = currentPhysicsValues.NOTE_BLOCK_REPEL_X;
  }
  
  public void collideWithNoteBlockRight() {
    setDirection(Direction.LEFT);
    velocityX = currentPhysicsValues.NOTE_BLOCK_REPEL_X;
  }
  
  private void setDirection(Direction newDirection){
    currentVelocityDirection = newDirection;
    currentPhysicsValues = (currentVelocityDirection == Direction.RIGHT) ? rightPhysicsValues : leftPhysicsValues;
  }
}
