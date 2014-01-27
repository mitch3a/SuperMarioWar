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

/**
 * A level is made up of a 2D grid of tiles. There could be "layers" like in SNES for graphics
 * This could be accomplished by having an array of 2D tile arrays. Tiles are drawn based on 
 * a tile set that provides the sprite sheet of tile art. There is also a tile data file that
 * indicates the type of a given tile (solid, non-solid, etc.) for collision detection.
 */
public class Level {
  // TODO - keep these constants here or move them to "global" constants area?
  // mk - I think this is fine here. It's where I'd expect it. 
  public static final int MAP_WIDTH = 20;
  public static final int MAP_HEIGHT = 15;
  public static final int MAX_MAP_LAYERS = 4;
  public static final int MAX_AUTO_FILTERS = 12;
  
  public static final int TILE_SIZE = 32;
  public final int WIDTH = GameFrame.res_width / TILE_SIZE; // TODO - replace these with the above MAP_WIDTH, HEIGHT consts? 
  public final int HEIGHT = GameFrame.res_height / TILE_SIZE;
  
  // TODO - pick one or the other... tiles or mapdata
  // mk -  I prefer tiles. I would also prefer if the first index was the map layers. Just more
  //       intuitive to me and for debugging or if we ever wanted to break it, etc. 
  private TileSetTile[][][] mapData = new TileSetTile[MAP_WIDTH][MAP_HEIGHT][MAX_MAP_LAYERS];
  private Tile[][] tiles = new Tile[WIDTH][HEIGHT];
  private MapBlock[][] objectData = new MapBlock[MAP_WIDTH][MAP_HEIGHT];
  private int topTileType[][] = new int[MAP_WIDTH][MAP_HEIGHT];
  
  private String backgroundFile = new String();
  private BufferedImage backgroundImg;
  
  // TODO - this stuff isn't used yet
  private boolean[] autoFilter = new boolean[MAX_AUTO_FILTERS];
  private int tileAnimationTimer;
  private int tileAnimationFrame;
  
  // TODO - RPG - Testing my TileSet stuff. We will eventually need a "tile set manager" to handle multiple sets.
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
  
  //TODO mk - do you really want to be returning an ENUM?
  public int getTileTypeAtPx(int x, int y) {
    //TODO mk - guessing you throw this in to stop crashes... and not sure 
    //          where it is used, but would it make sense to pass back an
    //          invalid value for bad bounds? Like -1?
    /*
    
    if(x < 0 || y < 0 || x >= MAP_WIDTH || y >= MAP_HEIGHT){
      return -1
    }
    
    return tiles[col][row].getTileType();
    
     */
    int col = x / TILE_SIZE;
    if (col >= MAP_WIDTH) {
      col = MAP_WIDTH -1;
    } else if (col < 0) {
      col = 0;
    }
    
    int row = y / TILE_SIZE;
    if (row < 0) {
      row = 0;
    } else if (row >= MAP_HEIGHT) {
      row = MAP_HEIGHT - 1;    
    }
    
    //TODO - this should work, not sure why it isn't!
    //return topTileType[col][row];
    
    return tiles[col][row].getTileType();
  }
  
  //TODO mk I don't think this is used anywhere (same as method above)
  /** Gets tile type at provided tile coordinates. */
  public int getTileTypeAtTile(int x, int y) {
    return tiles[x][y].getTileType();
  }
  
  // TODO - This will eventually update interactive and animated stuff in the level.
  public void update() {
    
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
    
    // Draw the foreground using real map file data.
    final int layer = 0; // TODO - Only using one layer for now (wanted to get simplest case working first).
    for (int i = 0; i < MAP_WIDTH; i++) { //TODO mk prefer x/y or w/h. i think i've been using w/h
      for (int j = 0; j < MAP_HEIGHT; j++) {
        //TODO mk - the naming convention is kind of confusing. Altho i think i put this above.
        TileSetTile tileData = mapData[i][j][layer];
        if (tileData.ID >= 0) {
          g.drawImage(tileSet.getTileImg(tileData.col, tileData.row), i * TILE_SIZE, j * TILE_SIZE, io);
        }
      }
    } 
  }
  
  // TODO - work in progress loading existing SMW map files (using their formats)
  // Currently we only use the latest map versions 1.8+
  // Limitations - only support 1 layer, classic tile set, and only support drawing basic tiles (no animated, moving stuff,
  // etc.)
  // TODO mk maybe we should insert a list of how to approach this... my guess is:
  // 1. Finish with NMcCoy_1-3.map
  //  a. Rest of layers (w/e that means haha)
  //  b. Anything else?
  // 2. Do another 1.8 map (suggestions?)
  // 3. Cover us for all tile sets?
  // 4. Convert all older versions to 1.8 (KEEPING THE CODE IN SOURCE!!)
  // ORRRRRRR we could just put that on the wiki
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
        System.out.println("map v" + version[0] + "." + version[1] + "." + version[2] + "." + version[3]);
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
            if (Debug.LOG_TILE_TYPE_INFO) {
              System.out.println(topTileType[i][j]);
            }
          }
        }
        
        // Load background image.
        final int backgroundNameLen = buffer.getInt();
        for (int bgi = 0; bgi < backgroundNameLen; bgi++) {
          char toWrite = (char)(buffer.get());
          if (toWrite != 0)//TODO mk i kinda like brackets.
          backgroundFile += toWrite;
        }
        if (Debug.LOG_MAP_INFO) { //TODO mk AAANNNNDDD i think when i started the Debug class, i didn't use the brackets <- hypocrite
          System.out.println("background: " + backgroundFile);
        }
        backgroundImg = ImageIO.read(this.getClass().getClassLoader().getResource("map/backgrounds/" + backgroundFile)); 
      }
      
      // Close out file.
      buffer.clear(); //TODO mk I don't know much about this class, but if you're not reusing it... do you need to clear it?
      fc.close();
      f.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  //TODO mk do we call these in the right spots yet? JW
  //     ALSO... would it make sense to group these methods?
  
  // TODO - We don't support animated tiles yet (question blocks, etc.).
  private void clearAnimatedTiles() {
    
  }
  
  //TODO - I think this is for moving platforms, which we don't support yet.
  private void clearPlatforms() {
    
  }
  
}
