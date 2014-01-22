package smw.level;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class Tile {
  /* TODO - Each tile has an image the is drawn for it
   * Tiles make up the level (map)
   * They indicate whether or not the player can pass through them
   * There could be many different types:
   * Pass - player can pass
   * NoPass - player cannot pass (solid blocks)
   * Damage - hurt or kill the player (spikes, etc.)
   * Water - kicks in swimming physics / animation
   * Interactive - Question Blocks, Teleporters, etc.
   */
  // Tile types. 
  public static final int NONSOLID = 0;
  public static final int SOLID = 1;
  public static final int SOLID_ON_TOP = 2;
  public static final int ICE = 3;
  public static final int DEATH = 4;
  public static final int DEATH_ON_TOP = 5;
  public static final int DEATH_ON_BOTTOM = 6;
  public static final int DEATH_ON_LEFT = 7;
  public static final int DEATH_ON_RIGHT = 8;
  public static final int ICE_ON_TOP = 9;
  public static final int ICE_DEATH_ON_BOTTOM = 10;
  public static final int ICE_DEATH_ON_LEFT = 11;
  public static final int ICE_DEATH_ON_RIGHT = 12;
  public static final int SUPER_DEATH = 13;
  public static final int SUPER_DEATH_TOP = 14;
  public static final int SUPER_DEATH_BOTTOM = 15;
  public static final int SUPER_DEATH_LEFT = 16;
  public static final int SUPER_DEATH_RIGHT = 17;
  public static final int PLAYER_DEATH = 18;
  public static final int GAP = 19;
  
  private BufferedImage img;
  private int type;
  private int x;
  private int y;
  private int size;
  
  public Tile(int x, int y) {
    this.x = x;
    this.y = y;
    type = 0; // NOT SOLID
  }
    
  // TODO - these members will be set by reading in a map file probably
  public void init(BufferedImage img, int type, int x, int y) {
    this.img = img;
    this.x = x;
    this.y = y;
    this.type = type;
    size = img.getHeight();
  }
  
  int getX() {
    return x;
  }
  
  int getY() {
    return y;
  }
  
  BufferedImage getImg() {
    return img;
  }
  
  void setX(int newX) {
    x = newX;
  }
  
  void setY(int newY) {
    y = newY;
  }
  
  void setImg(BufferedImage newImg) {
    img = newImg;
  }
  
  void setTileType(int newType) {
    type = newType;
  }
  
  int getTileType() {
    return type;
  }
  
  // TODO - not sure if a tile should draw itself...
  public void draw(Graphics2D graphics, ImageObserver observer){
    if (img != null) {
      graphics.drawImage(img, x, y, observer);
    }
  }

  public int getSize() {
    return size;
  }
  
}
