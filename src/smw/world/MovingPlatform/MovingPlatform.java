package smw.world.MovingPlatform;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import smw.Collidable;
import smw.Drawable;
import smw.Updatable;
import smw.entity.Player;
import smw.gfx.Sprite;
import smw.ui.screen.GameFrame;
import smw.world.Tile;

public class MovingPlatform implements Drawable, Updatable{
  Path path;
  Tile[][] tiles;
  Collidable[][] collidables;
  
  public MovingPlatform(Tile[][] tiles, Collidable[][] collidables, Path path){
    this.path = path;
    this.tiles = tiles;
    this.collidables = collidables;
    
    //Shift the collidables by the start point of the path
    for(Collidable[] collidableArray : collidables){
      for(Collidable collidable : collidableArray){
        collidable.move(path.getX(), path.getY());
      }
    }
  }
 
  public float getX(){
    return path.getX();
  }
  
  public float getY(){
    return path.getY();
  }
  
  public void draw(Graphics2D graphics, ImageObserver observer){
    int startY = path.getY();
    int x = path.getX();
    
    for(int i = 0 ; i < tiles.length ; ++i){
     int y = startY;
    
     for(int j = 0 ; j < tiles[i].length ; ++j){
       tiles[i][j].draw(graphics, x, y, observer);
       
       if(x > GameFrame.res_width - Tile.SIZE){
         //Draw it if it overlaps
         tiles[i][j].draw(graphics, x - GameFrame.res_width, y, observer);
       }
       
       y += Tile.SIZE;
     }
     
     x += Tile.SIZE;
     
     if(x > GameFrame.res_width){
       x = x % GameFrame.res_width;
     }
    }
  }
  
  Collidable getCollidable(float x, float y){
    float difX = x - path.getX();
    float difY = y - path.getY(); 
    
    difX = (difX + GameFrame.res_width) % GameFrame.res_width;
    difY = (difY + GameFrame.res_height) % GameFrame.res_height;
    
    int indexX = (difX >= 0) ? (int)(difX/Tile.SIZE) : -1;
    int indexY = (difY >= 0) ? (int)(difY/Tile.SIZE) : -1;
    
    if(indexX >= 0 && indexX < tiles.length){
      if(indexY >= 0 && indexY < tiles[indexX].length){
        return collidables[indexX][indexY];
      }
    }
    
    return null;
  }

  @Override
  public void update(float timeDif_ms) {
    timeDif_ms = 1.0f;//TODO
    path.move(timeDif_ms);
    
    for(Collidable[] collidableArray : collidables){
      for(Collidable collidable : collidableArray){
        collidable.move(path.getXChange(), path.getYChange());
      }
    }
  }
  
  /**
   * Helper method to check collision between right side of a player and
   * left side of a collidable
   */
  float checkCollisionRight(Player player, float xToCheck, float yToCheck, float newX){
    Collidable temp = getCollidable(xToCheck, yToCheck);
    
    if(temp != null){
      newX = temp.collideWithLeft(player, newX);
    }
    
    return newX;
  }
  
  /**
   * Helper method to check collision between left side of a player and
   * right side of a collidable
   */
  float checkCollisionLeft(Player player, float xToCheck, float yToCheck, float newX){
    Collidable temp = getCollidable(xToCheck, yToCheck);
    
    if(temp != null){
      newX = temp.collideWithRight(player, newX);
    }
    
    return newX;
  }
  
  /**
   * Helper method to check collision between left side of a player and
   * right side of a collidable
   */
  boolean willDrag(float xToCheck, float yToCheck){
    Collidable temp = getCollidable(xToCheck, yToCheck);
    
    if(temp != null){
      return temp.willDrag();
    }
    
    return false;
  }
  
  public float collideX(Player player, float newX) {
    float rightMostX = newX + Sprite.IMAGE_WIDTH - 1;
    float bottomYPlayer = player.y +Sprite.IMAGE_HEIGHT - 1;
    
    float justUnderPlayer = player.y + Sprite.IMAGE_HEIGHT+ 1;
    
    //If the platform underneath is moving, so should the player
    if(willDrag(newX,       justUnderPlayer) || 
       willDrag(rightMostX, justUnderPlayer)){
      newX += path.getXChange();
    }
            
    newX = checkCollisionRight(player, rightMostX, player.y,      newX);
    newX = checkCollisionRight(player, rightMostX, bottomYPlayer, newX);
    newX = checkCollisionLeft (player, newX,       player.y,      newX);
    newX = checkCollisionLeft (player, newX,       bottomYPlayer, newX);

    return newX;
  }

  /**
   * Helper method to check collision between top of a player and
   * bottom of a collidable
   */
  float checkCollisionTop(Player player, float xToCheck, float yToCheck, float newY){
    Collidable temp = getCollidable(xToCheck, yToCheck);
    
    if(temp != null){
      newY = temp.collideWithBottom(player, newY);
    }
    
    return newY;
  }
  
  /**
   * Helper method to check collision between bottom of a player and
   * top of a collidable
   */
  float checkCollisionBottom(Player player, float xToCheck, float yToCheck, float newY){
    Collidable temp = getCollidable(xToCheck, yToCheck);
    
    if(temp != null){
      newY = temp.collideWithTop(player, newY);
    }
    
    return newY;
  }
  
  public float collideY(Player player, float newX, float newY) {
    float rightMostX = newX + Sprite.IMAGE_WIDTH - 1;
    float bottomYPlayer = newY +Sprite.IMAGE_HEIGHT - 1;
    
    //TODO i think this (and X version) should probably be called before the collides?
    float justUnderPlayer = player.y + Sprite.IMAGE_HEIGHT + 1 + path.getYChange();
    //If the platform underneath is moving, so should the player
    if(willDrag(newX,       justUnderPlayer) || 
       willDrag(rightMostX, justUnderPlayer)){
      newY += path.getYChange();
    }
         
    
    newY = checkCollisionTop   (player, newX,       newY,      newY);
    newY = checkCollisionTop   (player, rightMostX, newY,      newY);
    newY = checkCollisionBottom(player, newX,       bottomYPlayer, newY);
    newY = checkCollisionBottom(player, rightMostX, bottomYPlayer, newY);
    
    return newY;
  }
}
