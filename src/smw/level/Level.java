package smw.level;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
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
import smw.level.MovingPlatform.MovingPlatform;
import smw.level.MovingPlatform.StraightContinuousPath;
import smw.level.MovingPlatform.StraightSegmentPath;

/**
 * A level is made up of a 2D grid of tiles. There could be "layers" like in SNES for graphics
 * This could be accomplished by having an array of 2D tile arrays. Tiles are drawn based on 
 * a tile set that provides the sprite sheet of tile art. There is also a tile data file that
 * indicates the type of a given tile (solid, non-solid, etc.) for collision detection.
 */
public class Level {
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
  
  // TODO - this stuff isn't used yet
  private boolean[] autoFilter = new boolean[MAX_AUTO_FILTERS];
  private int tileAnimationTimer;
  private int tileAnimationFrame;
  
  //TODO mk i hate this enough to leave it as is to draw attention to itself for being awful
  //Converts the tile type into the flags that this tile carries (solid + ice + death, etc)
  short[] g_iTileTypeConversion = {0, 1, 2, 5, 121, 9, 17, 33, 65, 6, 21, 37, 69, 3961, 265, 529, 1057, 2113, 4096};

  MovingPlatform[] platforms;
  
  // TODO - RPG - Testing my TileSet stuff. We will eventually need a "tile set manager" to handle multiple sets.
  public static TileSet tileSet = new TileSet("Classic");
  
  // TODO - this will eventually be init by a map file
  public void init() {    
    BufferedImage testTileImg = null;
    try {
      testTileImg = ImageIO.read(this.getClass().getClassLoader().getResource("test_tile.png"));
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
  
  /** Gets tile type at provided pixel coordinates. */
  public int getTileTypeAtPx(int x, int y) {
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
    
    return topTileType[col][row];
  }
  
  // TODO - This will eventually update interactive and animated stuff in the level.
  public void update() {
    if(platforms != null){
      for(int i = 0 ; i < platforms.length ; ++i){
        platforms[i].move(1);
      }
    }
  }
  
  public void draw(Graphics2D g, ImageObserver io) {
    // Draw the background.
    g.drawImage(backgroundImg, 0, 0, io);

    // Draw the foreground using real map file data.
    final int layer = 0; // TODO - Only using one layer for now (wanted to get simplest case working first).
    for (int i = 0; i < MAP_WIDTH; i++) {
      for (int j = 0; j < MAP_HEIGHT; j++) {
        TileSetTile tileData = mapData[i][j][layer];
        if (tileData.ID >= 0) {
          g.drawImage(tileSet.getTileImg(tileData.col, tileData.row), i * TILE_SIZE, j * TILE_SIZE, io);
        }
        
        MapBlock mapBlock = objectData[i][j];
        if(mapBlock.type != -1){ //TODO mk probably just make sure its within bounds...
          g.drawImage(tileSet.getTileImg(27, 15), i * TILE_SIZE, j * TILE_SIZE, io); //TODO mk yes i cheated. Not sure where the row/col is supposed to come from
        }
      }
    }
    
    if(platforms != null){
      for(int i = 0 ; i < platforms.length ; ++i){
        platforms[i].draw(g, io);
      }
    }
  }
  
  public static void printAllMapsAndVersions(){
    File folder = new File(Level.class.getClassLoader().getResource("map/").getFile());
    File[] listOfFiles = folder.listFiles();
    for(File f : listOfFiles){
      String name = f.getName();
      if(name.endsWith(".map")){
        System.out.println(name + ": " + getMapVersion(name));
      }
    }
  }
  
  public static String getMapVersion(String name){
    String result = "";
    try {
      int version = 0;
      RandomAccessFile f = new RandomAccessFile(Level.class.getClassLoader().getResource("map/" + name).getPath().replaceAll("%20", " "), "r");
      FileChannel fc = f.getChannel();
      MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
      buffer.order(ByteOrder.LITTLE_ENDIAN); // Java defaults to BIG_ENDIAN, but MAP files were probably made on a x86 PC.
      buffer.load();
      
      // Read the map version.
      for (int i = 0; i < 4; i++) {
        version = 10*version + buffer.getInt();
      }
      
      result += "Version: " + version;

      // For now only support latest map files (1.8+)
      if (version >= 1800) {

        for (int i = 0; i < MAX_AUTO_FILTERS; i++) {
          buffer.getInt();
        }
        
        buffer.getInt(); // unused 32 bits after auto filter section
                
        // Load tile set information.
        final int tileSetCount = buffer.getInt();
        TileSetTranslation[] translation = new TileSetTranslation[tileSetCount];
        
        result += " with tilesets: ";
        for (int i = 0; i < tileSetCount; i++) {
          translation[i] = new TileSetTranslation();
          buffer.getInt(); //Not used

          
          final int ID_LENGTH = buffer.getInt();
          for (int j = 0; j < ID_LENGTH; j++) {
            translation[i].name += (char)(buffer.get());
          }
          
          result += translation[i].name + ", ";
        }
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    
    return result;
  }
  
  // TODO - work in progress loading existing SMW map files (using their formats)
  // Currently we only use the latest map versions 1.8+
  // Limitations - only support 1 layer, classic tile set, and only support drawing basic tiles (no animated, moving stuff,
  // etc.)
  public void loadMap(String name) {
    try {
      RandomAccessFile f = new RandomAccessFile(this.getClass().getClassLoader().getResource("map/" + name).getPath().replaceAll("%20", " "), "r");
      FileChannel fc = f.getChannel();
      MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
      buffer.order(ByteOrder.LITTLE_ENDIAN); // Java defaults to BIG_ENDIAN, but MAP files were probably made on a x86 PC.
      buffer.load();
      
      // Reset map class data.
      tileAnimationTimer = 0;
      tileAnimationFrame = 0;
      clearAnimatedTiles();
      
      // Read the map version.
      int version = 0;
      for (int i = 0; i < 4; i++) {
        version = 10*version + buffer.getInt();
      }
      
      if (Debug.LOG_MAP_INFO) {
        System.out.println("map v" + version);
      }
      
      // For now only support latest map files (1.8+)
      if (version >= 1800) {

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
        for (int h = 0; h < MAP_HEIGHT; ++h) {
          for (int w = 0; w < MAP_WIDTH; w++) {
            topTileType[w][h] = 0;
            for (int k = 0; k < MAX_MAP_LAYERS; k++) {
              mapData[w][h][k] = readTile(buffer);
              
              if(mapData[w][h][k].ID >= 0){
            	  //TODO mk what if there are multiple per k?
                topTileType[w][h] = tileSet.getTileType(mapData[w][h][k].col, mapData[w][h][k].row);
              }
            }
            objectData[w][h] = new MapBlock();
            objectData[w][h].type = (int)(buffer.get());
            objectData[w][h].hidden = buffer.get() != 0;
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
        
        //TODO mk not using this so not storing it for now...
        int[] switches = new int[4];
        for(int switchIndex = 0 ; switchIndex < switches.length ; ++switchIndex){
          switches[switchIndex] = buffer.getInt();
        }
        
        loadPlatforms(buffer, false, version, maxTileSetId);//TODO got rid of the other params... they were for error checking...hm.....
        
        //Load map items (like carryable spikes and springs)
        int numMapItems = buffer.getInt();
        MapItem[] mapItems = new MapItem[numMapItems];
        
        for(int i = 0; i < numMapItems; ++i)
        {
          MapItem item = new MapItem();
          item.type = buffer.getInt();
          item.x    = buffer.getInt();
          item.y    = buffer.getInt();
          
          mapItems[i] = item;
        }

        //Load map hazards (like fireball strings, rotodiscs, pirhana plants)
        int numMapHazards = buffer.getInt();
        MapHazard[] mapHazards = new MapHazard[numMapHazards];

        for(short i = 0; i < numMapHazards; ++i)
        {
          MapHazard hazard = new MapHazard();
          hazard.type = (short)buffer.getInt();
          hazard.x    = (short)buffer.getInt();
          hazard.y    = (short)buffer.getInt();

          for(short j = 0; j < MapHazard.NUMMAPHAZARDPARAMS; j++)
            hazard.iparam[j] = (short)buffer.getInt();
          
          for(short j = 0; j < MapHazard.NUMMAPHAZARDPARAMS; j++)
            hazard.dparam[j] = buffer.getFloat();
          
          mapHazards[i] = hazard;
        }

        short[] eyeCandy = new short[3];
        
        if(version > 1802){
          eyeCandy[0] = (short)buffer.getInt();
          eyeCandy[1] = (short)buffer.getInt();
        }
        
        eyeCandy[2] = (short)buffer.getInt();
        
        int musicCategoryID = buffer.getInt();
      }
      
      // Close out file.
      buffer.clear();
      fc.close();
      f.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  TileSetTile readTile(MappedByteBuffer buffer){
    TileSetTile tile = new TileSetTile();
    
    tile.ID = (int)(buffer.get());
    tile.col = (int)(buffer.get());
    tile.row = (int)(buffer.get());
    tile.type = tileSet.getTileType(tile.col, tile.row); //TODO mk this replaced the ID I believe but didnt feel right for now
    
    return tile;
  }
  
  private void loadPlatforms(MappedByteBuffer buffer, boolean fPreview, int version, int maxTileSetId){
    int numPlatforms = buffer.getInt();
    platforms = new MovingPlatform[numPlatforms];
    
    for(int platformIndex = 0 ; platformIndex < numPlatforms ; ++platformIndex){
      int width = buffer.getInt();
      int height = buffer.getInt();
      
      TileSetTile[][] tiles = new TileSetTile[width][height];
      TileType[][] types = new TileType[width][height]; 
      
      //Setup the tiles and their types
      for(int col = 0 ; col < width  ; ++col){
        for(int row = 0  ; row < height ; ++row){
          
          if(version >= 1800){
            //TODO mk this is just for version 1800
            tiles[col][row] = readTile(buffer);
            short type =  (short)buffer.getInt();
            
            //TODO mk this code is verbatum(ish) and is terrible
            TileType tileType = new TileType();
            if(type >= 0 && type < g_iTileTypeConversion.length){
              tileType.type = type;
              tileType.typeConversion = g_iTileTypeConversion[type];
            }
            else{
              tileType.type = 0;
              tileType.typeConversion = g_iTileTypeConversion[0];
            }
            types[col][row] = tileType;
          }
          else{
            //TODO other version code
          }
        }
      }
      
      short drawLayer = getDrawLayer(version, buffer);
      int pathType = getPathType(version, buffer);
      
      Path path = null;
      
      if(pathType == 0) //segment path
      {
        float fStartX = buffer.getFloat();
        float fStartY = buffer.getFloat();
        float fEndX = buffer.getFloat();
        float fEndY = buffer.getFloat();
        float fVelocity = buffer.getFloat();
        
        ////////////////////////////////////////////////////////////////////////////////////
        //For some reason, original maps use a start/end based on the center of the object.
        //In the case of a straight path, it makes no sense so correcting it here
        int xOffset = (int)(width*Level.TILE_SIZE)/2;
        int yOffset = (int)(height*Level.TILE_SIZE)/2;

        path = new StraightSegmentPath(fVelocity, fStartX - xOffset, fStartY - yOffset, fEndX - xOffset, fEndY - yOffset);
      }
      else if(pathType == 1) //continuous path
      {
        float fStartX = buffer.getFloat();
        float fStartY = buffer.getFloat();
        float fAngle = buffer.getFloat();
        float fVelocity = buffer.getFloat();

        ////////////////////////////////////////////////////////////////////////////////////
        //For some reason, original maps use a start/end based on the center of the object.
        //In the case of a straight path, it makes no sense so correcting it here
        int xOffset = (int)(width*Level.TILE_SIZE)/2;
        int yOffset = (int)(height*Level.TILE_SIZE)/2;
        path = new StraightContinuousPath(fVelocity, fStartX - xOffset, fStartY - yOffset, fAngle);
      }
      else if(pathType == 2) //elliptical path
      {
        float fRadiusX = buffer.getFloat();
        float fRadiusY = buffer.getFloat();
        float fCenterX = buffer.getFloat();
        float fCenterY = buffer.getFloat();
        float fAngle   = buffer.getFloat();
        float fVelocity = buffer.getFloat();

        //NOTE: from above, straight paths keep an X, Y according to top right corner. For this, might be
        //      better off doing it by center
        //TODO path = new EllipsePath(fVelocity, fAngle, fRadiusX, fRadiusY, fCenterX, fCenterY, fPreview);
      }

      platforms[platformIndex] = new MovingPlatform(tiles, path);
      //platforms[platformIndex] = new MovingPlatform(tiles, types, width, height, drawLayer, path, fPreview);
    }
  }  
  
  short getDrawLayer(int version, MappedByteBuffer buffer){
    return (version >= 1801) ? (short)buffer.getInt() : 2;
  }
  
  int getPathType(int version, MappedByteBuffer buffer){
    return (version >= 1800) ? buffer.getInt() : 0;
  }
 
  // TODO - We don't support animated tiles yet (question blocks, etc.).
  private void clearAnimatedTiles() {
    
  }
  
  //TODO - I think this is for moving platforms, which we don't support yet.
  private void clearPlatforms() {
    
  }
  
}
