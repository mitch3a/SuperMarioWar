package smw.level;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import javax.imageio.ImageIO;

import smw.settings.Debug;
import smw.ui.screen.GameFrame;
import smw.level.MapBlock;
import smw.level.TileSetTile;

public class Level {
    
  /*
   * TODO - a level will be made up of tiles In the simplest sense this could
   * be a 2D array of tiles There could be "layers" like in SNES for graphics
   * This could be accomplished by having an array of 2D tile arrays
   * 
   * Need to provide a way to get tile at x,y coord. - During the game logic,
   * the game would check what type of tile the player is touching
   */

  // TODO - keep these constants here or move them to "global" constants area?
  public static final int MAP_WIDTH = 20;
  public static final int MAP_HEIGHT = 15;
  public static final int MAX_MAP_LAYERS = 4;
  public static final int MAX_AUTO_FILTERS = 12;
  
  public static final int TILE_SIZE = 32;
  public final int WIDTH = GameFrame.res_width / TILE_SIZE; // TODO - replace these with the above MAP_WITH, HEIGHT consts? 
  public final int HEIGHT = GameFrame.res_height / TILE_SIZE;
  
  // TODO - pick one or the other... tiles or mapdata
  private TileSetTile[][][] mapData = new TileSetTile[MAP_WIDTH][MAP_HEIGHT][MAX_MAP_LAYERS];
  private Tile[][] tiles = new Tile[WIDTH][HEIGHT];
  private MapBlock[][] objectData = new MapBlock[MAP_WIDTH][MAP_HEIGHT];
  private int topTileType[][] = new int[MAP_WIDTH][MAP_HEIGHT];
  
  private String backgroundFile = new String();
  private BufferedImage backgroundImg;
  
  private boolean[] autoFilter = new boolean[MAX_AUTO_FILTERS];
  private int tileAnimationTimer;
  private int tileAnimationFrame; // TODO - not sure if we will keep this
  
  // TODO - RPG - TEMP - testing my TileSet stuff...
  private TileSet tileSet = new TileSet("Classic");
  
  // TODO - this will eventually be init by a map file
  public void init() {    
    BufferedImage testTileImg = null;
    try {
      testTileImg = ImageIO.read(this.getClass().getClassLoader().getResource("test_tile.png"));
    } catch (IOException e) {
      e.printStackTrace();
    } 
    
    // TODO - delete this when collision detection based on map data works!
    // My cheese ball statically populated "map" makes a solid floor.
    for (int i = 0; i < WIDTH; i++) {
      for (int j = 0; j < HEIGHT; j++) {
          tiles[i][j] = new Tile(i * WIDTH, i * HEIGHT);
          if (j == HEIGHT - 1)
          {
            tiles[i][j].setImg(testTileImg);
            tiles[i][j].setTileType(1);
          }
      }
    }
  }
  
  /** Gets tile type at provided pixel coordinates. */
  public int getTileTypeAtPx(int x, int y) {
    int col = x / TILE_SIZE;
    int row = y / TILE_SIZE;
    if (col >= MAP_WIDTH)
      col = MAP_WIDTH -1;
    if (col < 0)
      col = 0;
    if (row < 0)
      row = 0;
    if (row >= MAP_HEIGHT)
      row = MAP_HEIGHT - 1;    
    
    //TODO - this should work, not sure why it isn't!
    //return topTileType[col][row];
    
    return tiles[col][row].getTileType();
  }
  
  /** Gets tile type at provided tile coordinates. */
  public int getTileTypeAtTile(int x, int y) {
    return tiles[x][y].getTileType();
  }
  
  public void update() {
    // TODO - not sure what would get updated here, interactive junk in the level I guess?
  }
  
  public void draw(Graphics2D g, ImageObserver io) {
    /* TODO - this was my old hard coded tile array, can delete eventually
    for (int i = 0; i < WIDTH; i++) {
      for (int j = 0; j < HEIGHT; j++) {
        BufferedImage img = tiles[i][j].getImg();
        if (img != null) {
          g.drawImage(img, i * TILE_SIZE, j * TILE_SIZE, io);
        }
      }
    }
    */
    
    // Draw the background.
    g.drawImage(backgroundImg, 0, 0, io);

    // TODO - perform draw using real map file data
    final int layer = 0; // TODO - only using one layer for now...
    for (int i = 0; i < MAP_WIDTH; i++) {
      for (int j = 0; j < MAP_HEIGHT; j++) {
        TileSetTile tileData = mapData[i][j][layer];
        if (tileData.ID >= 0) {
          // TODO - how to get image to draw from our tile set?
          g.drawImage(tileSet.getTileImg(tileData.col, tileData.row), i * TILE_SIZE, j * TILE_SIZE, io);
        }
      }
    }
    
    
  }
  
  // TODO - work in progress loading existing SMW map files (using their formats)
  public void loadMap(String name) {
    try {
      RandomAccessFile f = new RandomAccessFile(this.getClass().getClassLoader().getResource("map/" + name).getPath(), "r");
      FileChannel fc = f.getChannel();
      MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
      buffer.order(ByteOrder.LITTLE_ENDIAN); // Java defaults to BIG_ENDIAN, but MAP files were probably made on a x86 PC.
      buffer.load();
      
      // Reset map class data.
      tileAnimationTimer = 0;
      tileAnimationFrame = 0;
      clearAnimatedTiles();
      
      // Read the map version.
      int[] version = new int[4];
      for (int i = 0; i < version.length; i++) {
        version[i] = buffer.getInt();
      }
      
      if (Debug.LOG_MAP_INFO) {
        System.out.println(version[0] + " " + version[1] + " " + version[2] + " " + version[3]);
      }
      
      // For now only support latest map files (1.8+)
      if (version[0] >= 1 && version[1] >= 8) {
        
        for (int i = 0; i < MAX_AUTO_FILTERS; i++) {
          autoFilter[i] = buffer.getInt() > 0;
        }
        buffer.getInt(); // unused 32 bits after auto filter section
        
        clearPlatforms();
                
        // Load tile set information.
        final int tileSetCount = buffer.getInt();
        TileSetTranslation[] translation = new TileSetTranslation[tileSetCount];
        int maxTileSetId = 0;
        int tileSetId = 0;
        
        for (int i = 0; i < tileSetCount; i++) {
          translation[i] = new TileSetTranslation();
          tileSetId = buffer.getInt();
          translation[i].ID = tileSetId;
          
          if (tileSetId > maxTileSetId) {
            maxTileSetId = tileSetId;
          }
          
          final int ID_LENGTH = buffer.getInt();
          for (int j = 0; j < ID_LENGTH; j++) {
            translation[i].name += (char)(buffer.get());
          }
          if (Debug.LOG_MAP_INFO) {
            System.out.println("tileset: " + translation[i].name);
          }
        }
        
        // TODO - translate tile set to get from tile set manager... this is all very dir / naming dependent.
        // For now we're only using this one map, that uses the 'classic' tileset, so we shouldn't need to
        // do anything extra... yet!
        
        // Load map data.
        for (int j = 0; j < MAP_HEIGHT; j++) {
          for (int i = 0; i < MAP_WIDTH; i++) {
            for (int k = 0; k < MAX_MAP_LAYERS; k++) {
              mapData[i][j][k] = new TileSetTile();
              mapData[i][j][k].ID = (int)(buffer.get());
              mapData[i][j][k].col = (int)(buffer.get());
              mapData[i][j][k].row = (int)(buffer.get());
            }
            objectData[i][j] = new MapBlock();
            objectData[i][j].type = (int)(buffer.get());
            objectData[i][j].hidden = buffer.get() != 0;
            
            topTileType[i][j] = this.tileSet.getTileType(mapData[i][j][0].col, mapData[i][j][0].row); // TODO - I think this will work...
            System.out.println(topTileType[i][j]);
          }
        }
        
        // Load background image.
        final int backgroundNameLen = buffer.getInt();
        for (int bgi = 0; bgi < backgroundNameLen; bgi++) {
          char toWrite = (char)(buffer.get());
          if (toWrite != 0)
          backgroundFile += toWrite;
        }
        if (Debug.LOG_MAP_INFO) {
          System.out.println("background: " + backgroundFile);
        }
        backgroundImg = ImageIO.read(this.getClass().getClassLoader().getResource("map/backgrounds/" + backgroundFile)); 
      }
      
      // Close out file.
      buffer.clear();
      fc.close();
      f.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  // TODO - not sure if we will keep this
  private void clearAnimatedTiles() {
    
  }
  
//TODO - not sure if we will keep this
  private void clearPlatforms() {
    
  }
  
}
