package smw.world;

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
 *  Stores the tile sheet of a given style.
 */
public class TileSheet {
  
  protected BufferedImage image;
  //TODO I dont think we are using this Tile.TileType[] tiletypes;
  
  /** 
   * TileSet constructor.
   * @param name
   * Currently the folder of the tile sheet, this could change in the future.
   */
  public TileSheet(String name) {
    try {
      //TODO obviously I hate this...
      String fullName = "map/tilesheets/" + name + ((name.endsWith(".png")) ? "" : "/large.png");
      BufferedImage img = ImageIO.read(this.getClass().getClassLoader().getResource(fullName));

      // Must convert to a BufferedImage that allows transparency (read above uses TYPE_3BYTE_BGR).
      image = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
      image.getGraphics().drawImage(img, 0, 0, null);
      
      Palette p = Palette.getInstance();
      p.implementTransparent(image);      
      
      //TODO I dont think we are using this but it crashes for non "large" tilesheets readTileTypeFile(name);
    } catch (IOException e) {
      System.out.println("Could not read spritesheet for: " + name);
      e.printStackTrace();
    }
  }
  
  //TODO mk I think there is a better way... but I don't know hwat it is right now
  protected TileSheet(boolean forBlock){
    try {
      BufferedImage img = ImageIO.read(this.getClass().getClassLoader().getResource("map/tilesheets/blocks.png"));
      // Must convert to a BufferedImage that allows transparency (read above uses TYPE_3BYTE_BGR).
      image = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
      image.getGraphics().drawImage(img, 0, 0, null);
      
      Palette p = Palette.getInstance();
      p.implementTransparent(image);      
    } catch (IOException e) {
      System.out.println("Could not read spritesheet for: blocks.png");
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
  /*
  private void readTileTypeFile(String name) {       
    try {
      RandomAccessFile f = new RandomAccessFile(this.getClass().getClassLoader().getResource(
        "map/tilesheets/" + name + "/tilesheet.tls").getPath(), "r");
      FileChannel fc = f.getChannel();
      MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
      buffer.order(ByteOrder.LITTLE_ENDIAN); // Java defaults to BIG_ENDIAN, but TLS files were probably made on a x86 PC.
      buffer.load();
      
      if (Debug.LOG_TILE_TYPE_INFO) {
        System.out.println("Tile types for sheet " + name);
      }
      
      // First entry is number of tiles.
      int tileTypeSize = buffer.getInt();
      tiletypes = new Tile.TileType[tileTypeSize];
      for (int i = 0; i < tileTypeSize; i++) {
        tiletypes[i] = Tile.getType(buffer.getInt());
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
  */
  
  /**
   * Returns the tile type at the provided x and y.
   * @param x col of the tile sheet
   * @param y row of the tile sheet
   * @return tile type
   */
  /*
  Tile.TileType getTileType(int x, int y) {
    final int index = x + y * 32;
      
    if( index >= 0 && index < tiletypes.length){
      return tiletypes[index];
    }
    
    return Tile.TileType.NONSOLID;
  }
  */
  
  /**
   * Gets the image of a tile from the sheet at the provided row and col.
   * @param col col of the tile sheet
   * @param row row of the tile sheet
   * @return tile image
   */
  public BufferedImage getTileImg(int x, int y) {
    if(x < 0 || y < 0){//TODO check not TOO big
      return null;
    }
    
    return image.getSubimage(x, y, Tile.SIZE, Tile.SIZE);
  }

  public int getWidth() {
    return image.getWidth();
  }  
  
  
}
