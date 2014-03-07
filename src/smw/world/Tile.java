package smw.world;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import smw.Drawable;
import smw.ui.screen.GameFrame;

/* 
 * Each tile has an image the is drawn for it
 * Tiles make up the world. They are purely for 
 * drawing.
 */
public class Tile implements Drawable {

  public static boolean isValidType(int type){
    return (type >= 0 && type < 20);
  }

  public static final int SIZE = 32;
  
  int tileSheetX, tileSheetY, x, y, id;
  boolean hasImage;
  
  TileSheet tileSheet;
  
  public Tile(int x, int y, int id, int row, int col, TileSheet tileSheet){
    this.x = x;
    this.y = y;
    this.id = id;

    this.tileSheetY = row*Tile.SIZE;
    this.tileSheetX = col*Tile.SIZE;
    this.tileSheet  = tileSheet;
    
    hasImage = (tileSheet != null && tileSheet.getTileImg(tileSheetX, tileSheetY) != null);
  }
  
  //TODO this is NOT a solution
  public AnimatedTile getAnimatedTile(){
    if(id == -1){
      //TODO i don't love the math but its only done on startup for each animated 
      //     tile so really not the worst thing
      return new AnimatedTile(tileSheetY/Tile.SIZE, tileSheetX/Tile.SIZE, x, y);
    }
    
    return null;
  }

  /**
   * Draws the tile image to the provided graphics object.
   * @param graphics
   * @param observer
   */
  public void draw(Graphics2D graphics, ImageObserver observer) {
    if(hasImage){
      graphics.drawImage(tileSheet.getTileImg(tileSheetX, tileSheetY), x, y, observer);
    }
  }

  /**
   * Draws the tile image to the provided graphics object.
   * @param graphics
   * @param observer
   */
  public void draw(Graphics2D graphics, int overrideX, int overrideY, ImageObserver observer) {
    if (hasImage) {    
      //Make sure we only draw within the screen bounds
      BufferedImage image = tileSheet.getTileImg(tileSheetX, tileSheetY);
      //Right side of screen
      int width = Math.min(image.getWidth(),   GameFrame.res_width  - overrideX);
      //Bottom of screen
      int height = Math.min(image.getHeight(), GameFrame.res_height - overrideY);
      
      //Left side of screen
      if(overrideX < 0){
        if(overrideX + width < 0){
          return;
        }
        
        width += overrideX; 
        overrideX = 0;
      }
      
      //Top of screen
      if(overrideY < 0){
        if(overrideY + height < 0){
          return;
        }
        
        height += overrideY; 
        overrideY = 0;
      }
      
      graphics.drawImage(image, overrideX, overrideY, width, height, observer);
    }
  }
  
  public boolean hasImage() {
    return hasImage;
  }
  
  BufferedImage getImage(){
    return (hasImage) ? tileSheet.getTileImg(tileSheetX, tileSheetY) : null;
  }

  @Override
  public boolean shouldBeRemoved() {
    return false;
  }
}
