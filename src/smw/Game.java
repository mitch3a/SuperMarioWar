package smw;

import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import smw.entity.Player;
import smw.settings.Debug;
import smw.sound.SoundPlayer;
import smw.ui.GamePad;
import smw.ui.Keyboard;
import smw.ui.PlayerControlBase;
import smw.ui.XboxGamePad;
import smw.ui.screen.GameFrame;
import smw.world.World;

public class Game implements Runnable {  
  private GameFrame gameFrame;
  private Player[] players;
  public static World world;
  public static SoundPlayer soundPlayer = new SoundPlayer();
  
  /** The desired frames per second. */
  public double FPS = 60.0;
  
  /** Indicates if main game loop is running. */
  private boolean running = false;
  
  public Game(final int numPlayers) {
    
    // TODO - setup world selector or something, for now pick what you want to test code.
    // ALSO ADD DESCRIPTION (good for testing ____)
    String[] worlds = {
      "0smw.map", 
      "4matsy_Evening Fracas.map", 
      "NMcCoy_1-3.map", 
      "ym7_world1-2.map", 
      "two52_Up In The Hills.map", 
      "tubesteak_coolnights.map", //segment platform, spikes
      "Pikablu_Mushroom Kingdom.map",
      "MrMister_Azul Montana.map", 
      "GG_Angry angels.map", 
      "coolman13714_green greens.map", 
      "Link901_MileHigh Madness.map", 
      "Xijar_Boo is Back.map",
      "MrMister_Snow Top.map",
      "Xijar_1986.map", //ICE
      "eeliottheking_not so cave.map",
      "Peardian_Tower of the Sun.map", // BUG - has some weird ball thing that floats away when it shouldn't
      "sgraff_Buster_Beetle_s_Tower.map", // FEATURE - switches to turn on/off colored ! blocks
      "GG_Fire Fortress.map", //Animated blocks
      "tubesteak_lockout.map" //Spinning block, note box
    };   
    world = new World(worlds[18]);
    //world = new World(); // TODO - Starts a random world (for now).
    //world = new World("mm64_as seen on tv.map");
    
    players = new Player[numPlayers];
  	gameFrame = new GameFrame(players, world);
  	
  	// When the window is closed do any needed cleanup.
  	// TODO - make sure we are cleaning up everything we need to and releasing native resources where applicable!
  	gameFrame.addWindowListener(new WindowAdapter() {
  	  @Override
  	  public void windowClosing(WindowEvent we) { 
  	    soundPlayer.shutDown();
  	    for(Player p : players) {
  	      p.physics.playerControl.release();
  	    }
  	    System.exit(0);
  	  }
  	});

  	soundPlayer.setTrackList(world.getMusicCategoryID());
  	soundPlayer.playBGM();
  	if (Debug.MUTE) {
      soundPlayer.setMasterVolume(0);
    }
    if (Debug.MUTE_MUSIC) {
      soundPlayer.setBGMVolume(0);
    }
    if (Debug.MUTE_SFX) {
      soundPlayer.setSFXVolume(0);
    }
  	
    PlayerControlBase[] pc = new PlayerControlBase[numPlayers]; 

    // TODO - check for game pads, then default to keyboard--probably allow the user to change this in settings later.
    // Start with checking for Xbox controller if running Windows.
    if (Utilities.isWindows()) {
      XboxGamePad controller = new XboxGamePad(1); // player 1 only - eventually check for multiple controllers
      if (controller.isConnected()) {
        pc[0] = controller;
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
        pc[0] = new Keyboard(gameFrame, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_DOWN, KeyEvent.VK_UP, KeyEvent.VK_SPACE);
      }
    }
    if(numPlayers == 2) {
      pc[1] = new Keyboard(gameFrame, KeyEvent.VK_A,KeyEvent.VK_D, KeyEvent.VK_S, KeyEvent.VK_W, KeyEvent.VK_G);
    }

    //pc[2] = new Keyboard(gameFrame, KeyEvent.VK_A,KeyEvent.VK_D, KeyEvent.VK_W, KeyEvent.VK_G);
    //pc[3] = new Keyboard(gameFrame, KeyEvent.VK_A,KeyEvent.VK_D, KeyEvent.VK_W, KeyEvent.VK_G);
    //pc[1] = new GamePad(GamePad.SavedControllerType.SNES_WIN_MK);
    
    for (int i = 0; i < numPlayers; ++i) {
      players[i] = new Player(pc[i], i);
      players[i].init(50*(i + 2), 100);
    }
  }

  public synchronized void start() {    
    running = true;
    new Thread(this).start();
  }
  
  public synchronized void stop() {
    running = false;
  }
  
  /** Main game loop method. */
  public void run() {
    // Time between renders in nanoseconds (1 sec / FPS).
    double timePerRender_ns = 1000000000.0 / FPS;
    long lastUpdateTime_ns = System.nanoTime();
    double neededUpdates = 0.0;
    
    // Record keeping to determine FPS and UPS.
    long secTimer = System.currentTimeMillis();
    int frames = 0;
    int updates = 0;
    while (running) {
      final long currentTime_ns = System.nanoTime();
      neededUpdates += (currentTime_ns - lastUpdateTime_ns) / timePerRender_ns;
      lastUpdateTime_ns = currentTime_ns;
      boolean needRender = false; // TODO - need to figure out how we want our loop to work

      while (neededUpdates >= 1.0) {
      	//updateGame();
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
        updateGame(timePerRender_ns);
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

  private void render() {
    // TODO - our drawing performance sucks this will need to change at some point
    gameFrame.repaint();
  }

  private void updateGame(double timeDelta_ns) { 	
  	// Mitch - I set this up to just do the collision and if no collision, then allow the player in.
  	// not only is this unfair (ie if 2 players are running at each other, player 1 will see the spot
  	// open, take it, then player 2 will see it as taken and not get it), but this could also cause
  	// weird issues (like if you were chasing a player moving 2 pixels a frame, you couldn't get any
  	// closer than 2 pixels to him. But this is good enough for now. 
    float timeDelta_ms = (int)(timeDelta_ns) / 1000000;
    world.update(timeDelta_ms);
    
  	for (Player p : players) {
  		p.poll();
  	}
  	
  	for (Player p : players) {
  	  p.update(timeDelta_ms);
  		p.move(players);
  	}
  }
}
