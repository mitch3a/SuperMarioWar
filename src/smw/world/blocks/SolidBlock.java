package smw.world.blocks;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import smw.Collidable;
import smw.Drawable;
import smw.Updatable;
import smw.entity.Player;
import smw.world.Tile;
import smw.world.Tile.TileType;
import smw.world.TileSheet;
import smw.world.TileSheetManager;
import smw.world.World;

public class SolidBlock extends Collidable implements Drawable{
  TileSheet tileSheet = null;
  
  public int[] settings = new int[26]; // TODO - I think this is mapped to NUM_POWERUPS
  //The x,y pixels this blcok gets drawn too.
  int x, y;
  //The x,y pixels that map to the block image in the blocks.png TileSheet
  int subImageX, subImageY;
  
  /**
   * This is a block that, by default, will not let anything
   * pass through it. 
   */
  public SolidBlock(int x, int y) {
    super((short)1, x, y);
    
    this.x = x;
    this.y = y;
    
    //These default to Brick. The parent class should reassign these
    subImageX = 0;
    subImageY = 0;
        
    tileSheet = TileSheetManager.getInstance().getTileSheet("blocks.png");
  }
  
  @Override
  public int collideWithLeft(Player player, int newX) {
    player.physics.collideWithWall();
    return left;
  }

  @Override
  public int collideWithRight(Player player, int newX) {
    player.physics.collideWithWall();
    return right;
  }

  @Override
  public int collideWithTop(Player player, int newY) {
    player.physics.collideWithFloor();
    return top;
  }

  @Override
  public int collideWithBottom(Player player, int newY) {    
    player.physics.collideWithCeiling();
    return bottom;
  }

  @Override
  public void draw(Graphics2D g, ImageObserver io) {
    g.drawImage(getImage(), x, y, io);
  }
  
  BufferedImage getImage(){
    return tileSheet.getTileImg(subImageX, subImageY);
  }
}
