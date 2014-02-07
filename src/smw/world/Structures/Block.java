package smw.world.Structures;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;

import smw.Collidable;
import smw.Drawable;
import smw.Updatable;
import smw.entity.Player;
import smw.world.Tile;
import smw.world.Tile.TileType;
import smw.world.TileSheet;
import smw.world.World;

public class Block extends Collidable implements Drawable{
  public static BlockSheet blockSheet = null;
  public int colorType;
  public int[] settings = new int[26]; // TODO - I think this is mapped to NUM_POWERUPS
  public boolean hidden; //TODO could just be using baseclass Type
  public boolean switchOn; // TODO - if this block is a switch, it will be switched on... I guess
  int x, y;
  
  public Block(int colorType, int x, int y) {
    super((short)1, x, y);
    
    this.x = x;
    this.y = y;
    
    this.colorType = colorType;
    this.hidden = false;
    this.switchOn = true;
    
    if(blockSheet == null){
      blockSheet = new BlockSheet();
    }
  }
  
  //TODO mk should probably use the player size and not tile
  @Override
  public int collideWithLeft(Player player, int newX) {
    if(!hidden){
      player.physics.collideWithWall();
      return left;
    }
    
    return newX;
  }

  @Override
  public int collideWithRight(Player player, int newX) {
    if(!hidden){
      player.physics.collideWithWall();
      return right;
    }
    
    return newX;
  }

  @Override
  public int collideWithTop(Player player, int newY) {
    if(!hidden){
      player.physics.collideWithFloor();
      return top;
    }
    
    return newY;
  }

  @Override
  public int collideWithBottom(Player player, int newY) {
    //TODO this is terrible but works for now
    hidden = !hidden;
    
    for(Block block : World.blocks){
      if(block.colorType == colorType){
        block.hidden = hidden;
      }
    }
    
    if(!hidden){
      player.physics.collideWithCeiling();
      return bottom;
    }
    
    return newY;
  }

  @Override
  public void draw(Graphics2D g, ImageObserver io) {
    g.drawImage(blockSheet.getTileImg(this), x, y, io);
  }
  
  public class BlockSheet extends TileSheet{
    final int NUM_COLUMNS = 15;

    private BlockSheet() { 
      super(true);
    }  

    public Image getTileImg(Block b) {
      int type = b.colorType;
      boolean hidden = b.hidden;
      boolean switchOn = b.switchOn;
      
      if(type < 0){//TODO check not TOO big
        return null;
      }
      int column = type%NUM_COLUMNS;
      int row = type/NUM_COLUMNS;
      
      if (hidden) {
        // Even if ! blocks are hidden, they still have a tile
        switch (type) {
        case 11:
        case 12:
        case 13:
        case 14:
          row++;
          break;
        }
      } else {
        // If it's a switch type check handle ON/OFF images.
        if (!switchOn) {
          switch (type) {
          case 7:
          case 8:
          case 9:
          case 10:
            row++;
            break;
          }
        }
      }
      
      return image.getSubimage(column * Tile.SIZE, row * Tile.SIZE, Tile.SIZE, Tile.SIZE);
    }
  }
}
