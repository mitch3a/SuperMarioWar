package smw.entity;

import java.awt.Image;
import java.awt.event.KeyListener;

import smw.gfx.Sprite;
import smw.ui.PlayerControl;

public class Player {
	Sprite  sprite;
	PlayerPhysics physics;
	PlayerControl playerControl;
		
	public Player(PlayerControl playerControl){	
		physics = new PlayerPhysics(playerControl);
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
			sprite.setY(500);
			physics.collideWithFloor();
		}
	}

	public final KeyListener getControls() {
	  return physics.playerControl;
  }
}
