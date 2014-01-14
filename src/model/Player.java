package model;

import java.awt.Image;
import java.awt.Point;

import javax.swing.ImageIcon;

public class Player {
	
	/* TODO these are not used yet (and probably shouldn't be in yet but oh well)
	enum PlayerState {	player_wait, 
						player_spawning, 
						player_dead, 
						player_ready, 
						player_entering_warp_up, 
						player_entering_warp_right, 
						player_entering_warp_down, 
						player_entering_warp_left, 
						player_exiting_warp_down, 
						player_exiting_warp_left, 
						player_exiting_warp_up, 
						player_exiting_warp_right };
						
	enum PlayerAction { player_action_none, 
					    player_action_bobomb, 
					    player_action_fireball, 
					    player_action_hammer, 
					    player_action_boomerang, 
					    player_action_iceblast, 
					    player_action_bomb, 
					    player_action_spincape, 
					    player_action_spintail };
					    
	Score score;
	Player state;
	*/
	
	//TODO consider defining own version with no get/set. Made these points because I thought it would read better
	Point position, previous_position, velocity;
	//TODO not sure if this will stand
	Image image;
	
	public Player(){
		this.position = new Point();
		this.previous_position = new Point();
		this.velocity = new Point();
		//TODO temp
		image = new ImageIcon(this.getClass().getResource("mario.jpg")).getImage();
	}
	
	//TODO getters/setters might be slower for something being called so often
	public int getX(){
		return position.x;
	}
	
	public int getY(){
		return position.y;
	}
	
	public Image getImage(){
		return image;
	}
	
	public void init(int start_x, int start_y){
		position.x = start_x;
		position.y = start_y;
		velocity.x = 0;
		velocity.y = 0;
		previous_position = position.getLocation();
	}
	
	public void setVelocityX(int x){
		velocity.x = x;
	}
	
	public void setVelocityY(int y){
		velocity.y = y;
	}
	
	public void move(){
		previous_position = position.getLocation();
		position.translate(velocity.x, velocity.y);
	}

	public boolean isMovingLeft() {
		return velocity.x < 0;
	}
	
	public boolean isMovingRight() {
		return velocity.x > 0;
	}
	
	public boolean isMovingUp() {
		return velocity.y < 0;
	}
	
	public boolean isMovingDown() {
		return velocity.y > 0;
	}
}
