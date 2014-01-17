package smw.ui.screen;

import smw.entity.Player;
import smw.ui.PlayerControl;

public class Game implements Runnable {  
  private GameFrame game_frame;
  private Player[] players;
  private PlayerControl[] player_controls;
  
  /** The desired frames per second. */
  public double FPS = 60.0;
  
  /** Indicates if main game loop is running. */
  private boolean running = false;
  
  public Game(final int numPlayers) {
    players = new Player[numPlayers];
    player_controls = new PlayerControl[numPlayers];
    for (int i = 0; i < numPlayers; ++i) {
      players[i] = new Player();
      players[i].init(50, 50, "4matsy_BubbleBobble.bmp");
      player_controls[i] = new PlayerControl(players[i]);
    }

    this.game_frame = new GameFrame(players);

    // TODO I think the player/control should be packaged together... but this
    // is good enough for now
    game_frame.addKeyListener(player_controls[0]);    
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
        System.out.println("FPS " + frames + " UPS " + updates);
        secTimer += 1000;
        frames = 0;
        updates = 0;
      }
      
    }
  }

  private void render() {
    // TODO - this will probably change to render individual stuff
    game_frame.repaint();
  }

  private void updateGame() {
    // TODO - poll input update    
    // Poll player update (movement, etc.)
    // Way later poll level update and all that other junk...
    players[0].move();
  }
}
