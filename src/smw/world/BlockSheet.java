package smw.world;

import java.awt.Image;

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

  public Image getTileImg(int type) {
    if(type < 0){//TODO check not TOO big
      return null;
    }
    int column = type%NUM_COLUMNS;
    int row = type/NUM_COLUMNS;
    return image.getSubimage(column * Tile.SIZE, row * Tile.SIZE, Tile.SIZE, Tile.SIZE);

  }
}
