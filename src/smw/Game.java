package smw;

import java.awt.event.KeyEvent;

import smw.Games.ClassicGameRules;
import smw.Games.GameRules;
import smw.entity.Player;
import smw.level.Level;
import smw.settings.Debug;
import smw.ui.Keyboard;
import smw.ui.PlayerControlBase;
import smw.ui.screen.GameFrame;
import smw.ui.*;

public class Game implements Runnable {  
  private GameFrame gameFrame;
  private Player[] players;
  private Level level = new Level();
  private GameRules rules;
  
  /** The desired frames per second. */
  public double FPS = 60.0;
  
  /** Indicates if main game loop is running. */
  private boolean running = false;
  
  public Game(final int numPlayers) {
        
  	//TODO mk this logic weirds me out. GameFrame needs to have the players in order to draw them
  	//     but the keyboard needs the gameframe to register as a listener. hm....
    players = new Player[numPlayers];
    level.init();
  	this.gameFrame = new GameFrame(players, level);
  	
  	// TODO - RPG - TEMP testing my map stuff...
  	level.loadMap("NMcCoy_1-3.map");
  	rules = new ClassicGameRules(10, numPlayers);
  	
    PlayerControlBase[] pc = new PlayerControlBase[numPlayers]; 
    pc[0] = new Keyboard(gameFrame, KeyEvent.VK_LEFT,KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_SPACE);
    pc[1] = new Keyboard(gameFrame, KeyEvent.VK_A,KeyEvent.VK_D, KeyEvent.VK_W, KeyEvent.VK_G);
    //pc[2] = new Keyboard(gameFrame, KeyEvent.VK_A,KeyEvent.VK_D, KeyEvent.VK_W, KeyEvent.VK_G);
    //pc[3] = new Keyboard(gameFrame, KeyEvent.VK_A,KeyEvent.VK_D, KeyEvent.VK_W, KeyEvent.VK_G);
    //pc[1] = new GamePad(GamePad.SavedControllerType.LOGITECH_TIM);

    String[] images = {"hazey_Lolo.png", "0smw.png", "ftg_Train.png", "BlackMage.png"};
    
    for (int i = 0; i < numPlayers; ++i) {
      players[i] = new Player(pc[i], i);
      players[i].init(175*(i + 2), 50, images[i]);
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
      boolean needRender = false; // TODO - render as much as possible for now, may want to adjust this later...

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
        updateGame();
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
    // Poll player update (movement, etc.)
    // Way later poll level update and all that other junk...
    
    //TODO should maybe randomize players for all these? just to keep things "fair"
  	for(Player p : players){
  		p.poll();
  		p.updateValuesToPrepareForMove();
  	}
  	
  	for(Player p : players){
      rules.collide(level, p);
    }
  	
  	for(int p1 = 0 ; p1 < players.length - 1 ; ++p1){
  	  for(int p2 = p1 + 1 ; p2 < players.length ; ++p2){
  	    rules.collidePlayers(players[p1], players[p2]);
  	  }
  	}
  	
  	for(Player p : players){
  		p.move();
  	}
  }
}
