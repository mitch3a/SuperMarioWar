package model;

import java.awt.Image;

import ui.PlayerControl;

public class Player {
	
	Sprite  sprite;
	MarioPhysics physics;
	PlayerControl playerControl;
		
	public Player(PlayerControl playerControl){	
		physics = new MarioPhysics(playerControl);
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
		sprite.move(physics.getVelocityX(), physics.getVelocityY(), physics.isJumping, physics.isSkidding);
		
		//TODO this is to simulate ground...
		if(sprite.getY() > 500){
			sprite.position.setLocation(sprite.position.getX(), 500);
			physics.collideWithFloor();
		}
	}
}
