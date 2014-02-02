package smw.entity;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;

import smw.Game;
import smw.gfx.Palette.ColorScheme;
import smw.gfx.Sprite;
import smw.ui.PlayerControlBase;
import smw.ui.screen.GameFrame;
import smw.world.Tile;

public class Player extends Rectangle{
	/**
	 * Autogenerated!
	 */
	private static final long serialVersionUID = -4197702383138211374L;
	private static final long RESPAWN_WAIT_MS = 2000;
	private Sprite  sprite;
	private PlayerPhysics physics;
	private Score score;
	private final int playerIndex;
	private boolean crushed = false;
	private long respawnTime;
	
	/** Indicates whether a player is falling through a tile by pressing down key. */
	private boolean isFallingThrough = false;
	/** The starting height of falling through a "solid on top" tile. */
	private int fallHeight = 0;
	private boolean pushedDown = false;
	
	public Player(PlayerControlBase playerControl, int playerIndex){	
		physics = new PlayerPhysics(playerControl);
		sprite  = new Sprite();
		score   = new Score();
		this.playerIndex = playerIndex;
	}
	
	void setBounds(int newX, int newY){
	  //TODO might be a faster way (width/height never change)
		setBounds(newX, newY, Sprite.IMAGE_WIDTH, Sprite.IMAGE_HEIGHT);			
	}
	
	public Image getImage(){
		return sprite.getImage();
	}
	
	public void init(int newX, int newY, String image){
		setBounds(newX, newY);
		
		//TODO this is obviously not staying in
		int i = (int)(4*Math.random());
		ColorScheme color = ColorScheme.YELLOW;
		if(i == 0){
		  color = ColorScheme.RED;
		}
		else if(i == 1){
      color = ColorScheme.GREEN;
    }
		else if(i == 2){
      color = ColorScheme.BLUE;
    }
		
		sprite.init(image, color);
	}
	
	/*** This method is to get the state ready to move ***/
	public void prepareToMove(){
		physics.update();
	}
		
	public void move(Player[] players){	
		if(crushed){
			//TODO this is messy and should be a method called at the beginning
			if(respawnTime < System.currentTimeMillis()){
				crushed = false;
				setBounds(300, 100);
				sprite.setJumping();
			}
			
			return; //Do not want to do anything with someone who is crushed
		}
			
		float dx = physics.getVelocityX();
		float dy = physics.getVelocityY();
		
		sprite.update(dx, dy, physics.isJumping, physics.isSkidding);
		int newX, newY = 0;
		
		newX = (int) (x + dx);
		newY = (int) (y + dy);
		
		if (x != newX) {
      if (x > newX) {
        //Moving left
        if(Game.world.getTileType(newX, y) == Tile.TileType.SOLID){
          newX = newX +  Tile.SIZE - (newX % Tile.SIZE);
          physics.collideWithWall();
        }
      }
      else{
        //Moving right
        if(Game.world.getTileType(newX + Sprite.IMAGE_WIDTH, y) == Tile.TileType.SOLID){
          newX = newX - (newX % Tile.SIZE);
          physics.collideWithWall();
        }
      }
    } 

    if (y != newY){
      if (y < newY) {
        // If the player pushed the down key check to see if it was released.
        if (pushedDown) {
          pushedDown = physics.playerControl.isDown();
        }
        // If falling through a solid on top block then reset flag when the player has fallen at least one tile.
        if (isFallingThrough && (y - fallHeight) >= Tile.SIZE) {
          isFallingThrough = false;
        }
        
        //Moving down. We want to check every block that is under the sprite. This is from the first 
        //             Pixel (newX) to the last (newX + (Sprite.Width - 1))
        Tile.TileType tile1 = Game.world.getTileType(newX, newY + Sprite.IMAGE_HEIGHT);
        Tile.TileType tile2 = Game.world.getTileType(newX + Sprite.IMAGE_WIDTH - 1, newY + Sprite.IMAGE_HEIGHT);
        
        if(tile1 != Tile.TileType.NONSOLID || tile2 != Tile.TileType.NONSOLID) {
          // Handle case where player is allowed to sink through a tile.
          if((tile1 == Tile.TileType.SOLID_ON_TOP || tile1 == Tile.TileType.NONSOLID) &&
             (tile2 == Tile.TileType.SOLID_ON_TOP || tile2 == Tile.TileType.NONSOLID) &&
             (!pushedDown && physics.playerControl.isDown())) {
                // If this is the first time we reached this then the player pushed down the first time to fall through.
                // Set the falling through flags and height.
                if (!isFallingThrough) {
                  isFallingThrough = true;
                  fallHeight = y;
                  pushedDown = true;
                }
          }
          else if (!isFallingThrough){ // Not falling through a solid on top tile so it's OK to collide with floor. 
            newY = newY - (newY % Tile.SIZE); // Just above the floor.
            physics.collideWithFloor();
          }
        }
      }
      else {
        //Moving up
        if(Game.world.getTileType(newX, newY) == Tile.TileType.SOLID ||
           Game.world.getTileType(newX + Sprite.IMAGE_WIDTH - 1, newY) == Tile.TileType.SOLID){
          newY += Tile.SIZE - newY % Tile.SIZE;
          physics.collideWithCeiling();
        }
      }
    }

		//physics.update();
		
		boolean xCollide = false;
		//This is definitely not right... but its kinda cool that it sort of works
		for(Player p : players){
			if(p.playerIndex != playerIndex){
				if(!p.crushed && p.intersects(newX, y, Sprite.IMAGE_WIDTH, Sprite.IMAGE_HEIGHT)){
				  if(p.x > newX){
				    newX = p.x - Sprite.IMAGE_WIDTH - 1;
				  }
				  else{
				    newX = p.x + Sprite.IMAGE_HEIGHT + 1;
				  }
				  
					xCollide = true;
					break;
				}
			}
		}
		
		for(Player p : players){
			if(p.playerIndex != playerIndex){
				if(!p.crushed && p.intersects(x, newY, Sprite.IMAGE_WIDTH, Sprite.IMAGE_HEIGHT)){
					if(!xCollide){
						if(p.getY() < newY){
						  p.score.increaseScore();
							crush();
						}
						else{
						  score.increaseScore();
							p.crush();
						}
					}
					physics.collideWithFloor();
					newY = y;
					break;
				}
			}
		}

	/*****************************************************
	 *TODO this is not complete but good enough for now
	 *
	 *THIS WILL WRAP THE SCREEN AROUND CHA CHING mk
	 ******************************************************/
		newX = newX % GameFrame.res_width;
		newY = newY % GameFrame.res_height;
		
		if(newX < 0){
		  newX += GameFrame.res_width;
		}
		
		if(newY < 0){
		  newY += GameFrame.res_height;
		}
		
		setBounds(newX, newY);					
	}
		
	protected void crush(){
		crushed = true;
		//TODO mk didn't like this but if you want to play with it, make gameFrame static and this works 
		//Game.gameFrame.bump();
		respawnTime = System.currentTimeMillis() + RESPAWN_WAIT_MS;
		sprite.crush();
	}
	
	public void poll(){
		physics.poll();
	}
	
	public void draw(Graphics2D graphics, ImageObserver observer){
	  graphics.drawImage(sprite.getImage(), x, y, observer);
	}
	
	public int getScore(){
		return score.getScore();
	}
}
