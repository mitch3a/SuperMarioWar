package model;

public class Physics {
	public static final int RUN_ACCELERATION = 3;
	public static final int MAX_RUN_VELOCITY = 3;
	public static final int MIN_RUN_VELOCITY = -3;
	public static final int JUMP_VELOCITY = -13;
	public static final float GRAVITY = .05f; //based on milliseconds
	
	long previousTime_ms;
	float friction = 0.005f; //TODO obviously these need to be calibrated (guessing friction comes from map?)	
	float velocityX, velocityY;
	float accelerationX, accelerationY;
	boolean activelyMovingX;
	
	public Physics(){
		previousTime_ms = 0;
		velocityX = 0;
		velocityY = 0;
		accelerationX = 0;
		accelerationY = GRAVITY;
		activelyMovingX = false;
	}
	
	public boolean canJump(){
		return (accelerationY == 0);
	}
	
	public void performJump(){
		accelerationY = GRAVITY;
		velocityY = JUMP_VELOCITY;
	}
	
	public void collideWithFloor(){
		accelerationY = 0;
		velocityY = 0;
	}
	
	public void freeFall(){
		accelerationY = GRAVITY;
	}
	
	public void moveRight(){
		activelyMovingX = true;
		accelerationX = RUN_ACCELERATION;
	}
	
	public void moveLeft(){
		activelyMovingX = true;
		accelerationX = (-1)*RUN_ACCELERATION;
	}
	
	public void stopMoving(){
		activelyMovingX = false;
		accelerationX = (velocityX > 0) ? ((-1)*friction) : friction;
	}
	
	public void update(){
		long currentTime_ms = System.currentTimeMillis();
		float timeDif_ms = (currentTime_ms - previousTime_ms);
		
		//For first "move" call, it's safe to assume we're not moving
		if(previousTime_ms != 0){
			updateVelocityX(timeDif_ms);
			updateVelocityY(timeDif_ms);
		}
				
		previousTime_ms = currentTime_ms;
	}
	
	void updateVelocityX(float timeDif_ms){
		velocityX += accelerationX*timeDif_ms;
		
		//Make sure we don't go over the max velocity in either direction
		Math.max(MIN_RUN_VELOCITY, Math.min(MAX_RUN_VELOCITY, velocityX ));
		
		//Don't want to move passed zero if we stopped trying to move
		if(!activelyMovingX){
			if(accelerationX > 0){
				velocityX = Math.min(0, velocityX);
			}
			else{
				velocityX = Math.max(0, velocityX);
			}
		}
	}
	
	void updateVelocityY(float timeDif_ms){
		velocityY = velocityY + (accelerationY*timeDif_ms);
	}

	public int getVelocityX() {
		return (int)this.velocityX;
	}
	
	public int getVelocityY() {
		return (int)this.velocityY;
	}
}
