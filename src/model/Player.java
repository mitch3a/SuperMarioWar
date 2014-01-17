package model;

import java.awt.Image;

public class Player {
	
	Sprite  sprite;
	MarioPhysics physics;
		
	public Player(){	
		physics = new MarioPhysics();
		sprite  = new Sprite();
	}
	
	//TODO getters/setters might be slower for something being called so often 
	//     (also might want to draw in the sprite level?)
	public int getX(){
		return sprite.getX();
	}
	
	public int getY(){
		return sprite.getY();
	}
	
	public Image getImage(){
		return sprite.getImage();
	}
	
	public void init(int start_x, int start_y, String image){
		sprite.init(start_x, start_y, image);
	}

	public void move(){
		physics.update();
		sprite.move(physics.getVelocityX(), physics.getVelocityY());
		
		//TODO this is to simulate ground...
		if(sprite.getY() > 500){
			sprite.position.setLocation(sprite.position.getX(), 500);
			physics.collideWithFloor();
		}
	}
	
	//###################################################################
	// These methods are for the Input Handler. They are strictly actions
	// commanded by the Input Handler and must be checked.
	//###################################################################
	public void jump(){
		if(physics.canJump()){
			physics.performJump();
			sprite.setJumping();
		}
	}

	public void moveLeft(){
		physics.moveLeft();
		sprite.movingLeft();
	}
	
	public void moveRight(){
		physics.moveRight();
		sprite.movingRight();
	}
	
	public void stopMoving(){
		physics.stopMoving();
	}
}
