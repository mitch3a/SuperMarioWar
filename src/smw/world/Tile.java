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
  
  /** Tile types specified by the original map files. */
  public static enum TileType {
    NONSOLID, SOLID, SOLID_ON_TOP, ICE, DEATH, DEATH_ON_TOP, DEATH_ON_BOTTOM, DEATH_ON_LEFT, DEATH_ON_RIGHT,
    ICE_ON_TOP, ICE_DEATH_ON_BOTTOM, ICE_DEATH_ON_LEFT, ICE_DEATH_ON_RIGHT, 
    SUPER_DEATH, SUPER_DEATH_TOP, SUPER_DEATH_BOTTOM, SUPER_DEATH_LEFT, SUPER_DEATH_RIGHT, PLAYER_DEATH, GAP;
    
    public static final int SIZE = 20;
  }
  
  Block block;
  int tileSheetRow;
  int tileSheetColumn;
  public int ID;//TODO make sure we use this for something
  SpecialTile specialTile;//TODO make sure we use this (see moving platforms)
  short[] settings = new short[1];//TODO figure out what this is for
  
  public static TileSheet tileSheet; //TODO not the best way to do this
  
  public static final int SIZE = 32;
  
  /** Converts the provided integer value to the corresponding tile type enum literal. */
  public static TileType getType(int type) {
    TileType result = TileType.NONSOLID;
    switch(type) {
      case 0: result = TileType.NONSOLID; break;
      case 1: result = TileType.SOLID; break;
      case 2: result = TileType.SOLID_ON_TOP; break;
      case 3: result = TileType.ICE; break;
      case 4: result = TileType.DEATH; break;
      case 5: result = TileType.DEATH_ON_TOP; break;
      case 6: result = TileType.DEATH_ON_BOTTOM; break;
      case 7: result = TileType.DEATH_ON_LEFT; break;
      case 8: result = TileType.DEATH_ON_RIGHT; break;
      case 9: result = TileType.ICE_ON_TOP; break;
      case 10: result = TileType.ICE_DEATH_ON_BOTTOM; break;
      case 11: result = TileType.ICE_DEATH_ON_LEFT; break;
      case 12: result = TileType.ICE_DEATH_ON_RIGHT; break;
      case 13: result = TileType.SUPER_DEATH; break;
      case 14: result = TileType.SUPER_DEATH_TOP; break;
      case 15: result = TileType.SUPER_DEATH_BOTTOM; break;
      case 16: result = TileType.SUPER_DEATH_LEFT; break;
      case 17: result = TileType.SUPER_DEATH_RIGHT; break;
      case 18: result = TileType.PLAYER_DEATH; break;
      case 19: result = TileType.GAP; break;
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
