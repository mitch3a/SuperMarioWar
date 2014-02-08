package smw.world.MovingPlatform;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import smw.Collidable;
import smw.Drawable;
import smw.Updatable;
import smw.entity.Player;
import smw.gfx.Sprite;
import smw.world.Tile;
import smw.world.Tile.TileType;

public class MovingPlatform implements Drawable, Updatable{
  Path path;
  Tile[][] tiles;
  Collidable[][] collidables; //TODO really don't like this
  
  public MovingPlatform(Tile[][] tiles, Collidable[][] collidables, Path path){
    this.path = path;
    this.tiles = tiles;
    this.collidables = collidables;
  }
 
  public int getX(){
    return path.getX();
  }
  
  public int getY(){
    return path.getY();
  }
  
  public void draw(Graphics2D graphics, ImageObserver observer){
    int startY = path.getY();
    int x = path.getX();
    
    for(int i = 0 ; i < tiles.length ; ++i){
     int y = startY;
    
     for(int j = 0 ; j < tiles[i].length ; ++j){
       Tile tile = tiles[i][j];
       //Sometimes there is a gap in the middle
       if(tile.hasImage()){
         graphics.drawImage(tile.getImage(), x, y, observer);
       }
       y += Tile.SIZE;
     }
     
     x += Tile.SIZE;
    }
    /* TODO mk want something like this but would need to pass the x, y of path down somehow
    for(int i = 0 ; i < tiles.length ; ++i){
      for(int j = 0 ; j < tiles[i].length ; ++j){
        tiles[i][j].draw(graphics, observer);
      }
    }
    */
  }

  public TileType getTile(int x, int y) {
    int difX = x - path.getX();
    int difY = y - path.getY();
    
    int indexX = (difX >= 0) ? difX/Tile.SIZE : -1;
    int indexY = (difY >= 0) ? difY/Tile.SIZE : -1;
    
    if(indexX >= 0 && indexX < tiles.length){
      if(indexY >= 0 && indexY < tiles[indexX].length){
        return collidables[indexX][indexY].type;
      }
    }
    
    return Tile.TileType.NONSOLID;
  }

  public int getXChange() {
    return path.getXChange();
  }

  @Override
  public void update(float timeDif_ms) {
    timeDif_ms = 1.0f;//TODO
    path.move(timeDif_ms);
  }

  //This method returns the pixel a player should be at if this
  //platform is pushing him/her
  public int getFrontEdgeX(int newX) {
    //TODO not the most accurate width
    int leftX = getX();
    int rightX = leftX + tiles[0].length*Tile.SIZE;

    //TODO these additive factors are bc the way the turnaround works, platform might not
    //     hit the edge during a frame. This should be done more accurately somehow
    if(Math.abs(leftX - newX) > Math.abs(rightX - newX)){
      return rightX + 2;
    }
    
    return leftX - Tile.SIZE - 2;
  }

  public int collideX(Player player, int newX) {
    int rightMostX = newX + Sprite.IMAGE_WIDTH - 1;
    int bottomYPlayer = player.y +Sprite.IMAGE_HEIGHT - 1;
    
    //If the platform is pushing player left/right, update newX
    if(Tile.TileType.SOLID == getTile(rightMostX, player.y) ||
       Tile.TileType.SOLID == getTile(newX, player.y) ||
       Tile.TileType.SOLID == getTile(rightMostX, bottomYPlayer) ||
       Tile.TileType.SOLID == getTile(newX, bottomYPlayer)){
      
      newX = getFrontEdgeX(newX);
    }
    
    
    int justUnderPlayer = player.y + Sprite.IMAGE_HEIGHT+ 1;
    
    //If the platform underneath is moving, so should the player
    if(Tile.TileType.SOLID == getTile(newX,       justUnderPlayer) || 
       Tile.TileType.SOLID == getTile(rightMostX, justUnderPlayer)){
      newX += getXChange();
    }
    
    return newX;
  }

  public int collideY(Player player, int newX, int newY) {
    if (player.y < newY) {
      //Moving down. We want to check every block that is under the sprite. This is from the first 
      //             Pixel (newX) to the last (newX + (Sprite.Width - 1))
      Tile.TileType tile1 = getTile(newX, newY + Sprite.IMAGE_HEIGHT + 16);
      Tile.TileType tile2 = getTile(newX + Sprite.IMAGE_WIDTH - 1, newY + Sprite.IMAGE_HEIGHT + 16);
      
      if( tile1 != Tile.TileType.NONSOLID || tile2 != Tile.TileType.NONSOLID){
        //TODO this might need some work once others are introduced, but making sure 
        //     it isn't a situation where the player is pressing down to sink through
        if((tile1 == Tile.TileType.SOLID_ON_TOP || tile1 == Tile.TileType.NONSOLID) &&
           (tile2 == Tile.TileType.SOLID_ON_TOP || tile2 == Tile.TileType.NONSOLID) &&
           (player.physics.playerControl.isDown())) {//either pushing down or already did and working through the block
          player.physics.startFalling();
        }
        else{ 
          newY = getY() - Sprite.IMAGE_HEIGHT;
          player.physics.collideWithFloor();
        }
      }
    }
    else {
      //Moving up
      if(getTile(newX, newY) == Tile.TileType.SOLID ||
         getTile(newX + Sprite.IMAGE_WIDTH - 1, newY) == Tile.TileType.SOLID){
        newY += Tile.SIZE - newY % Tile.SIZE;
        player.physics.collideWithCeiling();
      }
    }
    
    return newY;
  }
}
