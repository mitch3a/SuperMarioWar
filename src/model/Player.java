package model;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.Point2D;

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
	Point position, previous_position;
	float velocity_x, velocity_y;
	
	boolean moving_x;//This is to signal that friction should start working (key no longer held down)
	final int RUN_VELOCITY = 10;
	final int JUMP_VELOCITY = -15;
	
	long previous_time;
	//TODO obviously these need to be calibrated
	float gravity = 50f;
	float friction = 30f;
	//TODO not sure if this will stand
	Image image;
	
	public Player(){
		this.position = new Point();
		this.previous_position = new Point();
		velocity_x = 0;
		velocity_y = 0;
		
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
		velocity_x = 0;
		velocity_y = 0;
		previous_position = position.getLocation();
		previous_time = 0;
	}
	
	public void setVelocityX(float x){
		velocity_x = x;
		
		if(velocity_x != 0){
			moving_x = true;
		}
	}
	
	public void setVelocityY(float y){
		velocity_y = y;
	}
	
	public void move(){
		long currentTime = System.currentTimeMillis();
		previous_position = position.getLocation();
		
		float timeDif = (currentTime - previous_time)/1000f;
		
		if(previous_time != 0){
			if(timeDif < 0){
				//TODO log some sort of anomaly
			}
			
			if(!moving_x){
				if(velocity_x > 0){
					velocity_x = Math.max((velocity_x - (friction*timeDif)), 0);
				}
				else{
					velocity_x = Math.min((velocity_x + (friction*timeDif)), 0);
				}
			}
			
			velocity_y = velocity_y + (gravity*timeDif);
		}
		
		//I think this might open a can of worms (rounding)
		position.translate((int)velocity_x, (int)velocity_y);
		
		//TODO bad to hardcode value but this will work for now (trying to make gravity work)
		if(position.y > 500){
			position.y = 500;
			velocity_y = 0;
		}
		previous_time = currentTime;
	}
	
	public void jump(){
		velocity_y = JUMP_VELOCITY;
	}

	public void moveLeft(){
		velocity_x = (-1)*RUN_VELOCITY;
		moving_x = true;
	}
	
	public void moveRight(){
		velocity_x = RUN_VELOCITY;
		moving_x = true;
	}
	
	public void stopMovingLeft(){
		if(velocity_x < 0){
			moving_x = false;
		}
	}
	
	public void stopMovingRight(){
		if(velocity_x > 0){
			moving_x = false;
		}
	}

}
