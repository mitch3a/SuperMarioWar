package smw.world;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import javax.imageio.ImageIO;

import smw.settings.Debug;
import smw.ui.screen.GameFrame;
import smw.world.MovingPlatform.MovingPlatform;
import smw.world.MovingPlatform.Path;
import smw.world.MovingPlatform.StraightContinuousPath;
import smw.world.MovingPlatform.StraightSegmentPath;
import smw.world.Tile.TileType;

public class World {
  
  public final int MAX_LAYERS = 4;

  public final int MAP_WIDTH;
  public final int MAP_HEIGHT;
  
  BufferedImage backgroundImg;
  
  Block[]  blocks;
  Warp[][]   warps;
  Item[]   items;
  Hazard[] hazards;
  MovingPlatform[] movingPlatforms;
  
  Tile[][]   backgroundTiles;
  TileSheet tileSheet = new TileSheet("Classic");
  
  //This is a hardcoded value only used once. If you know why it  is 12, replace this comment with the reason
  boolean[] autoFilter = new boolean[12];
  int NUM_SPAWN_AREA_TYPES = 6;
  final boolean[][][] nospawn;
  int musicCategoryID;
  
  public World(String worldName){
    //TODO I DONT LIKE THIS...
    Tile.tileSheet = tileSheet;
    
    MAP_WIDTH = GameFrame.res_width / Tile.SIZE;
    MAP_HEIGHT = GameFrame.res_height / Tile.SIZE;
    
    backgroundTiles = new Tile[MAP_WIDTH][MAP_HEIGHT];
    warps = new Warp[MAP_WIDTH][MAP_HEIGHT];
    nospawn = new boolean[NUM_SPAWN_AREA_TYPES][MAP_WIDTH][MAP_HEIGHT];
    
    try {
      WorldBuffer buffer = new WorldBuffer(worldName);

      int version = buffer.getVersion();
      
      // For now only support latest world files (1.8+)
      if (version >= 1800) {
        
        for (int i = 0; i < autoFilter.length; i++) {
          autoFilter[i] = buffer.getAutoFilter();
        }
        
        buffer.getInt(); // unused 32 bits after auto filter section
                
        ///////////////////////////////////////////////////////////////
        // Load tile set information.
        ///////////////////////////////////////////////////////////////
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
          
          translation[i].name = buffer.getString();
          
          if (Debug.LOG_WORLD_INFO) {
            System.out.println("tileset: " + translation[i].name);
          }
        }
        
        ///////////////////////////////////////////////////////////////
        // Load background tiles
        ///////////////////////////////////////////////////////////////
        for (int h = 0; h < MAP_HEIGHT; ++h) {
          for (int w = 0; w < MAP_WIDTH; w++) {
            //Right now we are only using one layer, but still need to read them
            for (int k = 0; k < MAX_LAYERS; k++) {
              Tile tile = buffer.getTile(w, h);
           
              //For now, only one layer. 
              if(k == 0){                
                backgroundTiles[w][h] = tile;
              }
            }
            
            int type = (int)(buffer.getByte());
            boolean hidden = buffer.getBoolean();
            
            if(Tile.isValidType(type)){
              backgroundTiles[w][h].setBlock(type, hidden);
            }
          }
        }
        
        ///////////////////////////////////////////////////////////////
        // Load background image
        ///////////////////////////////////////////////////////////////
        String backgroundFile = buffer.getString();

        if (Debug.LOG_WORLD_INFO) {
          System.out.println("background: " + backgroundFile);
        }
        
        backgroundImg = ImageIO.read(this.getClass().getClassLoader().getResource("map/backgrounds/" + backgroundFile)); 
        
        ///////////////////////////////////////////////////////////////
        //TODO mk not using this so not storing it for now...
        ///////////////////////////////////////////////////////////////
        int[] switches = new int[4];
        for(int switchIndex = 0 ; switchIndex < switches.length ; ++switchIndex){
          switches[switchIndex] = buffer.getInt();
        }
        
        ///////////////////////////////////////////////////////////////
        //Load world objects
        ///////////////////////////////////////////////////////////////
        loadPlatforms(buffer, version);
        loadItems(buffer);
        loadHazards(buffer);

        ///////////////////////////////////////////////////////////////
        //Load Misc.
        ///////////////////////////////////////////////////////////////
        short[] eyeCandy = new short[3];
        
        if(version > 1802){
          eyeCandy[0] = (short)buffer.getInt();
          eyeCandy[1] = (short)buffer.getInt();
        }
        
        eyeCandy[2] = (short)buffer.getInt();
        
        musicCategoryID = buffer.getInt();

        ///////////////////////////////////////////////////////////////
        //Load World specifics. Tile type overrides, warps and spawns
        ///////////////////////////////////////////////////////////////
        for(int h = 0; h < MAP_HEIGHT; ++h) {
          for(int w = 0; w < MAP_WIDTH; ++w) {    
            
            backgroundTiles[w][h].specialTile = buffer.getSpecialTile();  
            warps[w][h] = buffer.getWarp();

            for(short sType = 0; sType < NUM_SPAWN_AREA_TYPES; sType++){
              nospawn[sType][w][h] = buffer.getBoolean();
            }
          }
        }
      }//end if (version >= 1800)
      
      // Close out file.
      buffer.close();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  /**
   * Load moving platforms
   * This method expects the buffer to be at the hazards position
   */
  void loadPlatforms(WorldBuffer buffer, int version){
    int numPlatforms = buffer.getInt();
    movingPlatforms = new MovingPlatform[numPlatforms];
    
    for(int platformIndex = 0 ; platformIndex < numPlatforms ; ++platformIndex){
      int width = buffer.getInt();
      int height = buffer.getInt();
      
      Tile[][] platformTiles = new Tile[width][height];
      
      //////////////////////////////////////////////
      //Setup the tiles and their types
      for(int w = 0 ; w < width  ; ++w){
        for(int h = 0  ; h < height ; ++h){          
          if(version >= 1800){
            Tile tempTile = buffer.getTile(w, h);
            tempTile.specialTile = buffer.getSpecialTile();
            platformTiles[w][h] = tempTile;
          }
          else{
            //TODO other version code
          }
        }
      }
      
      short drawLayer = (version >= 1801) ? (short)buffer.getInt() : 2;
      int pathType    = (version >= 1800) ? buffer.getInt() : 0;
      
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
        int xOffset = (int)(width*Tile.SIZE)/2;
        int yOffset = (int)(height*Tile.SIZE)/2;

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
        int xOffset = (int)(width*Tile.SIZE)/2;
        int yOffset = (int)(height*Tile.SIZE)/2;
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

      movingPlatforms[platformIndex] = new MovingPlatform(platformTiles, path);
      //platforms[platformIndex] = new MovingPlatform(tiles, types, width, height, drawLayer, path, fPreview);
    }
  }  
  
  /**
   * Load items (like carry-able spikes and springs)
   * This method expects the buffer to be at the hazards position
   */
  void loadItems(WorldBuffer buffer) {
    
    int numMapItems = buffer.getInt();
    items = new Item[numMapItems];
    
    for(int i = 0; i < numMapItems; ++i)
    {
      items[i] = buffer.getItem();
    }
  }
  
  /**
   * Load hazards (like fireball strings, rotodiscs, pirhana plants).
   * This method expects the buffer to be at the hazards position
   */
  void loadHazards(WorldBuffer buffer) {
    int numMapHazards = buffer.getInt();
    hazards = new Hazard[numMapHazards];

    for(short i = 0; i < numMapHazards; ++i){
      hazards[i] = buffer.getHazard();
    }
  }


  public void draw(Graphics2D g, ImageObserver io) {
    // Draw the background.
    g.drawImage(backgroundImg, 0, 0, io);

    // Draw the foreground using real world file data.
    for(Tile[] tiles : backgroundTiles){
      for(Tile tile : tiles){
        tile.draw(g, io);
      }
    }

    if(movingPlatforms != null && movingPlatforms.length > 0){
      for(MovingPlatform platform : movingPlatforms){
        platform.draw(g, io);
      }
    }
  }


  public void update() {
    if(movingPlatforms != null && movingPlatforms.length > 0){
      for(MovingPlatform platform : movingPlatforms){
        platform.move(1);
      }
    }
  }

/**
 * This method will return the tile type at the given pixel
 * 
 * @param x pixel on the x axis
 * @param y pixel on the y axis
 * @return the tile type at the given pixel
 */
public TileType getTileType(int x, int y) {
    int column = x / Tile.SIZE;
    if (column >= MAP_WIDTH) {
      column = MAP_WIDTH -1;
    } else if (column < 0) {
      column = 0;
    }
    
    int row = y / Tile.SIZE;
    if (row < 0) {
      row = 0;
    } else if (row >= MAP_HEIGHT) {
      row = MAP_HEIGHT - 1;    
    }
    
    return backgroundTiles[column][row].getTileType();
  }
}
