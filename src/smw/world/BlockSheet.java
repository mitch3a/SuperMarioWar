package smw.world;

import java.awt.Image;

import smw.world.Structures.Block;

public class BlockSheet extends TileSheet{
  final int NUM_COLUMNS = 15;
  private static BlockSheet instance;

  static {
      instance = new BlockSheet();
  }

  private BlockSheet() { 
    super(true);
  }    

  public static BlockSheet getInstance() {
      return instance;
  }

  public Image getTileImg(Block b) {
    int type = b.type;
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
  
  /*
  public Image getTileImg(int type, boolean hidden) {
    if(type < 0){//TODO check not TOO big
      return null;
    }
    int column = type%NUM_COLUMNS;
    int row = type/NUM_COLUMNS;
    
    if (hidden) {
    switch (type) {
    case 11:
    case 12:
    case 13:
    case 14:
      
      break;
    }
    return image.getSubimage(column * Tile.SIZE, row * Tile.SIZE, Tile.SIZE, Tile.SIZE);
  }*/
}
