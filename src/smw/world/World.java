package smw.world;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import smw.world.Structures.Item;
import smw.world.Structures.RaceGoalLocation;
import smw.world.Structures.SpawnArea;
import smw.world.Structures.WorldBuffer;
import smw.world.blocks.AnimatedBlock;
import smw.world.blocks.DonutBlock;
import smw.world.blocks.FlipBlock;
import smw.world.blocks.NoteBlock;
import smw.world.blocks.QuestionBlock;
import smw.world.blocks.SolidBlock;
import smw.world.blocks.SwitchBlock;
import smw.world.blocks.SwitchControlBlock;
import smw.world.blocks.SwitchControlBlock.Blue;
import smw.world.blocks.SwitchControlBlock.Green;
import smw.world.blocks.SwitchControlBlock.Red;
import smw.world.blocks.SwitchControlBlock.Yellow;
import smw.world.hazards.AnimatedHazard;
import smw.world.warps.WarpEntrance;
import smw.world.warps.WarpExit;
import smw.world.warps.WarpBase.Direction;

public class World {

  public final int MAX_LAYERS = 4;
  public final int MAX_WARPS = 32;
  public final int NUM_SPAWN_AREA_TYPES = 6;
  public final int MAP_WIDTH;
  public final int MAP_HEIGHT;

  BufferedImage backgroundImg;

  Item[] items;
  WarpExit[] warpExits;
  DrawArea[] drawAreas;
  // This is a hardcoded value only used once. If you know why it is 12, replace
  // this comment with the reason
  boolean[] autoFilter = new boolean[12];

  RaceGoalLocation[] raceGoalLocations;
  FlagBaseLocation[] flagBaseLocation;

  final Collidable[][] collidables;

  // ///////////////////////////////////////////////////////////////////
  // Purposefully kept these apart as they should only be used to draw
  // and should only be called in order
  final List<Drawable> drawablesLayer0 = new LinkedList<Drawable>();
  final List<Drawable> drawablesLayer1 = new LinkedList<Drawable>();
  final List<Drawable> drawablesLayer2 = new LinkedList<Drawable>();
  final List<Drawable> drawablesLayer3 = new LinkedList<Drawable>();

  final List<Updatable> updatables = new LinkedList<Updatable>();
  final List<MovingCollidable> movingCollidables = new LinkedList<MovingCollidable>();

  // TODO this is temp... until there is a smarter way to update blocks on a
  // change
  final static List<SolidBlock> blocks = new LinkedList<SolidBlock>();

  WarpEntrance[][] warps;
  SpawnArea[][] spawnAreas;

  final boolean[][][] nospawn;

  /** The music category ID as defined in the world file. */
  private int musicCategoryID;

  /**
   * Constructs the world based on a random map file.
   */
  public World() {
    this(getRandomWorld());
  }

  /**
   * Constructs the world object based on the provided file name.
   * 
   * @param worldName
   */
  public World(String worldName) {

    if (Debug.LOG_WORLD_INFO) {
      System.out.println("Loading world: " + worldName);
    }

    MAP_WIDTH = GameFrame.res_width / Tile.SIZE;
    MAP_HEIGHT = GameFrame.res_height / Tile.SIZE;

    collidables = new Collidable[MAP_WIDTH][MAP_HEIGHT];
    // This is because we read in some collidables (ie blocks) before reading in
    // the "top" TileType. These collidables must be added after.
    Set<Collidable> collidablesForAfter = new HashSet<Collidable>();

    warps = new WarpEntrance[MAP_WIDTH][MAP_HEIGHT];
    nospawn = new boolean[NUM_SPAWN_AREA_TYPES][MAP_WIDTH][MAP_HEIGHT];

    try (WorldBuffer buffer = new WorldBuffer(worldName)){
      int version = buffer.getVersion();

      // For now only support latest world files (1.8+)
      if (version >= 1800) {
        for (int i = 0; i < autoFilter.length; i++) {
          autoFilter[i] = buffer.getAutoFilter();
        }

        buffer.getInt(); // unused 32 bits after auto filter section

        // /////////////////////////////////////////////////////////////
        // Load tile set information.
        // /////////////////////////////////////////////////////////////
        final int numTileSheets = buffer.getInt();

        Map<Integer, String> tileSheetMap = new HashMap<Integer, String>();

        for (int i = 0; i < numTileSheets; i++) {
          int tileSheetId = buffer.getInt();
          String tileSheetName = buffer.getString();

          tileSheetMap.put(tileSheetId, tileSheetName);

          if (Debug.LOG_WORLD_INFO) {
            System.out.println("tileset: " + tileSheetName);
          }
        }

        // Need switch control block references created up front so we can
        // update them after the tiles.
        SwitchControlBlock redSwitch = null;
        SwitchControlBlock greenSwitch = null;
        SwitchControlBlock yellowSwitch = null;
        SwitchControlBlock blueSwitch = null;

        // /////////////////////////////////////////////////////////////
        // Load background tiles
        // /////////////////////////////////////////////////////////////
        for (int h = 0; h < MAP_HEIGHT; ++h) {
          for (int w = 0; w < MAP_WIDTH; w++) {
            // Right now we are only using one layer, but still need to read
            // them
            for (int k = 0; k < MAX_LAYERS; k++) {
              Tile tile = buffer.getTile(w, h, tileSheetMap);

              if (tile.hasImage()) {
                addToLayer(tile, k);
              }

              // TODO this should be handled better
              AnimatedTile t = tile.getAnimatedTile();
              if (t != null) {
                drawablesLayer3.add(t); // TODO is this the correct layer?
                updatables.add(t);
              }
            }

            int type = (int) (buffer.getByte());
            boolean hidden = buffer.getBoolean(); // TODO do we use this or is
                                                  // it always false?
            SolidBlock block = null;

            if (Tile.isValidType(type)) {
              switch (type) {
              /** Animated Blocks (more further down) **/
                case 0:
                  AnimatedBlock temp0 = new AnimatedBlock.BreakableBlock(w
                      * Tile.SIZE, h * Tile.SIZE);
                  updatables.add(temp0);
                  block = temp0;
                  break;
                case 1:
                  AnimatedBlock temp1 = new QuestionBlock(w * Tile.SIZE, h
                      * Tile.SIZE);
                  updatables.add(temp1);
                  block = temp1;
                  break;
                case 2:
                  DonutBlock temp2 = new DonutBlock(w * Tile.SIZE, h
                      * Tile.SIZE);
                  updatables.add(temp2);
                  block = temp2;
                  break;
                case 3:
                  AnimatedBlock temp3 = new FlipBlock(w * Tile.SIZE, h
                      * Tile.SIZE);
                  updatables.add(temp3);
                  block = temp3;
                  break;
                case 5:
                  AnimatedBlock temp5 = new NoteBlock.WhiteNoteBlock(w
                      * Tile.SIZE, h * Tile.SIZE);
                  updatables.add(temp5);
                  block = temp5;
                  break;
                case 6:
                  AnimatedBlock temp6 = new AnimatedBlock.BlueThrowBlock(w
                      * Tile.SIZE, h * Tile.SIZE);
                  updatables.add(temp6);
                  block = temp6;
                  break;

                /** Switch Control Blocks **/
                case 7:
                  SwitchControlBlock temp7 = new SwitchControlBlock.Red(type, w
                      * Tile.SIZE, h * Tile.SIZE);
                  updatables.add(temp7);
                  block = temp7;
                  redSwitch = temp7;
                  break;
                case 8:
                  SwitchControlBlock temp8 = new SwitchControlBlock.Green(type,
                      w * Tile.SIZE, h * Tile.SIZE);
                  updatables.add(temp8);
                  block = temp8;
                  greenSwitch = temp8;
                  break;
                case 9:
                  SwitchControlBlock temp9 = new SwitchControlBlock.Yellow(
                      type, w * Tile.SIZE, h * Tile.SIZE);
                  updatables.add(temp9);
                  block = temp9;
                  yellowSwitch = temp9;
                  break;
                case 10:
                  SwitchControlBlock temp10 = new SwitchControlBlock.Blue(type,
                      w * Tile.SIZE, h * Tile.SIZE);
                  updatables.add(temp10);
                  block = temp10;
                  blueSwitch = temp10;
                  break;

                /** Switch Blocks **/
                case 11:
                  SwitchBlock temp11 = new SwitchBlock(type, w * Tile.SIZE, h
                      * Tile.SIZE);
                  SwitchControlBlock.Red.registerBlock(temp11);
                  block = temp11;
                  break;
                case 12:
                  SwitchBlock temp12 = new SwitchBlock(type, w * Tile.SIZE, h
                      * Tile.SIZE);
                  SwitchControlBlock.Green.registerBlock(temp12);
                  block = temp12;
                  break;
                case 13:
                  SwitchBlock temp13 = new SwitchBlock(type, w * Tile.SIZE, h
                      * Tile.SIZE);
                  SwitchControlBlock.Yellow.registerBlock(temp13);
                  block = temp13;
                  break;
                case 14:
                  SwitchBlock temp14 = new SwitchBlock(type, w * Tile.SIZE, h
                      * Tile.SIZE);
                  SwitchControlBlock.Blue.registerBlock(temp14);
                  block = temp14;
                  break;

                /** More Animated Blocks **/
                case 16:
                  AnimatedBlock temp16 = new NoteBlock.RedThrowBlock(w
                      * Tile.SIZE, h * Tile.SIZE);
                  updatables.add(temp16);
                  block = temp16;
                  break;
                case 17:
                  AnimatedBlock temp17 = new NoteBlock.RedNoteBlock(w
                      * Tile.SIZE, h * Tile.SIZE);
                  updatables.add(temp17);
                  block = temp17;
                  break;
                case 18:
                  AnimatedBlock temp18 = new NoteBlock.BlueNoteBlock(w
                      * Tile.SIZE, h * Tile.SIZE);
                  updatables.add(temp18);
                  block = temp18;
                  break;
                case 19:
                  AnimatedBlock temp19 = new AnimatedBlock.BreakableBlock(w
                      * Tile.SIZE, h * Tile.SIZE);
                  updatables.add(temp19);
                  block = temp19;
                  break;
                /** TODO Blocks **/
                default:
                  block = new SolidBlock(w * Tile.SIZE, h * Tile.SIZE);
              }

              drawablesLayer3.add(block); // TODO is this the correct layer?
              collidablesForAfter.add(block);
              blocks.add(block);
            }
          }
        }

        // /////////////////////////////////////////////////////////////
        // Load background image
        // /////////////////////////////////////////////////////////////
        String backgroundFile = buffer.getString();

        if (Debug.LOG_WORLD_INFO) {
          System.out.println("background: " + backgroundFile);
        }

        backgroundImg = ImageIO.read(this.getClass().getClassLoader()
            .getResource("map/backgrounds/" + backgroundFile));

        // /////////////////////////////////////////////////////////////
        // Read in color switch settings
        // /////////////////////////////////////////////////////////////
        Red.blocksOn = buffer.getInt() == 0;
        Green.blocksOn = buffer.getInt() == 0;
        Yellow.blocksOn = buffer.getInt() == 0;
        Blue.blocksOn = buffer.getInt() == 0;
        // Apply setting if a switch block for that color exists.
        if (redSwitch != null) {
          redSwitch.setBlocks(Red.blocksOn);
        }
        if (greenSwitch != null) {
          greenSwitch.setBlocks(Green.blocksOn);
        }
        if (yellowSwitch != null) {
          yellowSwitch.setBlocks(Yellow.blocksOn);
        }
        if (blueSwitch != null) {
          blueSwitch.setBlocks(Blue.blocksOn);
        }

        drawTilesToBackground();

        // /////////////////////////////////////////////////////////////
        // Load world objects
        // /////////////////////////////////////////////////////////////
        loadPlatforms(buffer, version, tileSheetMap);
        loadItems(buffer);
        loadHazards(buffer);

        // /////////////////////////////////////////////////////////////
        // Load Misc.
        // /////////////////////////////////////////////////////////////
        short[] eyeCandy = new short[3];

        if (version > 1802) {
          eyeCandy[0] = (short) buffer.getInt();
          eyeCandy[1] = (short) buffer.getInt();
        }

        eyeCandy[2] = (short) buffer.getInt();

        musicCategoryID = buffer.getInt();

        if (Debug.LOG_WORLD_INFO) {
          System.out.println("music category: " + musicCategoryID);
        }

        // /////////////////////////////////////////////////////////////
        // Load World specifics. Tile type overrides, warps and spawns
        // /////////////////////////////////////////////////////////////
        for (int h = 0; h < MAP_HEIGHT; ++h) {
          for (int w = 0; w < MAP_WIDTH; ++w) {
            collidables[w][h] = buffer.getCollidable(w * Tile.SIZE, h
                * Tile.SIZE);
            warps[w][h] = buffer.getWarp(w, h);

            for (short sType = 0; sType < NUM_SPAWN_AREA_TYPES; sType++) {
              nospawn[sType][w][h] = buffer.getBoolean();
            }
          }
        }

        for (Collidable collidable : collidablesForAfter) {
          collidables[(int) (collidable.x / Tile.SIZE)][(int) (collidable.y / Tile.SIZE)] = collidable;
        }

        loadSwitchBlocks(buffer);

        // /////////////////////////////////////////////////////////////
        // Load Warp Exits
        // /////////////////////////////////////////////////////////////
        int maxConnections = 0;
        short numWarpExits = buffer.getShort();
        warpExits = new WarpExit[Math.min(numWarpExits, MAX_WARPS)];

        for (int i = 0; i < warpExits.length; i++) {
          WarpExit tempWarpExit = buffer.getWarpExit();
          maxConnections = Math.max(maxConnections, tempWarpExit.connection);
          warpExits[i] = tempWarpExit;
        }

        // Ignore any more warps than the max
        for (int i = 0; i < numWarpExits - MAX_WARPS; i++) {
          buffer.getWarpExit();
        }

        // /////////////////////////////////////////////////////////////
        // Load Spawn Areas
        // /////////////////////////////////////////////////////////////
        spawnAreas = new SpawnArea[NUM_SPAWN_AREA_TYPES][];
        for (int i = 0; i < NUM_SPAWN_AREA_TYPES; i++) {
          short numSpawnAreas = buffer.getShort();
          if (numSpawnAreas == 0) {
            // If no spawn areas were identified, then create one big spawn area
            spawnAreas[i] = new SpawnArea[1];
            spawnAreas[i][0] = new SpawnArea();
          } else {
            spawnAreas[i] = new SpawnArea[numSpawnAreas];
            for (int m = 0; m < spawnAreas[i].length; m++) {
              spawnAreas[i][m] = buffer.getSpawnArea();
            }
          }
        }

        // /////////////////////////////////////////////////////////////
        // Load Draw Areas
        // /////////////////////////////////////////////////////////////
        short numDrawAreas = buffer.getShort();
        drawAreas = new DrawArea[numDrawAreas];

        for (int m = 0; m < drawAreas.length; m++) {
          drawAreas[m] = buffer.getDrawArea();
        }

        // /////////////////////////////////////////////////////////////
        // Load Extended Data Blocks
        // /////////////////////////////////////////////////////////////
        int numExtendedDataBlocks = buffer.getInt();

        for (short i = 0; i < numExtendedDataBlocks; i++) {
          short column = (short) buffer.getByte();
          short row = (short) buffer.getByte();

          short numSettings = (short) buffer.getByte();
          // TODO what do these dobackgroundTiles[column][row][0].settings = new
          // short[numSettings];
          for (short setting = 0; setting < numSettings; setting++) {
            // TODO what do these
            // dobackgroundTiles[column][row][0].settings[setting] =
            short waste = (short) buffer.getByte();
          }
        }

        // /////////////////////////////////////////////////////////////
        // Load mode item locations like flags and race goals
        // /////////////////////////////////////////////////////////////
        int numRaceGoals = buffer.getShort();
        raceGoalLocations = new RaceGoalLocation[numRaceGoals];

        for (int j = 0; j < raceGoalLocations.length; j++) {
          raceGoalLocations[j] = buffer.getRaceGoalLocation();
        }

        int numFlagBases = buffer.getShort();
        flagBaseLocation = new FlagBaseLocation[numFlagBases];

        for (int j = 0; j < flagBaseLocation.length; j++) {
          flagBaseLocation[j] = buffer.getFlagBaseLocation();
        }

      }// end if (version >= 1800)
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Reads switch block data from the file. This implementation of SMW does not
   * need to use this data because each switchable block is setup based on the
   * ON/OFF status of the control block ahead of time.
   * 
   * @param buffer
   */
  void loadSwitchBlocks(WorldBuffer buffer) {
    int iNumSwitchBlockData = buffer.getInt();
    for (short iBlock = 0; iBlock < iNumSwitchBlockData; iBlock++) {
      buffer.getByte(); // column
      buffer.getByte(); // row
      buffer.getByte(); // setting
    }
  }

  /**
   * Load moving platforms. This method expects the buffer to be at the hazards
   * position.
   * 
   * @param buffer
   * @param version
   * @param tileSheetMap
   */
  void loadPlatforms(WorldBuffer buffer, int version,
      Map<Integer, String> tileSheetMap) {
    int numPlatforms = buffer.getInt();

    for (int platformIndex = 0; platformIndex < numPlatforms; ++platformIndex) {
      int width = buffer.getInt();
      int height = buffer.getInt();

      Tile[][] platformTiles = new Tile[width][height];
      Collidable[][] collidables = new Collidable[width][height];

      // ////////////////////////////////////////////
      // Setup the tiles and their types
      for (int w = 0; w < width; ++w) {
        for (int h = 0; h < height; ++h) {
          if (version >= 1800) {
            Tile tempTile = buffer.getTile(w, h, tileSheetMap);
            collidables[w][h] = buffer.getCollidable(w * Tile.SIZE, h
                * Tile.SIZE);
            platformTiles[w][h] = tempTile;
          } else {
            // TODO other version code
          }
        }
      }

      short drawLayer = (version >= 1801) ? (short) buffer.getInt() : 2;
      int pathType = (version >= 1800) ? buffer.getInt() : 0;

      Path path = buffer.getPath(pathType, width, height);

      MovingPlatform temp = new MovingPlatform(platformTiles, collidables, path);
      movingCollidables.add(temp);

      addToLayer(temp, drawLayer);
      updatables.add(temp);
    }
  }

  void addToLayer(Drawable drawable, int layer) {
    switch (layer) {
      case 0:
        drawablesLayer0.add(drawable);
        break;
      case 1:
        drawablesLayer1.add(drawable);
        break;
      case 2:
        drawablesLayer2.add(drawable);
        break;
      case 3:
        drawablesLayer3.add(drawable);
        break;
    }
  }

  /**
   * Load items (like carry-able spikes and springs) This method expects the
   * buffer to be at the hazards position
   * 
   * @param buffer
   */
  void loadItems(WorldBuffer buffer) {

    int numMapItems = buffer.getInt();
    items = new Item[numMapItems];

    for (int i = 0; i < numMapItems; ++i) {
      items[i] = buffer.getItem();
    }
  }

  /**
   * Load hazards (like fireball strings, rotodiscs, pirhana plants). This
   * method expects the buffer to be at the hazards position
   * 
   * @param buffer
   */
  void loadHazards(WorldBuffer buffer) {
    int numMapHazards = buffer.getInt();

    for (short i = 0; i < numMapHazards; ++i) {
      AnimatedHazard temp = buffer.getHazard(this);

      if (temp != null) {
        drawablesLayer3.add(temp);
        updatables.add(temp);
        movingCollidables.add(temp);
      }
    }
  }

  void drawTilesToBackground() {
    Graphics2D g = backgroundImg.createGraphics();

    for (Drawable drawable : drawablesLayer0) {
      drawable.draw(g, null);
    }

    for (Drawable drawable : drawablesLayer1) {
      drawable.draw(g, null);
    }

    for (Drawable drawable : drawablesLayer2) {
      drawable.draw(g, null);
    }
  }

  public void testWarps(Player player, float newX, float newY) {

    if (player.physics.playerControl.isDown()) {
      int yToCheck = (int) ((player.y + Tile.SIZE + 1) / Tile.SIZE);
      WarpEntrance result = testWarps((int) (player.x / Tile.SIZE), yToCheck,
          (int) ((player.x + Player.WIDTH - 1) / Tile.SIZE), yToCheck,
          Direction.DOWN);
      if (result != null) {
        player.warp(result, getWarpExit(result));
        return;
      }
    }

    if (player.physics.playerControl.isUp()) {
      int yToCheck = (int) ((player.y - 1) / Tile.SIZE);
      WarpEntrance result = testWarps((int) (player.x / Tile.SIZE), yToCheck,
          (int) ((player.x + Player.WIDTH - 1) / Tile.SIZE), yToCheck,
          Direction.UP);

      if (result != null) {
        player.warp(result, getWarpExit(result));
        return;
      }
    }

    if (player.physics.playerControl.getDirection() < 0) {
      int XToCheck = (int) ((player.x - 1) / Tile.SIZE);
      WarpEntrance result = testWarps(XToCheck, (int) (player.y / Tile.SIZE),
          XToCheck, (int) ((player.y + Player.HEIGHT - 1) / Tile.SIZE),
          Direction.LEFT);
      if (result != null) {
        player.warp(result, getWarpExit(result));
        return;
      }
    } else if (player.physics.playerControl.getDirection() > 0) {
      int XToCheck = (int) ((player.x + Tile.SIZE + 1) / Tile.SIZE);
      WarpEntrance result = testWarps(XToCheck, (int) (player.y / Tile.SIZE),
          XToCheck, (int) ((player.y + Player.HEIGHT - 1) / Tile.SIZE),
          Direction.RIGHT);
      if (result != null) {
        player.warp(result, getWarpExit(result));
        return;
      }
    }
  }

  // Returns a valid warp if hit warp
  WarpEntrance testWarps(int column1, int row1, int column2, int row2,
      Direction direction) {
    if (row1 < 0 || column1 < 0 || row2 < 0 || column2 < 0
        || row1 >= warps[0].length || column1 >= warps.length
        || row2 >= warps[0].length || column2 >= warps.length) {
      return null;// TODO there has got to be a smarter way to do this
    }
    WarpEntrance warp = warps[column1][row1];

    if (warp.id >= 0 && warp.connection >= 0 && warp.direction == direction) {
      // Need both that the player is touching to be same warp
      WarpEntrance warp2 = warps[column2][row2];

      if (warp2.id >= 0 && warp.id == warp2.id && warp2.direction == direction) {
        return warp;
      }
    }

    return null;
  }

  WarpExit getWarpExit(WarpEntrance warp) {
    // TODO WOW this is awful
    List<WarpExit> options = new ArrayList<WarpExit>();
    WarpExit sameAsWarp = null;
    for (WarpExit warpExit : warpExits) {
      if (warp.connection == warpExit.connection) {
        if (warp.id == warpExit.id) {
          sameAsWarp = warpExit;
        } else {
          options.add(warpExit);
        }
      }
    }

    return (options.isEmpty()) ? sameAsWarp : options
        .get((int) (options.size() * Math.random()));
  }

  /**
   * Performs collision handling for the X axis.
   * 
   * @param player
   * @param newX
   * @return
   */
  // This will include fireballs, etc. For these we can't just index by
  // position, we need to check all
  public float getCollisionX(Player player, float newX) {
    // This is to test the bottom of the sprite

    // Above everything
    if (player.y <= -Player.HEIGHT) {
      return newX;
    }

    int lowestYTile = (int) (((player.y + Player.HEIGHT - 1) % GameFrame.res_height) / Tile.SIZE);

    // If half of player is above the top, just check the bottom half of the
    // player twice
    int highestYTile = (player.y < 0) ? lowestYTile
        : (int) (player.y / Tile.SIZE);

    // /////////////////////////////////////////////////////////////
    // Moving Platforms
    // /////////////////////////////////////////////////////////////
    for (MovingCollidable movingCollidable : movingCollidables) {
      newX = movingCollidable.collideX(player, newX);
    }

    // /////////////////////////////////////////////////////////////
    // Objects
    // /////////////////////////////////////////////////////////////
    newX = (newX + GameFrame.res_width) % GameFrame.res_width;

    if (player.x != newX) {
      // TODO not the best way to check if its on the edge but good enough for
      // now
      if (player.x < newX && player.x + 500 > newX) {
        // Moving Right so check the right side of the sprite with the left side
        // of the object
        int xCollision = (int) (((newX + Player.WIDTH) % GameFrame.res_width) / Tile.SIZE);

        newX = collidables[xCollision][highestYTile].collideWithLeft(player,
            newX);
        newX = collidables[xCollision][lowestYTile].collideWithLeft(player,
            newX);
      } else {
        // Moving Left so check the left side of the sprite with the right side
        // of the object
        int xCollision = (int) (newX / Tile.SIZE);

        newX = collidables[xCollision][highestYTile].collideWithRight(player,
            newX);
        newX = collidables[xCollision][lowestYTile].collideWithRight(player,
            newX);
      }
    }

    return newX;
  }

  /**
   * Performs collision handling for the Y axis.
   * 
   * @param player
   * @param newX
   * @param newY
   * @return
   */
  public float getCollisionY(Player player, float newX, float newY) {
    // /////////////////////////////////////////////////////////////
    // Moving Platforms
    // /////////////////////////////////////////////////////////////
    for (MovingCollidable movingCollidable : movingCollidables) {
      newY = movingCollidable.collideY(player, newX, newY);
    }

    // Doesn't get returned
    newX = (newX + GameFrame.res_width) % GameFrame.res_width;

    int rightmostXTile = (int) (((newX + Player.WIDTH - 1) % GameFrame.res_width) / Tile.SIZE);

    // /////////////////////////////////////////////////////////////
    // Objects
    // /////////////////////////////////////////////////////////////
    newY = newY % GameFrame.res_height;

    // TODO this >= 0 thing is to let us go above the screen... it needs a
    // little more work tho
    if (player.y != newY) {
      if (player.y < newY) {
        // Moving down so check the bottom of the sprite with the top of the
        // object
        int yCollision = (int) (((newY + Player.HEIGHT) % GameFrame.res_height) / Tile.SIZE);

        if (yCollision < 0) {
          return newY;
        }

        //order matters, like for hitting a block
        if(newX % Player.WIDTH < (Player.WIDTH/2)  || 
            collidables[rightmostXTile][yCollision].isDeath(Direction.DOWN)){//if right side is death, check left first
          Collidable leftCollidable = collidables[(int) (newX / Tile.SIZE)][yCollision];
          
          newY = leftCollidable.collideWithTop(player, newY);
          //Re-evaluate for right side
          yCollision = (int) (((newY + Player.HEIGHT) % GameFrame.res_height) / Tile.SIZE);

          //In this second case the player is JUST above a tile so no need checking it
          if (yCollision < 0 || (newY - Player.HEIGHT) % Tile.SIZE == 0) {
            return newY;
          }
          
          Collidable rightCollidable = collidables[rightmostXTile][yCollision];
          newY = rightCollidable.collideWithTop(player, newY);
        }
        else{

          Collidable rightCollidable = collidables[rightmostXTile][yCollision];
          
          newY = rightCollidable.collideWithTop(player, newY);
          //Re-evaluate for left side
          yCollision = (int) (((newY + Player.HEIGHT) % GameFrame.res_height) / Tile.SIZE);

          if (yCollision < 0 || (newY - Player.HEIGHT) % Tile.SIZE == 0) {
            return newY;
          }
          
          Collidable leftCollidable = collidables[(int) (newX / Tile.SIZE)][yCollision];
          newY = leftCollidable.collideWithTop(player, newY);
        }
      } else {
        // Moving up so check the top of the sprite with the bottom of the
        // object
        int yCollision = (int) (newY / Tile.SIZE);

        if (yCollision < 0) {
          return newY;
        }

        //order matters, like for hitting a block
        if(newX % Player.WIDTH < (Player.WIDTH/2) || 
            collidables[rightmostXTile][yCollision].isDeath(Direction.UP)){ //if right side is death, check left first
          Collidable leftCollidable = collidables[(int) (newX / Tile.SIZE)][yCollision];
          
          newY = leftCollidable.collideWithBottom(player, newY);
          //Re-evaluate for right side
          yCollision = (int) (newY / Tile.SIZE);

          if (yCollision < 0 || (newY - Player.HEIGHT) % Tile.SIZE == 0) {
            return newY;
          }
          
          Collidable rightCollidable = collidables[rightmostXTile][yCollision];
          newY = rightCollidable.collideWithBottom(player, newY);
        }
        else{

          Collidable rightCollidable = collidables[rightmostXTile][yCollision];
          
          newY = rightCollidable.collideWithBottom(player, newY);
          //Re-evaluate for left side
          yCollision = (int) (newY / Tile.SIZE);

          if (yCollision < 0 || (newY - Player.HEIGHT) % Tile.SIZE == 0) {
            return newY;
          }
          
          Collidable leftCollidable = collidables[(int) (newX / Tile.SIZE)][yCollision];
          newY = leftCollidable.collideWithBottom(player, newY);
        }
      }
    }

    return newY;
  }

  /**
   * Gets a random world name from the map resource directory.
   * 
   * @return Random world name.
   */
  public static String getRandomWorld() {
    ArrayList<String> result = new ArrayList<String>();
    File folder = new File(World.class.getClassLoader().getResource("map/")
        .getFile());
    File[] listOfFiles = folder.listFiles();
    for (File f : listOfFiles) {
      result.add(f.getName());
    }

    return result.get((int) (Math.random() * result.size()));
  }

  // TODO probably just get ride of layer0 list because they are on the
  // background.
  // ALSO can we do the same with layer1? AND layer2? depends on whats stuck
  // to background always
  public void drawBackground(Graphics2D g, ImageObserver io) {
    g.drawImage(backgroundImg, 0, 0, io);
  }

  public void drawLayer0(Graphics2D g, ImageObserver io) {
    drawLayer(drawablesLayer0, g, io);
  }

  public void drawLayer1(Graphics2D g, ImageObserver io) {
    drawLayer(drawablesLayer1, g, io);
  }

  public void drawLayer2(Graphics2D g, ImageObserver io) {
    drawLayer(drawablesLayer2, g, io);
  }

  public void drawLayer3(Graphics2D g, ImageObserver io) {
    drawLayer(drawablesLayer3, g, io);
  }

  void drawLayer(List<Drawable> drawables, Graphics2D g, ImageObserver io) {
    Iterator<Drawable> iter = drawables.iterator();
    while (iter.hasNext()) {
      Drawable temp = iter.next();

      temp.draw(g, io);

      if (temp.shouldBeRemoved()) {
        iter.remove();
      }
    }
  }
  
  //TODO don't like this but good for now
  private Map<AnimatedHazard, Integer> toAdd = new HashMap<AnimatedHazard, Integer>();
//TODO don't like this but good for now
  public void queueAnimatedHazard(AnimatedHazard hazard, int layer){
    toAdd.put(hazard, layer);
  }

  /**
   * Updates the world based on the time delta.
   * 
   * @param timeDelta_ms
   *          Game time passed in ms.
   */
  public void update(float timeDelta_ms) {
    Iterator<Updatable> iter = updatables.iterator();
    while (iter.hasNext()) {
      Updatable temp = iter.next();

      temp.update(timeDelta_ms);

      if (temp.shouldBeRemoved()) {
        iter.remove();
      }
    }

    Iterator<MovingCollidable> iter2 = movingCollidables.iterator();
    while (iter2.hasNext()) {
      MovingCollidable temp = iter2.next();

      if (temp.shouldBeRemoved()) {
        iter2.remove();
      }
    }
    
    //TODO don't like this but good for now
    for(Map.Entry<AnimatedHazard, Integer> entry : toAdd.entrySet()){
      AnimatedHazard hazard = entry.getKey();
      addDrawable(hazard, entry.getValue());
      addUpdatable(hazard);
      addMovingCollidable(hazard);
    }
    
    toAdd.clear();
  }

  public void addDrawable(Drawable drawable, int layer) {
    switch (layer) {
      case 2: drawablesLayer2.add(drawable);
              break;
      case 3: drawablesLayer3.add(drawable);
              break;
      default:
        //We draw layers 0 and 1 to a set background so they dont get drawn every cycle.
        System.out.println("DRAWABLES SHOULD ONLY BE ADDED TO LAYERS 2 and 3");
    }
  }
  
  public void addMovingCollidable(MovingCollidable movingCollidable){
    movingCollidables.add(movingCollidable);
  }
  
  public void addUpdatable(Updatable updatable){
    updatables.add(updatable);
  }

  /** Returns the music category ID. */
  public int getMusicCategoryID() {
    return musicCategoryID;
  }

  // TODO mk this needs work but the original code is really confusing.
  public void setSpawnPoint(Player player) {
    if (spawnAreas == null || spawnAreas.length == 0) {
      // TODO mk this is just to avoid crashes
      player.x = (float) (Math.random() * (GameFrame.res_width - Player.WIDTH));
      player.y = (float) (Math.random() * (GameFrame.res_height - Player.HEIGHT));

      return;
    }

    boolean safe = false;

    while (!safe) {
      int area = (int) (spawnAreas.length * Math.random());
      int spawnPoint = (int) (spawnAreas[area].length * Math.random());
      SpawnArea spawnArea = spawnAreas[area][spawnPoint];

      int offsetX = (int) ((spawnArea.width) * Math.random()) * Tile.SIZE;
      int offsetY = (int) ((spawnArea.height) * Math.random()) * Tile.SIZE;

      // TODO, consider storing these with the factors in the first place
      player.x = spawnArea.left * Tile.SIZE + offsetX;
      player.y = spawnArea.top * Tile.SIZE + offsetY;

      safe = true;
      for (MovingCollidable movingCollidable : movingCollidables) {
        if (movingCollidable.kills(player)) {
          safe = false;
          break;
        }
      }
    }
  }
}
