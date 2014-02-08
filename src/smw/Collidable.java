package smw;

import smw.entity.Player;
import smw.ui.screen.GameFrame;
import smw.world.Tile;
import smw.world.Tile.TileType;

//TODO mk as other objects are implemented, might want to replace Player with something generic
//     ALSO this should probably be an abstract type
public class Collidable {
  //TODO mk i hate this enough to leave it as is to draw attention to itself for being awful
  //Converts the tile type into the flags that this tile carries (solid + ice + death, etc)
  static final short[] g_iTileTypeConversion = {0, 1, 2, 5, 121, 9, 17, 33, 65, 6, 21, 37, 69, 3961, 265, 529, 1057, 2113, 4096};
  protected int left;
  protected int right;
  protected int top;
  protected int bottom;
  
  public int row, column;
  
  public Collidable(short type, int x, int y) {
    this.type = Tile.getType(type);
    this.typeConversion = g_iTileTypeConversion[type];
    
    //TODO why bother taking in pixels if you're going to do this
    row = y/Tile.SIZE;
    column = x/Tile.SIZE;
    
    left   = (x - Tile.SIZE + GameFrame.res_width) % GameFrame.res_width;
    right  = (x + Tile.SIZE) % GameFrame.res_width;
    top    = (y - Tile.SIZE) % GameFrame.res_height;
    bottom = (y + Tile.SIZE) % GameFrame.res_height;
  }

  short typeConversion;
  public Tile.TileType type;
  
  public int collideWithLeft(Player player, int newX){
    if (type == TileType.SOLID){
      player.physics.collideWithWall();
      return left;
    }

    return newX;
  }
  
  public int collideWithRight(Player player, int newX){
    if (type == TileType.SOLID){
      player.physics.collideWithWall();
      return right;
    }
  
    return newX;
  }
  
  public int collideWithTop(Player player, int newY){
    if (type == TileType.SOLID) {
      player.physics.collideWithFloor();
      return top;
    }
    else if(type == TileType.SOLID_ON_TOP){
      if(!player.pushedDown && player.physics.playerControl.isDown()) {
        // If this is the first time we reached this then the player pushed down the first time to fall through.
        // Set the falling through flags and height.
        if (!player.isFallingThrough) {
          player.isFallingThrough = true;
          player.fallHeight = player.y;
          player.pushedDown = true;
        }
      }
      else{
        player.physics.collideWithFloor();
        return top;
      }
    }
    
    return newY;
  }
  
  public int collideWithBottom(Player player, int newY){
    if (type == TileType.SOLID) {
      player.physics.collideWithCeiling();
      return bottom;
    }
    
    return newY;
  }
}
