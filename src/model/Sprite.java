package model;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Sprite {
	public static enum Transform { TRANS_NONE, TRANS_MIRROR };
	public static enum Action { NONE, RUNNING_STEP, RUNNING_NO_STEP, JUMPING, TURNING, DYING, SQUASHED };
	
	public static final int IMAGE_WIDTH = 32;
	public static final int IMAGE_HEIGHT = 32;
	public static final int NUM_IMAGES = 6;
	
	Point position;
	
	BufferedImage[] sprites = new BufferedImage[NUM_IMAGES];
	Action current_action = Action.NONE;
	Transform current_transform = Transform.TRANS_NONE;
	//TODO no clue if this is the best way to do this
	public static final long TIME_TO_HALF_STEP = 100;//ms
	long time_action_change = 0;

	public Sprite(String image){
		this.position = new Point();

		BufferedImage bigImg;
		try {
			bigImg = ImageIO.read(this.getClass().getResource(image));
			
		    for (int i = 0; i < NUM_IMAGES; i++)
		    {
		        sprites[i] = bigImg.getSubimage( i * IMAGE_WIDTH, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
		    }
		} catch (IOException e) {
			// TODO UH OH
			e.printStackTrace();
		}
	}
	
	public void init(int x, int y) {
		this.position.x = x;
		this.position.y = y;
	}
	
	public int getX(){
		return position.x;
	}
	
	public int getY(){
		return position.y;
	}
	
	public void transform(Transform transform){
		current_transform = transform;
	}
	
	public boolean collidesWith(Sprite sprite){
		//TODO
		return false;
	}
	
	//It is assumed this method is only called after collision detection passed
	public void move(int dx, int dy){
		switch(current_action){
			case NONE: 				checkForRunning(dx);
					   				break;
			case RUNNING_STEP: 		checkForRunningStepChange(dx, Action.RUNNING_NO_STEP);
			   						break;
			case RUNNING_NO_STEP:	checkForRunningStepChange(dx, Action.RUNNING_STEP);
			   						break;
			case JUMPING: 			checkForJumpComplete(dx, dy);
			   						break;
			case TURNING:		 	checkForTurnComplete(dx);
			   						break;
			case SQUASHED: 			//TODO
			   						break;
			case DYING: 			//TODO
									break;
		}
		
		position.translate(dx, dy);
	}

	public AffineTransform getTransform() {
		AffineTransform result = AffineTransform.getTranslateInstance(position.x, position.y);
		
		boolean flip = false;
		
		if(current_transform == Transform.TRANS_MIRROR){
			flip = true;
		}
		
		if(current_action == Action.TURNING){
			flip = !flip;
		}

		if(flip){
			result.scale(-1.0, 1.0);
			result.translate((-1)*IMAGE_WIDTH, 0);
		}
		
		return result;
	}
		
	private void checkForRunningStepChange(int dx, Action nextRunningAction){
		if(dx == 0){
			current_action = Action.NONE;
		}
		else{
			//Make sure we're not turning around
			if(this.current_transform == Transform.TRANS_NONE){
				if(dx <= 0){
					current_action = Action.TURNING;
				}
			}
			else{
				if(dx >= 0){
					current_action = Action.TURNING;
				}
			}
			
			long currentTime = System.currentTimeMillis();
			if(currentTime - time_action_change > TIME_TO_HALF_STEP){
				current_action = nextRunningAction;
				time_action_change = currentTime;
			}
		}
	}
	
	private void checkForRunning(int dx){
		if(dx != 0){
			current_action = Action.RUNNING_STEP;
			time_action_change = System.currentTimeMillis();
		}
	}
	
	private void checkForJumpComplete(int dx, int dy){
		if(dy == 0){
			if(dx != 0){
				current_action = Action.RUNNING_STEP;
				time_action_change = System.currentTimeMillis();
			}
			else{
				current_action = Action.NONE;
			}
		}
	}
	
	private void checkForTurnComplete(int dx){
		if(this.current_transform == Transform.TRANS_NONE){
			if(dx >= 0){
				current_action = Action.RUNNING_STEP;
				time_action_change = System.currentTimeMillis();
			}
		}
		else{
			if(dx <= 0){
				current_action = Action.RUNNING_STEP;
				time_action_change = System.currentTimeMillis();
			}
		}
	}

	public Image getImage() {		
		return sprites[getIndex(current_action)];
	}
	
	public static int getIndex(Action action) {
		int result = -1;
		switch(action){
			case NONE: 				result = 0;
					   				break;
			case RUNNING_STEP: 		result = 0;
			   						break;
			case RUNNING_NO_STEP:	result = 1;
			   						break;
			case JUMPING: 			result = 2;
			   						break;
			case TURNING:		 	result = 3;
			   						break;
			case SQUASHED: 			result = 4;
			   						break;
			case DYING: 			result = 5;
									break;
		}
		
		return result;
	}
	
	public void setJumping(){
		current_action = Action.JUMPING;
	}
}
