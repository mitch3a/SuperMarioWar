package smw.level;

import java.awt.image.BufferedImage;

// TODO - RPG - this will store the tile set for a given map
public class TileSet {

  public static final String PATH = "res/map/tilesets/";
  
  private String name;
  private BufferedImage tileSet;
  private short tileTypeSize;
  private short width;
  private short height;
  // TODO - RPG - I think this needs to be a 2D array that maps types to our tile "sprite sheet"
  int[][] tiletypes;
  
  // TODO - RPG - may want to get rid of some of these setters
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public BufferedImage getTileSet() {
    return tileSet;
  }
  
  public void setTileSet(BufferedImage tileSet) {
    this.tileSet = tileSet;
  }
  
  public short getTileTypeSize() {
    return tileTypeSize;
  }
  
  public void setTileTypeSize(short tileTypeSize) {
    this.tileTypeSize = tileTypeSize;
  }
  public short getWidth() {
    return width;
  }
  
  public void setWidth(short width) {
    this.width = width;
  }
  
  public short getHeight() {
    return height;
  }
  
  public void setHeight(short height) {
    this.height = height;
  }
  
  
}
