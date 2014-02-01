package smw.world;

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
  //public static final int NONSOLID = 0;
  //public static final int SOLID = 1;
  //public static final int SOLID_ON_TOP = 2;
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
  
  public static enum TileType{
    NONSOLID, SOLID, SOLID_ON_TOP;
    
    public static final int SIZE = 20;
  }
  
  Block block;
  int tileSheetRow;
  int tileSheetColumn;
  public int ID;//TODO make sure we use this for something
  //TileType type;
  SpecialTile specialTile;//TODO make sure we use this (see moving platforms)
  public static TileSheet tileSheet; //TODO not the best way to do this
  
  public static final int SIZE = 32;
  
  public static TileType getType(int type){
    TileType result = TileType.NONSOLID;
    
    switch(type){
      case 0: result = TileType.NONSOLID;
              break;
      case 1: result = TileType.SOLID;
              break;
      case 2: result = TileType.SOLID_ON_TOP;
              break;
    }
    
    return result;
  }
  
  /*
   * TODO mk this is a helper method to get stuff working
   * but should not be a permanent solution
   */
  public static int tileToInt(TileType tile){
    int result = 0;
    
    switch(tile){
      case NONSOLID:     result = 0;
                                  break;
      case SOLID:        result = 1;
                                  break;
      case SOLID_ON_TOP: result = 2;
                                  break; 
    }
    
    return result;
  }
  
  public static boolean isValidType(int type){
    return (type >= 0 && type < TileType.SIZE);
  }

  final int x, y;
  
  public Tile(int x, int y){
    this.x = x;
    this.y = y;
    
    block = null;
    specialTile = null;
  }
  
  int getX() {
    return x;
  }
  
  int getY() {
    return y;
  }
  
  public BufferedImage getImage() {
    return tileSheet.getTileImg(tileSheetColumn, tileSheetRow);
  }

  TileType getTileType() {
    return (specialTile != null) ? specialTile.type : TileType.NONSOLID;
  }
  
  // TODO - not sure if a tile should draw itself...
  public void draw(Graphics2D graphics, ImageObserver observer){
    BufferedImage image = getImage();
    if (image != null) {
      graphics.drawImage(image, x, y, observer);
    }
  }

  public void setBlock(int type, boolean hidden) {
    this.block = new Block(type, hidden);
  }
  
}
