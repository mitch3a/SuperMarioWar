package smw.world.blocks;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import smw.Collidable;
import smw.Drawable;
import smw.world.TileSheet;
import smw.world.TileSheetManager;

public class SolidBlock extends Collidable.Solid implements Drawable{
  TileSheet tileSheet = null;
  
  public int[] settings = new int[26]; // TODO - I think this is mapped to NUM_POWERUPS

  //The x,y pixels that map to the block image in the blocks.png TileSheet
  int tileSheetX, tileSheetY;
  
  /**
   * This is a block that, by default, will not let anything
   * pass through it. 
   */
  public SolidBlock(int x, int y){
    this(x, y, "blocks.png");
  }
  
  public SolidBlock(int x, int y, String blockSheet) {
    super(x, y);
    
    //These default to Brick. The parent class should reassign these
    tileSheetX = 0;
    tileSheetY = 0;
        
    tileSheet = TileSheetManager.getInstance().getTileSheet(blockSheet);
  }

  @Override
  public void draw(Graphics2D g, ImageObserver io) {
    g.drawImage(getImage(), x, y, io);
  }
  
  BufferedImage getImage(){
    return tileSheet.getTileImg(tileSheetX, tileSheetY);
  }
}
