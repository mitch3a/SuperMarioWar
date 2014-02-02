package smw.world;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import smw.settings.Debug;
import smw.ui.screen.GameFrame;
import smw.world.MovingPlatform.MovingPlatform;
import smw.world.MovingPlatform.Path;
import smw.world.MovingPlatform.StraightContinuousPath;
import smw.world.MovingPlatform.StraightSegmentPath;
import smw.world.Structures.Block;
import smw.world.Structures.DrawArea;
import smw.world.Structures.FlagBaseLocation;
import smw.world.Structures.Hazard;
import smw.world.Structures.Item;
import smw.world.Structures.RaceGoalLocation;
import smw.world.Structures.SpawnArea;
import smw.world.Structures.TileSetTranslation;
import smw.world.Structures.Warp;
import smw.world.Structures.WarpExit;
import smw.world.Structures.WorldBuffer;
import smw.world.Tile.TileType;

public class World {
  
  public final int MAX_LAYERS = 4;
  public final int MAX_WARPS = 32;
  public final int NUM_SPAWN_AREA_TYPES = 6;
  public final int MAP_WIDTH;
  public final int MAP_HEIGHT;
  
  BufferedImage backgroundImg;

  Item[]     items;
  Block[]    blocks;
  Hazard[]   hazards;
  WarpExit[] warpExits;
  DrawArea[] drawAreas;
  //This is a hardcoded value only used once. If you know why it  is 12, replace this comment with the reason
  boolean[]  autoFilter = new boolean[12];
  
  //TODO this should not be visible
  public MovingPlatform[]   movingPlatforms;
  RaceGoalLocation[] raceGoalLocations;
  FlagBaseLocation[] flagBaseLocation;
  
  Tile[][]   backgroundTiles;
  Warp[][]   warps;
  SpawnArea[][] spawnAreas;  
  
  final boolean[][][] nospawn;
  // This is so that when drawing, you don't need to go through all tiles to find the ones worth drawing
  final ArrayList<Tile>  backgroundTileList = new ArrayList<Tile>();
  TileSheet tileSheet = new TileSheet("Classic");
 
  
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
        final int tileSheetCount = buffer.getInt();
        TileSetTranslation[] translation = new TileSetTranslation[tileSheetCount];
        int maxTileSetId = 0;
        int tileSetId = 0;
        
        for (int i = 0; i < tileSheetCount; i++) {
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
                
                if(tile.ID >= 0){
                  backgroundTileList.add(tile);
                }
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
        
        loadSwitches(buffer);
        
        ///////////////////////////////////////////////////////////////
        //Load Warp Exits
        ///////////////////////////////////////////////////////////////
        int maxConnections = 0;
        short numWarpExits = buffer.getShort();
        warpExits = new WarpExit[Math.min(numWarpExits, MAX_WARPS)];
        
        for(int i = 0; i < warpExits.length; i++) {
          WarpExit tempWarpExit = buffer.getWarpExit();
          maxConnections = Math.max(maxConnections, tempWarpExit.connection);
          warpExits[i] = tempWarpExit;
        }
        
        //Ignore any more warps than the max
        for(int i = 0; i < numWarpExits - MAX_WARPS; i++) {
          buffer.getWarpExit();
        }

        ///////////////////////////////////////////////////////////////
        //Load Spawn Areas
        ///////////////////////////////////////////////////////////////
        spawnAreas = new SpawnArea[NUM_SPAWN_AREA_TYPES][];
        for(int i = 0; i < NUM_SPAWN_AREA_TYPES; i++) {
          short numSpawnAreas = buffer.getShort();
          if(numSpawnAreas == 0){
            //If no spawn areas were identified, then create one big spawn area
            spawnAreas[i] = new SpawnArea[1];
            spawnAreas[i][0] = new SpawnArea();
          }
          else{
            spawnAreas[i] = new SpawnArea[numSpawnAreas];
            for(int m = 0; m < spawnAreas[i].length; m++) {
              spawnAreas[i][m] = buffer.getSpawnArea();
            }
          }
        }
        
        ///////////////////////////////////////////////////////////////
        //Load Draw Areas
        ///////////////////////////////////////////////////////////////
        short numDrawAreas = buffer.getShort();
        drawAreas = new DrawArea[numDrawAreas];
        
        for(int m = 0; m < drawAreas.length; m++) {
            drawAreas[m] = buffer.getDrawArea();
        }

        ///////////////////////////////////////////////////////////////
        //Load Extended Data Blocks
        ///////////////////////////////////////////////////////////////
        int numExtendedDataBlocks = buffer.getInt();

        for(short i = 0; i < numExtendedDataBlocks; i++) {
          short column = (short) buffer.getByte();
          short row    = (short) buffer.getByte();

          short numSettings = (short) buffer.getByte();
          backgroundTiles[column][row].settings = new short[numSettings];
          for(short setting = 0; setting < numSettings; setting++){
            backgroundTiles[column][row].settings[setting] = (short) buffer.getByte();
          }
        }

        ///////////////////////////////////////////////////////////////
        //Load mode item locations like flags and race goals
        ///////////////////////////////////////////////////////////////
        int numRaceGoals = buffer.getShort();
        raceGoalLocations = new RaceGoalLocation[numRaceGoals];
        
        for(int j = 0; j < raceGoalLocations.length; j++) {
          raceGoalLocations[j] = buffer.getRaceGoalLocation();
        }

        int numFlagBases = buffer.getShort();
        flagBaseLocation = new FlagBaseLocation[numFlagBases];
        
        for(int j = 0; j < flagBaseLocation.length; j++) {
          flagBaseLocation[j] = buffer.getFlagBaseLocation();
        }
        
      }//end if (version >= 1800)
      
      // Close out file.
      buffer.close();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    drawTilesToBackground();
  }
  
  void loadSwitches(WorldBuffer buffer) {
    //Read switch block state data
    int iNumSwitchBlockData = buffer.getInt();
    for(short iBlock = 0; iBlock < iNumSwitchBlockData; iBlock++) {
        short iCol = (short) buffer.getByte();
        short iRow = (short) buffer.getByte();

        backgroundTiles[iCol][iRow].settings[0] = (short) buffer.getByte();
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
      
      Path path = buffer.getPath(pathType, width, height);

      movingPlatforms[platformIndex] = new MovingPlatform(platformTiles, path);
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

  void drawTilesToBackground(){
    for(Tile tile : backgroundTileList){
      BufferedImage image = tile.getImage();
      
      for(int w = 0 ; w < image.getWidth() ; ++w){
        for(int h = 0 ; h < image.getHeight() ; ++h){
          int color = image.getRGB(w, h);
          if(color != Color.TRANSLUCENT){
            backgroundImg.setRGB(w + tile.x, h + tile.y, color);
          }
        }
      }
    }
  }

  public void draw(Graphics2D g, ImageObserver io) {
    // Draw the background (has background tiles in it)
    g.drawImage(backgroundImg, 0, 0, io);

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
