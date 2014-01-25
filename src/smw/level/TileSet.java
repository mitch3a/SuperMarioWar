package smw.level;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import javax.imageio.ImageIO;

import smw.gfx.Sprite.Direction;

// TODO - RPG - this will store the tile set of a given style
public class TileSet {
  
  private String name;
  private BufferedImage tileSet;
  private BufferedImage[][] tiles;
  private int tileTypeSize;
  private int width;
  private int height;
  int[] tiletypes;
  
  // TODO - RPG - currently the "name" is the folder of the tile set, not sure if this is what we really want
  public TileSet(String name) {
    try {
      // TODO - RPG - this image converting junk should probably be in a util somewhere, there is also probably a better,
      // more correct, more efficient way to do it...
      BufferedImage img = ImageIO.read(this.getClass().getClassLoader().getResource("map/tilesets/" + name + "/large.png"));
      // Must convert to a BufferedImage that allows transparency (read above uses TYPE_3BYTE_BGR).
      tileSet = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
      tileSet.getGraphics().drawImage(img, 0, 0, null);
      
      width = tileSet.getWidth() / 32; // TODO - this should be tile size, not a magic number
      height = tileSet.getHeight() / 32;
      
      /* TODO - need to remove magenta pixels, but this way doesn't work!
      // Change magenta to transparent, start by converting buffered image to pixels, then change each magenta pixel.
      int[] pixels = new int[width * height];
      tileSet.getRGB(0, 0, width, height, pixels, 0, width);
      for (int i = 0; i < pixels.length; i++) {
        if (pixels[i] == 0xffff00ff) {
          pixels[i] = -1;
        }
      }
       
      // Convert pixels back into buffered image.
      int[] bitMasks = new int[]{0xFF0000, 0xFF00, 0xFF, 0xFF000000};
      SinglePixelPackedSampleModel sm = new SinglePixelPackedSampleModel(
      DataBuffer.TYPE_INT, width, height, bitMasks);
      DataBufferInt db = new DataBufferInt(pixels, pixels.length);
      WritableRaster wr = Raster.createWritableRaster(sm, db, new Point());
      tileSet = new BufferedImage(ColorModel.getRGBdefault(), wr, false, null);
      */
      
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
      
      // First entry is number of tiles.
      int tileDataCount = buffer.getInt();
      tiletypes = new int[tileDataCount];
      for (int i = 0; i < tileDataCount; i++) {
        tiletypes[i] = buffer.getInt();
        System.out.println(tiletypes[i]);
      }
      
      buffer.clear();
      fc.close();
      f.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  int getTileType(int x, int y) {
    return tiletypes[x + y * 32];
  }
  
  // TODO - draw the specified tile... not sure if this is how we want to do it.
  public BufferedImage getTileImg(int col, int row) {
    return tileSet.getSubimage(col * 32, row * 32, 32, 32);
  }

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
  
  public int getTileTypeSize() {
    return tileTypeSize;
  }
  
  public void setTileTypeSize(short tileTypeSize) {
    this.tileTypeSize = tileTypeSize;
  }
  public int getWidth() {
    return width;
  }
  
  public void setWidth(short width) {
    this.width = width;
  }
  
  public int getHeight() {
    return height;
  }
  
  public void setHeight(short height) {
    this.height = height;
  }
  
  
}
