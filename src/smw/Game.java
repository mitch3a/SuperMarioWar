package smw;

import java.awt.event.KeyEvent;

import smw.entity.Player;
import smw.gfx.PauseDisplay;
import smw.menu.*;
import smw.settings.Debug;
import smw.settings.Settings;
import smw.sound.SoundPlayer;
import smw.ui.GamePad;
import smw.ui.Keyboard;
import smw.ui.PlayerControlBase;
import smw.ui.XboxGamePad;
import smw.ui.screen.GameFrame;
import smw.world.World;

public class Game implements Runnable {  
  private Thread thread;
  //TODO don't like this but not sure how player should tell Game to bump screen
  private static GameFrame gameFrame;
  private static Player[] players;
  public static Menu menu;
  public static World world;
  public static SoundPlayer soundPlayer = new SoundPlayer();
  private static PlayerControlBase[] pc;
  
  // TODO - should probably always create a keyboard regardless of gamepads
  public static Keyboard keyboard;
  
  private static final PauseDisplay pauseDisplay = new PauseDisplay();
  
  /** The desired frames per second. */
  public double FPS = 60.0;
  
  /** Indicates if main game loop is running. */
  private boolean running = false;
  public static boolean paused = false;
  // 1: pause has been pressed but never released
  // 2: pause has been released after being pressed
  // 3: pause has been pushed again but not yet released
  private int pauseState = 0;
  private int pausePlayer;
  
  public Game(final int numPlayers) {
    //TODO move this to the menu once it is fully implemented. For now, use this
    //     place to set to what you want
    
    Settings settings = Settings.getInstance();
    /* TODO - mitch uncomment these once you fix settings! (or add a default file in projo I guess)
    settings.setStretchMode(false);
    settings.setFullscreen(false);
    settings.setVolumeSFX(1.0f);
    settings.setVolumeMaster(0.0f);
    settings.saveSettings();
    */
    // TODO - RPG work in progress - don't use this yet! probably will want to have it turned off for debugging anyway
    //setMenu(new TitleMenu());
    
    // TODO - setup world selector or something, for now pick what you want to test code.
    // ALSO ADD DESCRIPTION (good for testing ____)
    String[] worlds = {
      "0smw.map", //0 couple fall through squares, breakable blocks
      "4matsy_Evening Fracas.map",//1 Night, note block, warp
      "NMcCoy_1-3.map",  //2 Plain with one moving platform (continuous only Y)
      "ym7_world1-2.map", //3 underground, bricks + two cont platforms on Y, dif directions
      "two52_Up In The Hills.map", //4 Moving platforms both directions, question block
      "tubesteak_coolnights.map", //5 segment platform, spikes
      "Pikablu_Mushroom Kingdom.map",//6 moving platform + spikes + warps
      "MrMister_Azul Montana.map", //7 circular platform + donut blocks
      "GG_Angry angels.map", //8 clouds, sideways platform, pipe with plant
      "coolman13714_green greens.map", //9 circular platforms
      "Link901_MileHigh Madness.map", //10 tons of moving platforms (CLOUDS)
      "Xijar_Boo is Back.map",// 11 spikes
      "MrMister_Snow Top.map",
      "Xijar_1986.map", //13 ICE
      "eeliottheking_not so cave.map",
      "Peardian_Tower of the Sun.map", // BUG - has some weird ball thing that floats away when it shouldn't
      "sgraff_Buster_Beetle_s_Tower.map", //16 FEATURE - switches to turn on/off colored ! blocks
      "GG_Fire Fortress.map", //17 Animated blocks spinning fire, warps in all but up direction
      "tubesteak_lockout.map", //18 Spinning block, note box
      "cristomarquez_abovethedomes.map", //19 falling donuts, warps, clouds
      "MrMister_Airshipz.map",//20 TODO buffer gets overdrawn...
      "Sgraff_Bewarehouse.map",
      "Peardian_arcterra gate.map", //22 lots of ice + ice spikes
      "JJames_Clocks on Fire.map", // 23 lava, circular moving platform and 2 spinning hazards
      "Peardian_alinos gate.map", //24 lava + noteblocks
      "LKA_Burn Yourself.map", //25 fire cannons
      "MrMister_Dirty Pipes.map", //26 tons of warps
      "JJames_Bullet Bill Air.map" //27 Bullet bills duh
    };   
    // TODO - this map doesn't work! - Tanuki_Der Ewige Garten.map

    world = new World(worlds[0]);

    //world = new World(); // Starts a random world (for now)
        
    players = new Player[numPlayers];
  	gameFrame = new GameFrame(players, world, this);
  	
  	soundPlayer.setTrackList(world.getMusicCategoryID());
  	soundPlayer.playBGM();
  	
  	// TODO - should probably always create a keyboard regardless of gamepads
  	keyboard = new Keyboard(gameFrame.getGameFrame(), KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_DOWN, KeyEvent.VK_UP, KeyEvent.VK_UP, KeyEvent.VK_J, KeyEvent.VK_SPACE);
  	
    pc = new PlayerControlBase[numPlayers]; 

    

    // TODO - check for game pads, then default to keyboard--probably allow the user to change this in settings later.
    // Start with checking for Xbox controller if running Windows.
    if (Utilities.isWindows()) {
      XboxGamePad controller = new XboxGamePad(1); // player 1 only - eventually check for multiple controllers
      if (controller.isConnected()) {
        pc[0] = controller;
      } else {
        controller.release();
      }
    }
    // No Xbox controller, so check for other game pad.
    if (pc[0] == null) {
      try {
        GamePad controller = new GamePad(GamePad.ControllerType.SNES_WIN_MK);
        if (controller.isConnected()) {
          pc[0] = controller;
        }
      } catch (IllegalArgumentException e) {
        // No controller found!
      }
      // If still nothing just use the keyboard.
      if (pc[0] == null) {
        pc[0] = new Keyboard(gameFrame.getGameFrame(), KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_DOWN, KeyEvent.VK_UP, KeyEvent.VK_UP, KeyEvent.VK_J, KeyEvent.VK_SPACE);
      }
    }
    // TODO - temp setup player 2 with keyboard WASD
    if(numPlayers == 2) {
      pc[1] = new Keyboard(gameFrame.getGameFrame(), KeyEvent.VK_A,KeyEvent.VK_D, KeyEvent.VK_S, KeyEvent.VK_W, KeyEvent.VK_W, KeyEvent.VK_G, KeyEvent.VK_SPACE);
    }

    //pc[2] = new Keyboard(gameFrame.getGameFrame(), KeyEvent.VK_A,KeyEvent.VK_D, KeyEvent.VK_W, KeyEvent.VK_G);
    //pc[3] = new Keyboard(gameFrame.getGameFrame(), KeyEvent.VK_A,KeyEvent.VK_D, KeyEvent.VK_W, KeyEvent.VK_G);
    //pc[1] = new GamePad(GamePad.SavedControllerType.SNES_WIN_MK);
    
    for (int i = 0; i < numPlayers; ++i) {
      players[i] = new Player(pc[i], i);
      players[i].init();
    }
  }

  /** Starts the main game thread. */
  public synchronized void start() {    
    if (!running) {
      running = true;
      thread = new Thread(this);
      thread.start();
    }
  }
  
  /** Stops the main game thread. */
  public synchronized void stop() {
    if (running) {
      running = false;
      try {
        thread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
  
  /** Performs cleanup at shutdown to release any needed resources. */
  public static void shutdown() {
    soundPlayer.shutDown();
    for(Player p : players) {
      p.physics.playerControl.release();
    }
  }
  
  /** Main game loop method. */
  public void run() {
    // Time between renders in nanoseconds (1 sec / FPS).
    double timePerRender_ns = 1000000000.0 / FPS;
    long lastUpdateTime_ns = System.nanoTime();
    double neededUpdates = 0.0;
    long currentTime_ns;
    boolean needRender;
    
    // Record keeping to determine FPS and UPS.
    long secTimer = System.currentTimeMillis();
    int frames = 0;
    int updates = 0;
    while (running) {
      currentTime_ns = System.nanoTime();
      neededUpdates += (currentTime_ns - lastUpdateTime_ns) / timePerRender_ns;
      lastUpdateTime_ns = currentTime_ns;
      needRender = false; // TODO - need to figure out how we want our loop to work

      updatePause();
      
      while (neededUpdates >= 1.0) {
        updates++;
        neededUpdates -= 1.0;
        needRender = true;
      }
      
      try {
        Thread.sleep(2);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      
      if (needRender) {
        for (Player p : players) {
          p.poll();
        }
        
        if(!paused){
          updateGame(timePerRender_ns);
        }
        
        render();
        frames++;
      }
      
      if (System.currentTimeMillis() - secTimer > 1000) {
        if(Debug.LOG_FRAMERATE) System.out.println("FPS " + frames + " UPS " + updates);
        secTimer += 1000;
        frames = 0;
        updates = 0;
      }
      
    }
  }
  
  /*This version is to to more accurately depict time. I believe it also is more effience
   * mathematically
   *  
  public void run() {
    // Time between renders in nanoseconds (1 sec / FPS).
    double timePerRender_ns = 1000000000.0 / FPS;
    long lastUpdateTime_ns = System.nanoTime();
    long timeSinceLastUpdate = 0;
    long currentTime_ns;
    boolean needRender;
    
    // Record keeping to determine FPS and UPS.
    long secTimer = System.currentTimeMillis();
    int frames = 0;
    int updates = 0;
    while (running) {
      currentTime_ns = System.nanoTime();
      timeSinceLastUpdate = currentTime_ns - lastUpdateTime_ns;
      needRender = false; // TODO - need to figure out how we want our loop to work

      updatePause();
      
      if (timeSinceLastUpdate >= timePerRender_ns) {
        updates++;
        needRender = true;
        lastUpdateTime_ns = currentTime_ns;
      }
      
      try {
        Thread.sleep(2);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      
      if (needRender) {
        for (Player p : players) {
          p.poll();
        }
        
        if(!paused){
          updateGame(timeSinceLastUpdate);
        }
        
        render();
        frames++;
      }
      
      if (System.currentTimeMillis() - secTimer > 1000) {
        if(Debug.LOG_FRAMERATE) System.out.println("FPS " + frames + " UPS " + updates);
        secTimer += 1000;
        frames = 0;
        updates = 0;
      }
      
    }
  }
   */
  private void updatePause(){
    if(!paused){
      for(int i = 0 ; i < players.length ; ++i){
        if(players[i].physics.playerControl.isPaused()){
          soundPlayer.sfxPause();
          pausePlayer = i;
          paused = true;
          world.addDrawable(pauseDisplay, 3);
          pauseState = 0;
          break;
        }
      }
    }
    else{
      if(pauseState == 0){
        //Pause has been pressed but never released
        if(!players[pausePlayer].physics.playerControl.isPaused()){
          pauseState = 1;
        }
      }
      else if(pauseState == 1){
        //Fully paused. Waiting for unpause to be pushed
        if(players[pausePlayer].physics.playerControl.isPaused()){
          pauseState = 2;
        }
      }
      else if(pauseState == 2){
        //Unpaused pushed but must be released before end of pause
        if(!players[pausePlayer].physics.playerControl.isPaused()){
          soundPlayer.sfxPause();
          pauseState = 0;
          paused = false;
        }
      }
    }
  }

  private void render() {
    gameFrame.render();
  }

  private void updateGame(double timeDelta_ns) { 	
    
    float timeDelta_ms = (float) (timeDelta_ns)/1000000;
    
    if (menu != null) {
      menu.update(timeDelta_ms);
      return;
    }
    
    // Mitch - I set this up to just do the collision and if no collision, then allow the player in.
  	// not only is this unfair (ie if 2 players are running at each other, player 1 will see the spot
  	// open, take it, then player 2 will see it as taken and not get it), but this could also cause
  	// weird issues (like if you were chasing a player moving 2 pixels a frame, you couldn't get any
  	// closer than 2 pixels to him. But this is good enough for now. 
    world.update(timeDelta_ms);
  	
  	for (Player p : players) {
  	  p.update(timeDelta_ms);
  		p.move(players);
  	}
  }
  
  /**
   * Sets the current menu to display.
   * @param m Menu to display.
   */
  public static void setMenu(Menu m) {
    menu = m;
  }
  
  public static PlayerControlBase[] getPlayerControl() {
    return pc;
  }
  
  public static void bump(){
    gameFrame.bump();
  }
}
