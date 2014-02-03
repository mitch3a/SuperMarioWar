package smw.world.Structures;

import smw.world.Tile;

public class SpecialTile {
  
  //TODO mk i hate this enough to leave it as is to draw attention to itself for being awful
  //Converts the tile type into the flags that this tile carries (solid + ice + death, etc)
  static final short[] g_iTileTypeConversion = {0, 1, 2, 5, 121, 9, 17, 33, 65, 6, 21, 37, 69, 3961, 265, 529, 1057, 2113, 4096};
  
  public SpecialTile(short type) {
    this.type = Tile.getType(type);
    this.typeConversion = g_iTileTypeConversion[type];
  }
  
  public Tile.TileType type;
  public short typeConversion;

  public String getType() {
    return String.valueOf(type);
  }
}
