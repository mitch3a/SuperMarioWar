package model;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;

import javax.swing.ImageIcon;

import model.Sprite.Transform;

public class Player {
	Sprite sprite;
	
	float velocity_x, velocity_y;
	float acceleration_x;
	boolean moving_x;//This is to signal that friction should start working (key no longer held down)
	boolean moving_right;//TODO don't like this but simple solution for now
	
	final int RUN_ACCELERATION = 3;//30;
	final int MAX_RUN_VELOCITY = 3;
	final int JUMP_VELOCITY = -13;
	
	long previous_time;
	//TODO obviously these need to be calibrated
	float gravity = 50f;
	float friction = 1f;//5f;
	//TODO not sure if this will stand
	
	public Player(){
		velocity_x = 0;
		velocity_y = 0;
		acceleration_x = 0;
		
		//TODO obviously not hard coded
		sprite = new Sprite("Luigi.bmp");
	}
	
	//TODO getters/setters might be slower for something being called so often (also might want to draw in the sprite level?)
	public int getX(){
		return sprite.getX();
	}
	
	public int getY(){
		return sprite.getY();
	}
	
	public Image getImage(){
		return sprite.getImage();
	}
	
	public void init(int start_x, int start_y){
		sprite.init(start_x, start_y);
		velocity_x = 0;
		velocity_y = 0;
		previous_time = 0;
	}

	public void move(){
		long currentTime = System.currentTimeMillis();
		
		float timeDif = (currentTime - previous_time)/1000f;
		
		if(previous_time != 0){
			if(timeDif < 0){
				//TODO log some sort of anomaly
			}
			
			velocity_x += acceleration_x*timeDif;
			
			if(acceleration_x > 0){
				velocity_x = Math.min(MAX_RUN_VELOCITY, velocity_x);
			}
			else if(acceleration_x < 0){
				velocity_x = Math.max((-1)*MAX_RUN_VELOCITY, velocity_x);
			}
			else{
				//No acceleration
				if(velocity_x > 0){
					velocity_x = Math.max(0, velocity_x - friction*timeDif);
				}
				else if (velocity_x < 0){
					velocity_x = Math.min(0, velocity_x + friction*timeDif);
				}
			}

			velocity_y = velocity_y + (gravity*timeDif);

			//TODO bad to hardcode value but this will work for now (trying to make gravity work)
			if(sprite.getY() + velocity_y > 500){
				velocity_y = 500 - sprite.getY();
			}
		}
		
		//I think this might open a can of worms (rounding)
		sprite.move((int)velocity_x, (int)velocity_y);
		previous_time = currentTime;
	}
	
	public void jump(){
		if(velocity_y == 0){
			velocity_y = JUMP_VELOCITY;
			sprite.setJumping();
		}
	}

	public void moveLeft(){
		if(!moving_x || moving_right){
			acceleration_x = (-1)*RUN_ACCELERATION;
			moving_x = true;
			moving_right = false;
			sprite.transform(Transform.TRANS_MIRROR);
		}
	}
	
	public void moveRight(){
		if(!moving_x || !moving_right){
			acceleration_x = RUN_ACCELERATION;
			moving_x = true;
			moving_right = true;
			sprite.transform(Transform.TRANS_NONE);
		}
	}
	
	//TODO there is a bug somewhere in here where Acceleration can get stuck (maybe not garaunteed key strokes in order??)
	public void stopMovingLeft(){
		if(velocity_x < 0 && !moving_right){
			moving_x = false;
			acceleration_x = 0;
		}
	}
	
	public void stopMovingRight(){
		if(velocity_x > 0 && moving_right){
			moving_x = false;
			acceleration_x = 0;
		}
	}

	public AffineTransform getTransform() {
		return sprite.getTransform();
	}
}
