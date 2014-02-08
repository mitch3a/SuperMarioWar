package smw.world;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import smw.Collidable;
import smw.Drawable;
import smw.Updatable;
import smw.entity.Player;
import smw.gfx.Sprite;
import smw.settings.Debug;
import smw.ui.screen.GameFrame;
import smw.world.MovingPlatform.MovingPlatform;
import smw.world.MovingPlatform.Path;
import smw.world.Structures.DrawArea;
import smw.world.Structures.FlagBaseLocation;
import smw.world.Structures.Hazard;
import smw.world.Structures.Item;
import smw.world.Structures.RaceGoalLocation;
import smw.world.Structures.SpawnArea;
import smw.world.Structures.Warp;
import smw.world.Structures.WarpExit;
import smw.world.Structures.WorldBuffer;
import smw.world.blocks.SolidBlock;
import smw.world.blocks.SwitchBlock;
import smw.world.blocks.SwitchControlBlock;

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

  RaceGoalLocation[] raceGoalLocations;
  FlagBaseLocation[] flagBaseLocation;
  
  final Collidable[][] collidables;
  
  /////////////////////////////////////////////////////////////////////
  //Purposefully kept these apart as they should only be used to draw
  //and should only be called in order
  final List<Drawable> drawablesLayer0 = new LinkedList<Drawable>();
  final List<Drawable> drawablesLayer1 = new LinkedList<Drawable>();
  final List<Drawable> drawablesLayer2 = new LinkedList<Drawable>();
  final List<Drawable> drawablesLayer3 = new LinkedList<Drawable>();
  
  final List<Updatable> updatables  = new LinkedList<Updatable>();
  final List<MovingPlatform> movingPlatforms = new LinkedList<MovingPlatform>();
  //TODO this is temp... until there is a smarter way to update blocks on a change
  final static public List<SolidBlock> blocks = new LinkedList<SolidBlock>();

  Warp[][]   warps;
  SpawnArea[][] spawnAreas;  
  
  final boolean[][][] nospawn;

  /** Animated tile list used for updating animations during world update. */
  private ArrayList<Tile> animatedTileList = new ArrayList<Tile>();

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
    
    collidables = new Collidable[MAP_WIDTH][MAP_HEIGHT];
    //This is because we read in some collidables (ie blocks) before reading in
    //the "top" TileType. These collidables must be added after.
    Set<Collidable> collidablesForAfter = new HashSet<Collidable>();
    
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
        
        Map<Integer, String> tileSheetMap = new HashMap<Integer, String>();
        
        for (int i = 0; i < numTileSheets; i++) {
          int tileSheetId      = buffer.getInt();
          String tileSheetName = buffer.getString();
          
          tileSheetMap.put(tileSheetId, tileSheetName);
          
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
              Tile tile = buffer.getTile(w, h, tileSheetMap);

              if(tile.hasImage()){
                addToLayer(tile, k);
              }
                 
              //TODO this should be handled better
              AnimatedTile t = tile.getAnimatedTile();
              if(t != null){
                drawablesLayer3.add(t); //TODO is this the correct layer?
                updatables.add(t);
              }
            }
            
            int type = (int)(buffer.getByte());
            boolean hidden = buffer.getBoolean(); //TODO do we use this or is it always false?
            
            if(Tile.isValidType(type)){
              if (AnimatedBlock.isTypeAnimated(type)) {
                AnimatedBlock animatedBlock = new AnimatedBlock(type, w*Tile.SIZE, h*Tile.SIZE);
                drawablesLayer3.add(animatedBlock); //TODO is this the correct layer?
                updatables.add(animatedBlock);
                collidablesForAfter.add(animatedBlock);
              }
              else{
                SolidBlock block = null;
                //Need this for switch blocks to pass it to the control block before 
                //casting it to a regular block 
                SwitchBlock temp = null;
                
                switch(type){
                  /** Switch Control Blocks **/
                  case  7: block = new SwitchControlBlock.Red(type, w*Tile.SIZE, h*Tile.SIZE);
                           break;
                  case  8: block = new SwitchControlBlock.Green(type, w*Tile.SIZE, h*Tile.SIZE);
                           break;
                  case  9: block = new SwitchControlBlock.Yellow(type, w*Tile.SIZE, h*Tile.SIZE);
                           break;
                  case 10: block = new SwitchControlBlock.Blue(type, w*Tile.SIZE, h*Tile.SIZE);
                           break;
                           
                  /** Switch Blocks **/
                  case 11: temp = new SwitchBlock(type, w*Tile.SIZE, h*Tile.SIZE);
                           SwitchControlBlock.Red.registerBlock(temp);
                           block = temp;
                           break;
                  case 12: temp =new SwitchBlock(type, w*Tile.SIZE, h*Tile.SIZE);
                           SwitchControlBlock.Green.registerBlock(temp);
                           block = temp;
                           break;
                  case 13: temp =new SwitchBlock(type, w*Tile.SIZE, h*Tile.SIZE);
                           SwitchControlBlock.Yellow.registerBlock(temp);
                           block = temp;
                           break;
                  case 14: temp =new SwitchBlock(type, w*Tile.SIZE, h*Tile.SIZE);
                           SwitchControlBlock.Blue.registerBlock(temp);
                           block = temp;
                           break;
                           
                  /** TODO Blocks **/         
                  default: block =new SolidBlock(w*Tile.SIZE, h*Tile.SIZE);
                }
                
                drawablesLayer3.add(block); //TODO is this the correct layer?
                collidablesForAfter.add(block);
                blocks.add(block);
                //TODO I feel like this should be updatable? updatables.add(animatedBlock);
              }
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
        
        drawTilesToBackground();
        
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
            collidables[w][h] = buffer.getCollidable(w*Tile.SIZE, h*Tile.SIZE);  
            warps[w][h] = buffer.getWarp();

            for(short sType = 0; sType < NUM_SPAWN_AREA_TYPES; sType++){
              nospawn[sType][w][h] = buffer.getBoolean();
            }
          }
        }
        
        for(Collidable collidable : collidablesForAfter){
          collidables[collidable.column][collidable.row] = collidable; 
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
          //TODO what do these dobackgroundTiles[column][row][0].settings = new short[numSettings];
          for(short setting = 0; setting < numSettings; setting++){
            //TODO what do these dobackgroundTiles[column][row][0].settings[setting] = 
                short waste = (short) buffer.getByte();
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
      e.printStackTrace();
    }
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

        //TODO what does this dobackgroundTiles[iCol][iRow][0].settings[0] = 
            short waste = (short) buffer.getByte();
    }
  }

  /**
   * Load moving platforms.
   * This method expects the buffer to be at the hazards position.
   * @param buffer
   * @param version
   * @param tileSheetMap
   */
  void loadPlatforms(WorldBuffer buffer, int version, Map<Integer, String> tileSheetMap){
    int numPlatforms = buffer.getInt();

    for(int platformIndex = 0 ; platformIndex < numPlatforms ; ++platformIndex){
      int width = buffer.getInt();
      int height = buffer.getInt();
      
      Tile[][] platformTiles = new Tile[width][height];
      Collidable[][] collidables = new Collidable[width][height];
      
      //////////////////////////////////////////////
      //Setup the tiles and their types
      for(int w = 0 ; w < width  ; ++w){
        for(int h = 0  ; h < height ; ++h){          
          if(version >= 1800){
            Tile tempTile = buffer.getTile(w, h, tileSheetMap);
            collidables[w][h] = buffer.getCollidable(w*Tile.SIZE, h*Tile.SIZE);
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

      MovingPlatform temp = new MovingPlatform(platformTiles, collidables, path);
      movingPlatforms.add(temp);
      
      addToLayer(temp, drawLayer);
      updatables.add(temp);
    }
  }  
  
  void addToLayer(Drawable drawable, int layer){
    switch(layer){
      case 0: drawablesLayer0.add(drawable);
              break;
      case 1: drawablesLayer1.add(drawable);
              break;
      case 2: drawablesLayer2.add(drawable);
              break;
      case 3: drawablesLayer3.add(drawable);
              break;
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
    Graphics2D g = backgroundImg.createGraphics();
    
    for(Drawable drawable : drawablesLayer0){
      drawable.draw(g, null);
    }
    
    for(Drawable drawable : drawablesLayer1){
      drawable.draw(g, null);
    }
    
    for(Drawable drawable : drawablesLayer2){
      drawable.draw(g, null);
    }
  }
  
  /**
   * Performs collision handling for the X axis.
   * @param player
   * @param newX
   * @return
   */
  //TODO mk for this and Y, replace Moving platforms with "movingCollidables". This will include fireballs, etc. For these we can't just index by position, we need to check all
  public int getCollisionX(Player player, int newX) {
    //This is to test the bottom of the sprite
    
    //Above everything
    if(player.y < -31){
      return newX;
    }
    
    int lowestYTile = ((player.y + Sprite.IMAGE_HEIGHT - 1) % GameFrame.res_height)/Tile.SIZE;
    
    //If half of player is above the top, just check the bottom half of the player twice
    int highestYTile = (player.y < 0) ? lowestYTile : player.y/Tile.SIZE;
    
    ///////////////////////////////////////////////////////////////
    // Moving Platforms
    ///////////////////////////////////////////////////////////////
    for(MovingPlatform platform : movingPlatforms){
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
    
    ///////////////////////////////////////////////////////////////
    // Objects
    ///////////////////////////////////////////////////////////////
    newX = (newX + GameFrame.res_width) % GameFrame.res_width;
    
    if (player.x != newX){
      if (player.x < newX) {  
        //Moving Right so check the right side of the sprite with the left side of the object
        int xCollision = ((newX + Sprite.IMAGE_HEIGHT)% GameFrame.res_width)/Tile.SIZE;
        
        newX = collidables[xCollision][highestYTile].collideWithLeft(player, newX);
        newX = collidables[xCollision][lowestYTile ].collideWithLeft(player, newX);
      }
      else{
        //Moving Left so check the left side of the sprite with the right side of the object
        int xCollision = newX/Tile.SIZE;
        
        newX = collidables[xCollision][highestYTile].collideWithRight(player, newX);
        newX = collidables[xCollision][lowestYTile ].collideWithRight(player, newX);
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
    for(MovingPlatform platform : movingPlatforms){
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
    
    int rightmostXTile = ((newX + Sprite.IMAGE_WIDTH - 1)%GameFrame.res_width)/Tile.SIZE;
    
    ///////////////////////////////////////////////////////////////
    // Objects
    ///////////////////////////////////////////////////////////////
    newY = newY % GameFrame.res_height;
    
    //TODO this >= 0 thing is to let us go above the screen... it needs a little more work tho
    if (player.y != newY){
      if (player.y < newY) {        
        //TODO mk don't think this is where we want this
        // If the player pushed the down key check to see if it was released.
        if (player.pushedDown) {
          player.pushedDown = player.physics.playerControl.isDown();
        }
        // If falling through a solid on top block then reset flag when the player has fallen at least one tile.
        if (player.isFallingThrough && (player.y - player.fallHeight) >= Tile.SIZE) {
          player.isFallingThrough = false;
        }
        
        //Moving down so check the bottom of the sprite with the top of the object
        int yCollision = ((newY + Sprite.IMAGE_HEIGHT)% GameFrame.res_height)/Tile.SIZE;
        
        if(yCollision < 0){
          return newY;
        }
        
        newY = collidables[newX/Tile.SIZE][yCollision].collideWithTop(player, newY);
        newY = collidables[rightmostXTile][yCollision].collideWithTop(player, newY);
      }
      else{
        //Moving up so check the top of the sprite with the bottom of the object
        int yCollision = newY/Tile.SIZE;
        
        if(yCollision < 0){
          return newY;
        }
        
        newY = collidables[newX/Tile.SIZE][yCollision].collideWithBottom(player, newY);
        newY = collidables[rightmostXTile][yCollision].collideWithBottom(player, newY);
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

  //TODO probably just get ride of layer0 list because they are on the background.
  //     ALSO can we do the same with layer1? AND layer2? depends on whats stuck
  //     to background always
  public void drawLayer0(Graphics2D g, ImageObserver io){
    g.drawImage(backgroundImg, 0, 0, io);
  }
  
  public void drawLayer1(Graphics2D g, ImageObserver io){
    for(Drawable drawables : drawablesLayer1){
      drawables.draw(g, io);
    }
  }
  
  public void drawLayer2(Graphics2D g, ImageObserver io){
    for(Drawable drawables : drawablesLayer2){
      drawables.draw(g, io);
    }
  }
  
  public void drawLayer3(Graphics2D g, ImageObserver io){
    for(Drawable drawables : drawablesLayer3){
      drawables.draw(g, io);
    }
  }

  /**
   * Updates the world based on the time delta.
   * @param timeDelta_ms Game time passed in ms. 
   */
  public void update(float timeDelta_ms) {
    for (Updatable updatable : updatables) {
      updatable.update(timeDelta_ms);
    }
  }

  /** Returns the music category ID. */
  public int getMusicCategoryID() {
    return musicCategoryID;
  }
}
