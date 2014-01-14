package ui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import model.Player;

//This is a temporary implementation to just get a block moving
public class PlayerControl extends KeyAdapter{
	Player player;
	
	int left;
	int right;
	int up;
	int down;
	int jump;

	final int VELOCITY = 10;
	final int NEGATIVE_VELOCITY = -10;
	
	public PlayerControl(Player player){
		this.player = player;
		
		//TODO these are just temp for testing (or default?)
		left = KeyEvent.VK_LEFT;
		right = KeyEvent.VK_RIGHT;
		up = KeyEvent.VK_UP;
		down = KeyEvent.VK_DOWN;
	}
	
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();

		//TODOD this (and on keypressed) propose an interesting question of how to handle opposing inputs
        if(key == left){
        	if(player.isMovingLeft()){
        		player.setVelocityX(0);
        	}
        	return;
        }
	
		if(key == right){
	    	if(player.isMovingRight()){
	    		player.setVelocityX(0);
	    	}
	    	return;
	    }
        
        if(key == up){
        	if(player.isMovingUp()){
        		player.setVelocityY(0);
        	}
        }

        if(key == down){
        	if(player.isMovingDown()){
        		player.setVelocityY(0);
        	}
        }
    }

    public void keyPressed(KeyEvent e) {
    	int key = e.getKeyCode();

        if(key == left){
        	player.setVelocityX(NEGATIVE_VELOCITY);
        	return;
        }
        
        if(key == right){
        	player.setVelocityX(VELOCITY);
        	return;
        }
        
        if(key == up){
        	player.setVelocityY(NEGATIVE_VELOCITY);
        	return;
        }
        
        if(key == down){
        	player.setVelocityY(VELOCITY);
        	return;
        }
    }
}
