package smw;

import java.awt.event.KeyEvent;

import smw.entity.Player;
import smw.settings.Debug;
import smw.ui.PlayerControl;
import smw.ui.screen.GameFrame;

public class Game implements Runnable {  
  private GameFrame gameFrame;
  private Player[] players;
  
  /** The desired frames per second. */
  public double FPS = 60.0;
  
  /** Indicates if main game loop is running. */
  private boolean running = false;
  
  public Game(final int numPlayers) {
    players = new Player[numPlayers];
    PlayerControl[] pc = new PlayerControl[2]; //TODO mk made this 2 on purpose... only for testing (until real input configured);
    pc[0] = new PlayerControl(KeyEvent.VK_LEFT,KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_UP, KeyEvent.VK_SPACE);
    pc[1] = new PlayerControl(KeyEvent.VK_A,   KeyEvent.VK_D,     KeyEvent.VK_W,  KeyEvent.VK_S,    KeyEvent.VK_W, KeyEvent.VK_G);
    
    String[] images = {"4matsy_BubbleBobble.bmp", "FTG_Yoshi.png"};
    
    for (int i = 0; i < numPlayers; ++i) {
    	//TODO this is obviously for just 1 player
      players[i] = new Player(pc[i], i);
      players[i].init(50*(i + 2), 50, images[i]);
      
    }

    this.gameFrame = new GameFrame(players);

    // TODO I think the player/control should be packaged together... but this
    // is good enough for now
    for(Player p : players){
    	gameFrame.addKeyListener(p.getControls());
    }
  }

  public synchronized void start() {    
    // TODO start new stuff here
    running = true;
    new Thread(this).start();
  }
  
  public synchronized void stop() {
    running = false;
    // TODO - is there anything else we need to do with the thread when stopping?
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
      boolean needRender = false;

      while (neededUpdates >= 1.0) {
        updateGame();
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
    // TODO - this will probably change to render individual stuff
    gameFrame.repaint();
  }

  private void updateGame() {
    // TODO - poll input update    
    // Poll player update (movement, etc.)
    // Way later poll level update and all that other junk...
  	
  	// Mitch - I set this up to just do the collision and if no collision, then allow the player in.
  	// not only is this unfair (ie if 2 players are running at each other, player 1 will see the spot
  	// open, take it, then player 2 will see it as taken and not get it), but this could also cause
  	// weird issues (like if you were chasing a player moving 2 pixels a frame, you couldn't get any
  	// closer than 2 pixels to him. But this is good enough for now. 
  	for(Player p : players){
  		p.move(players);
  	}
  }
}
