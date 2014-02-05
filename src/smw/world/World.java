package smw.world;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import smw.Game;
import smw.entity.Player;
import smw.gfx.Sprite;
import smw.settings.Debug;
import smw.ui.screen.GameFrame;
import smw.world.MovingPlatform.MovingPlatform;
import smw.world.MovingPlatform.Path;
import smw.world.Structures.Block;
import smw.world.Structures.DrawArea;
import smw.world.Structures.FlagBaseLocation;
import smw.world.Structures.Hazard;
import smw.world.Structures.Item;
import smw.world.Structures.RaceGoalLocation;
import smw.world.Structures.SpawnArea;
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
  Hazard[]   hazards;
  WarpExit[] warpExits;
  DrawArea[] drawAreas;
  //This is a hardcoded value only used once. If you know why it  is 12, replace this comment with the reason
  boolean[]  autoFilter = new boolean[12];
  
  //TODO this should not be visible
  public MovingPlatform[]   movingPlatforms;
  RaceGoalLocation[] raceGoalLocations;
  FlagBaseLocation[] flagBaseLocation;
  
  Tile[][][]   backgroundTiles;
  Warp[][]   warps;
  SpawnArea[][] spawnAreas;  
  
  final boolean[][][] nospawn;
  
  /** This is so that when drawing, you don't need to go through all tiles to find the ones worth drawing. */
  final ArrayList<Tile> frontTileList = new ArrayList<Tile>();
  
  /** Animated tile list used for updating animations during world update. */
  private ArrayList<Tile> animatedTileList = new ArrayList<Tile>();
  
  private ArrayList<Tile> redBlockList = new ArrayList<Tile>();
  
  /** The music category ID as defined in the world file. */
  private int musicCategoryID;
  
  /**
   * Constructs the world based on a random map file.
   */
  public World(){
    this(getRandomWorld());
  }
  
  /**
   * Constructs the world object based on the provided file name.
   * @param worldName
   */
  public World(String worldName){
    
    if (Debug.LOG_WORLD_INFO) {
      System.out.println("Loading world: " + worldName);
    }
    
    MAP_WIDTH = GameFrame.res_width / Tile.SIZE;
    MAP_HEIGHT = GameFrame.res_height / Tile.SIZE;
    
    backgroundTiles = new Tile[MAP_WIDTH][MAP_HEIGHT][MAX_LAYERS];
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
        final int numTileSheets = buffer.getInt();
        
        Map<Integer, TileSheet> tileSheetMap = new HashMap<Integer, TileSheet>();

        for (int i = 0; i < numTileSheets; i++) {
          int tileSheetId      = buffer.getInt();
          String tileSheetName = buffer.getString();
          
          tileSheetMap.put(tileSheetId, new TileSheet(tileSheetName));
          
          if (Debug.LOG_WORLD_INFO) {
            System.out.println("tileset: " + tileSheetName);
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
           
              tile.tileSheet = tileSheetMap.get(tile.ID);
              backgroundTiles[w][h][k] = tile;
              
              //TODO mk check which layer is in front of people. Also might want a dif list for each layer
              if(tile.ID >= -1 && k > 1){
                frontTileList.add(tile);
              }
              
              if(tile.ID == -1){
                animatedTileList.add(tile);
              }
            }
            
            int type = (int)(buffer.getByte());
            boolean hidden = buffer.getBoolean();
            
            // TODO - RPG I think this is red blocks?
            if (type == 11) {
              redBlockList.add(backgroundTiles[w][h][0]);
            }
            
            if(Tile.isValidType(type)){
              if (AnimatedBlock.isTypeAnimated(type)) {
                backgroundTiles[w][h][0].setAnimation(type);
                animatedTileList.add(backgroundTiles[w][h][0]);
              }
              backgroundTiles[w][h][0].setBlock(type, hidden);
              frontTileList.add(backgroundTiles[w][h][0]);
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
        loadPlatforms(buffer, version, tileSheetMap);
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
        
        if (Debug.LOG_WORLD_INFO) {
          System.out.println("music category: " + musicCategoryID);
        }

        ///////////////////////////////////////////////////////////////
        //Load World specifics. Tile type overrides, warps and spawns
        ///////////////////////////////////////////////////////////////
        for(int h = 0; h < MAP_HEIGHT; ++h) {
          for(int w = 0; w < MAP_WIDTH; ++w) {    
            
            backgroundTiles[w][h][0].specialTile = buffer.getSpecialTile();  
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
          backgroundTiles[column][row][0].settings = new short[numSettings];
          for(short setting = 0; setting < numSettings; setting++){
            backgroundTiles[column][row][0].settings[setting] = (short) buffer.getByte();
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
  
  /**
   * Loads switches.
   * @param buffer
   */
  void loadSwitches(WorldBuffer buffer) {
    //Read switch block state data
    int iNumSwitchBlockData = buffer.getInt();
    for(short iBlock = 0; iBlock < iNumSwitchBlockData; iBlock++) {
        short iCol = (short) buffer.getByte();
        short iRow = (short) buffer.getByte();

        backgroundTiles[iCol][iRow][0].settings[0] = (short) buffer.getByte();
    }
  }

  /**
   * Load moving platforms.
   * This method expects the buffer to be at the hazards position.
   * @param buffer
   * @param version
   * @param tileSheetMap
   */
  void loadPlatforms(WorldBuffer buffer, int version, Map<Integer, TileSheet> tileSheetMap){
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
            tempTile.tileSheet = tileSheetMap.get(tempTile.ID);
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
   * @param buffer
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
   * @param buffer
   */
  void loadHazards(WorldBuffer buffer) {
    int numMapHazards = buffer.getInt();
    hazards = new Hazard[numMapHazards];

    for(short i = 0; i < numMapHazards; ++i){
      hazards[i] = buffer.getHazard();
    }
  }

  void drawTilesToBackground(){
    //TODO can be optimized.
    for(int i = 0 ; i < backgroundTiles.length ; ++i ){
      for(int j = 0 ; j < backgroundTiles[0].length ; ++j){
        for(int k = 0; k < backgroundTiles[0][0].length ; ++k){
          Tile tile = backgroundTiles[i][j][k];
          BufferedImage image = tile.getImage();
          
          if(image != null){
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
      }
    }
  }
  
  /**
   * Performs collision handling for the X axis.
   * @param player
   * @param newX
   * @return
   */
  public int getCollisionX(Player player, int newX) {
    //This is to test the bottom of the sprite
    int lowestY = player.y + Sprite.IMAGE_HEIGHT - 1;
    
    ///////////////////////////////////////////////////////////////
    // Moving Platforms
    ///////////////////////////////////////////////////////////////
    if(Game.world.movingPlatforms != null && Game.world.movingPlatforms.length > 0){
      for(smw.world.MovingPlatform.MovingPlatform platform : Game.world.movingPlatforms){
        //Moving down. We want to check every block that is under the sprite. This is from the first 
        //             Pixel (newX) to the last (newX + (Sprite.Width - 1))
        Tile.TileType tile1 = platform.getTile(newX, player.y + Sprite.IMAGE_HEIGHT+ 1);
        Tile.TileType tile2 = platform.getTile(newX + Sprite.IMAGE_WIDTH - 1, player.y + Sprite.IMAGE_HEIGHT + 1);
        
        if(tile1 != Tile.TileType.NONSOLID || tile2 != Tile.TileType.NONSOLID){
          //TODO this might need some work once others are introduced, but making sure 
          //     it isn't a situation where the player is pressing down to sink through
          if((tile1 == Tile.TileType.SOLID_ON_TOP || tile1 == Tile.TileType.NONSOLID) &&
             (tile2 == Tile.TileType.SOLID_ON_TOP || tile2 == Tile.TileType.NONSOLID) &&
             (player.physics.playerControl.isDown())) {//either pushing down or already did and working through the block
            
          }
          else{ 
            newX += platform.getXChange();
          }
        }
      }
    }

    ///////////////////////////////////////////////////////////////
    // Objects
    ///////////////////////////////////////////////////////////////
    if (player.x != newX) {
      if (player.x > newX) {
        //Moving left
        Block block1 = getBlock(newX, player.y);
        Block block2 = getBlock(newX, lowestY); 
        if ((block1 != null && !block1.hidden) || (block2 != null && !block2.hidden)) {
          newX = newX +  Tile.SIZE - (newX % Tile.SIZE);
          player.physics.collideWithWall();
        }
      }
      else{
        //Moving right
        Block block1 = getBlock(newX + Sprite.IMAGE_WIDTH, player.y);
        Block block2 = getBlock(newX + Sprite.IMAGE_WIDTH, lowestY);
        if ((block1 != null && !block1.hidden) || (block2 != null && !block2.hidden)) {
          newX = newX - (newX % Tile.SIZE);
          player.physics.collideWithWall();
        }
      }
    } 
    
    ///////////////////////////////////////////////////////////////
    // Regular tiles
    ///////////////////////////////////////////////////////////////
    if (player.x != newX) {      
      if (player.x > newX) {
        //Moving left
      	Tile.TileType type1 = getTileType(newX, player.y);
      	Tile.TileType type2 = getTileType(newX, lowestY);
      	
        if(type1 == Tile.TileType.SOLID ||
           type2 == Tile.TileType.SOLID){
          newX = newX +  Tile.SIZE - (newX % Tile.SIZE);
          player.physics.collideWithWall();
        }
        else if(type1 == Tile.TileType.SUPER_DEATH || type1 == Tile.TileType.SUPER_DEATH_LEFT ||
                type2 == Tile.TileType.SUPER_DEATH || type2 == Tile.TileType.SUPER_DEATH_LEFT){
        	player.superDeath();
        }
      }
      else{
        //Moving right
        Tile.TileType type1 = getTileType(newX + Sprite.IMAGE_WIDTH, player.y);
        Tile.TileType type2 = getTileType(newX + Sprite.IMAGE_WIDTH, lowestY);
        
        if(type1 == Tile.TileType.SOLID ||
           type2 == Tile.TileType.SOLID){
          newX = newX - (newX % Tile.SIZE);
          player.physics.collideWithWall();
        }
        else if(type1 == Tile.TileType.SUPER_DEATH || type1 == Tile.TileType.SUPER_DEATH_LEFT ||
                type2 == Tile.TileType.SUPER_DEATH || type2 == Tile.TileType.SUPER_DEATH_LEFT){
        	player.superDeath();
        }
      }
    } 

    
    return newX;
  }
  
  /**
   * Performs collision handling for the Y axis.
   * @param player
   * @param newX
   * @param newY
   * @return
   */
  public int getCollisionY(Player player, int newX, int newY) {
    ///////////////////////////////////////////////////////////////
    // Moving Platforms
    ///////////////////////////////////////////////////////////////
    if(Game.world.movingPlatforms != null && Game.world.movingPlatforms.length > 0){
      for(smw.world.MovingPlatform.MovingPlatform platform : Game.world.movingPlatforms){
        //TODO will ever be equals?
        if (player.y < newY) {
          //Moving down. We want to check every block that is under the sprite. This is from the first 
          //             Pixel (newX) to the last (newX + (Sprite.Width - 1))
          Tile.TileType tile1 = platform.getTile(newX, newY + Sprite.IMAGE_HEIGHT + 16);
          Tile.TileType tile2 = platform.getTile(newX + Sprite.IMAGE_WIDTH - 1, newY + Sprite.IMAGE_HEIGHT + 16);
          
          if( tile1 != Tile.TileType.NONSOLID || tile2 != Tile.TileType.NONSOLID){
            //TODO this might need some work once others are introduced, but making sure 
            //     it isn't a situation where the player is pressing down to sink through
            if((tile1 == Tile.TileType.SOLID_ON_TOP || tile1 == Tile.TileType.NONSOLID) &&
               (tile2 == Tile.TileType.SOLID_ON_TOP || tile2 == Tile.TileType.NONSOLID) &&
               (player.physics.playerControl.isDown())) {//either pushing down or already did and working through the block
              player.physics.startFalling();
            }
            else{ 
              newY = platform.getY() - Sprite.IMAGE_HEIGHT;
              player.physics.collideWithFloor();
            }
          }
        }
        else {
          //Moving up
          if(platform.getTile(newX, newY) == Tile.TileType.SOLID ||
             platform.getTile(newX + Sprite.IMAGE_WIDTH - 1, newY) == Tile.TileType.SOLID){
            newY += Tile.SIZE - newY % Tile.SIZE;
            player.physics.collideWithCeiling();
          }
        }
      }
    }
    
    ///////////////////////////////////////////////////////////////
    // Objects
    ///////////////////////////////////////////////////////////////
    if (player.y != newY){
      if (player.y < newY) {        
        //Moving down. We want to check every block that is under the sprite. This is from the first 
        //             Pixel (newX) to the last (newX + (Sprite.Width - 1))
        Block block1 = Game.world.getBlock(newX, newY + Sprite.IMAGE_HEIGHT);
        Block block2 = Game.world.getBlock(newX + Sprite.IMAGE_WIDTH - 1, newY + Sprite.IMAGE_HEIGHT);
        
        if ((block1 != null && !block1.hidden) || (block2 != null && !block2.hidden)) {
          //TODO something with the blocks
          newY = newY - (newY % Tile.SIZE); // Just above the floor.
          player.physics.collideWithFloor();
        }
      }
      else {
        //Moving up
        Block block1 = Game.world.getBlock(newX, newY);
        Block block2 = Game.world.getBlock(newX + Sprite.IMAGE_WIDTH - 1, newY);
        
        // TODO consolidate
        Tile tile1 = Game.world.getTile(newX, newY);
        Tile tile2 = Game.world.getTile(newX + Sprite.IMAGE_WIDTH - 1, newY);
               
        if ((block1 != null && !block1.hidden) || (block2 != null && !block2.hidden)) {
          newY += Tile.SIZE - newY % Tile.SIZE;
          player.physics.collideWithCeiling();
          
          // TODO - RPG - trying to do block interactions
          // This is terrible, we should consolidate / clean up a lot of this code,
          // but I'm just trying to get it to work quick for now!
          
          // TODO - maybe add collide to tile then let it figure out what to do based on what type it is rather than adding more stuff to the world?
          if (tile1.block != null) {
            System.out.println(block1.type); // TODO - debug            
            switch (block1.type) {
            case 1:
              // Question Block - Stop the animation
              tile1.animatedBlock.stop();
              break;
            case 7:
              // TODO - I think this is red switch
              for (Tile t : redBlockList) {
                t.toggleHidden();
              }
              break;
              
            } 
            // TODO
            // If type is a flip block, make it flip, and allow player to pass through it
            // If type is note block, bump the player and move the block -- actually this has to happen on both X and Y axis
            // If type is switch - hide colored exclaimation blocks for that colored switch!
          }
          if (tile2.block != null) { 
            System.out.println(block2.type); // TODO - debug -- also do we need to handle block2 ever? Not sure how real game behaves
          }
        }
      }
    }
    
    ///////////////////////////////////////////////////////////////
    // Regular tiles
    ///////////////////////////////////////////////////////////////
    if (player.y != newY){
      if (player.y < newY) {
        // If the player pushed the down key check to see if it was released.
        if (player.pushedDown) {
          player.pushedDown = player.physics.playerControl.isDown();
        }
        // If falling through a solid on top block then reset flag when the player has fallen at least one tile.
        if (player.isFallingThrough && (player.y - player.fallHeight) >= Tile.SIZE) {
          player.isFallingThrough = false;
        }
        
        //Moving down. We want to check every block that is under the sprite. This is from the first 
        //             Pixel (newX) to the last (newX + (Sprite.Width - 1))
        Tile.TileType tile1 = Game.world.getTileType(newX, newY + Sprite.IMAGE_HEIGHT);
        Tile.TileType tile2 = Game.world.getTileType(newX + Sprite.IMAGE_WIDTH - 1, newY + Sprite.IMAGE_HEIGHT);
        
        if(tile1 != Tile.TileType.NONSOLID || tile2 != Tile.TileType.NONSOLID) {
          // Handle case where player is allowed to sink through a tile.
          if((tile1 == Tile.TileType.SOLID_ON_TOP || tile1 == Tile.TileType.NONSOLID) &&
             (tile2 == Tile.TileType.SOLID_ON_TOP || tile2 == Tile.TileType.NONSOLID) &&
             (!player.pushedDown && player.physics.playerControl.isDown())) {
            // If this is the first time we reached this then the player pushed down the first time to fall through.
            // Set the falling through flags and height.
            if (!player.isFallingThrough) {
              player.isFallingThrough = true;
              player.fallHeight = player.y;
              player.pushedDown = true;
            }
          }
          else if (!player.isFallingThrough && (newY < (GameFrame.res_height - Tile.SIZE))){
            // Not falling through a solid on top tile AND not falling through bottom map tile--OK to collide with floor.
            newY = newY - (newY % Tile.SIZE); // Just above the floor.
            player.physics.collideWithFloor();
          }
        }
      }
      else {
        //Moving up
        if(Game.world.getTileType(newX, newY) == Tile.TileType.SOLID ||
           Game.world.getTileType(newX + Sprite.IMAGE_WIDTH - 1, newY) == Tile.TileType.SOLID){
          newY += Tile.SIZE - newY % Tile.SIZE;
          player.physics.collideWithCeiling();
        }
      }
    }
        
    return newY;
  }
  
  /**
   * Gets a random world name from the map resource directory.
   * @return Random world name.
   */
  public static String getRandomWorld(){
    ArrayList<String> result = new ArrayList<String>();
    File folder = new File(World.class.getClassLoader().getResource("map/").getFile());
    File[] listOfFiles = folder.listFiles();
    for(File f : listOfFiles){
      result.add(f.getName());
    }
    
    return result.get((int)(Math.random()*result.size()));    
  }

  /**
   * Draws font to provided graphics object.
   * @param g 2D graphics object.
   * @param io Image observer.
   */
  public void drawFront(Graphics2D g, ImageObserver io){
    if(frontTileList.size() > 0){
      for(Tile tile : frontTileList){
        tile.draw(g, io);
      }
    }
  }

  /**
   * Draws the world to the provided graphics object.
   * @param g 2D graphics object.
   * @param io Image observer.
   */
  public void draw(Graphics2D g, ImageObserver io) {
    // Draw the background (has background tiles in it)
    g.drawImage(backgroundImg, 0, 0, io);
    
    for (Tile tile : animatedTileList) {
      tile.draw(g, io);
    }
    
    if(movingPlatforms != null && movingPlatforms.length > 0){
      for(MovingPlatform platform : movingPlatforms){
        platform.draw(g, io);
      }
    }
  }

  /**
   * Updates the world based on the time delta.
   * @param timeDelta_ms Game time passed in ms. 
   */
  public void update(int timeDelta_ms) {
    if(movingPlatforms != null && movingPlatforms.length > 0){
      for(MovingPlatform platform : movingPlatforms){
        platform.move(1);
      }
    }
    
    for (Tile tile : animatedTileList) {
      tile.update(timeDelta_ms);
    }
  }

/**
 * This method will return the block at the given pixel coordinates.
 * @param x pixel on the x axis
 * @param y pixel on the y axis
 * @return the block at the given pixel. If there is no block, it will return null
 */
public Block getBlock(int x, int y) {
  Tile t = getTile(x, y);
  return (t != null) ? t.block : null;
}

/**
 * This method will return the block at the given pixel coordinates.
 * @param x pixel on the x axis
 * @param y pixel on the y axis
 * @return the block at the given pixel. If there is no tile, it will return null
 */
public Tile getTile(int x, int y) {
  // Everything above the screen is non solid
  if (y < 0) {
    return null;
  }
  
  // We want the right value within the window (no negatives either!)
  x = (GameFrame.res_width + x) % GameFrame.res_width; 
  
  // For Y, we only want to wrap the bottom to the top.
  y = y % GameFrame.res_height; 
  
  int column = x / Tile.SIZE;
  int row = y / Tile.SIZE;
  
  return backgroundTiles[column][row][0];
}


/**
 * This method will return the tile type at the given pixel
 * 
 * @param x pixel on the x axis
 * @param y pixel on the y axis
 * @return the tile type at the given pixel
 */
public TileType getTileType(int x, int y) {
    //Everything above the screen is non solid
    if(y < 0){
      return TileType.NONSOLID;
    }
    
    //We want the right value within the window (no negatives either!)
    x =  ( GameFrame.res_width  + x) % GameFrame.res_width; 
    
    //For Y, we only want to wrap the bottom to the top
    y = y % GameFrame.res_height; 
    
    int column = x / Tile.SIZE;
    int row = y / Tile.SIZE;

    return backgroundTiles[column][row][0].getTileType();
  }

  /** Returns the music category ID. */
  public int getMusicCategoryID() {
    return musicCategoryID;
  }
}
