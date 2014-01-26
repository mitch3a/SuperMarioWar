package smw.level;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import javax.imageio.ImageIO;

import smw.gfx.Palette;
import smw.settings.Debug;

/**
 *  Stores the tile set of a given style.
 */
public class TileSet {
  
  private String name;
  private BufferedImage tileSet;
  private int tileTypeSize;
  private int width;
  private int height;
  int[] tiletypes;
  
  /** 
   * TileSet constructor.
   * @param name
   * Currently the folder of the tile set, this could change in the future.
   */
  public TileSet(String name) {
    try {
      BufferedImage img = ImageIO.read(this.getClass().getClassLoader().getResource("map/tilesets/" + name + "/large.png"));
      // Must convert to a BufferedImage that allows transparency (read above uses TYPE_3BYTE_BGR).
      tileSet = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
      tileSet.getGraphics().drawImage(img, 0, 0, null);
      
      Palette p = Palette.getInstance();
      p.implementTransparent(tileSet);      
      
      width = tileSet.getWidth() / Level.TILE_SIZE;
      height = tileSet.getHeight() / Level.TILE_SIZE;
            
      readTileTypeFile(name);
    } catch (IOException e) {
      System.out.println("Could not read spritesheet for: " + name);
      e.printStackTrace();
    }
  }
  

  /**
   * Reads the file that indicates the type of each tile on the tile sprite sheet.
   * The TLS file is a binary file that contains a series of 4 byte integers.
   * The first integer in the file indicates how many tiles there are.
   * The file is in little endian, but this is dependent on what type of machine generated it.
   * @param name
   */
  private void readTileTypeFile(String name) {       
    try {
      RandomAccessFile f = new RandomAccessFile(this.getClass().getClassLoader().getResource(
        "map/tilesets/" + name + "/tileset.tls").getPath(), "r");
      FileChannel fc = f.getChannel();
      MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
      buffer.order(ByteOrder.LITTLE_ENDIAN); // Java defaults to BIG_ENDIAN, but TLS files were probably made on a x86 PC.
      buffer.load();
      
      if (Debug.LOG_TILE_TYPE_INFO) {
        System.out.println("Tile types for set " + name);
      }
      
      // First entry is number of tiles.
      tileTypeSize = buffer.getInt();
      tiletypes = new int[tileTypeSize];
      for (int i = 0; i < tileTypeSize; i++) {
        tiletypes[i] = buffer.getInt();
        if (Debug.LOG_TILE_TYPE_INFO) {
          System.out.println(tiletypes[i]);
        }
      }
      
      buffer.clear();
      fc.close();
      f.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Returns the tile type at the provided x and y.
   * @param x col of the tile sheet
   * @param y row of the tile sheet
   * @return tile type
   */
  int getTileType(int x, int y) {
    final int index = x + y * 32;
    if (index > tileTypeSize || index < 0) {
      return 0;
    } else {
      return tiletypes[index];
    }
  }
  
  /**
   * Gets the image of a tile from the set at the provided row and col.
   * @param col col of the tile sheet
   * @param row row of the tile sheet
   * @return tile image
   */
  public BufferedImage getTileImg(int col, int row) {
    return tileSet.getSubimage(col * Level.TILE_SIZE, row * Level.TILE_SIZE, Level.TILE_SIZE, Level.TILE_SIZE);
  }  
}
