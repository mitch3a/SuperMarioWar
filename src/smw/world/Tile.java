package smw.world;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import smw.Drawable;

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
    if (hasImage) {
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
      graphics.drawImage(tileSheet.getTileImg(tileSheetX, tileSheetY), overrideX, overrideY, observer);
    }
  }
  
  public boolean hasImage() {
    return hasImage;
  }
  
  BufferedImage getImage(){
    return (hasImage) ? tileSheet.getTileImg(tileSheetX, tileSheetY) : null;
  }
}
