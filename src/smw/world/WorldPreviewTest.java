package smw.world;

import java.util.HashSet;
import java.util.Set;

import smw.settings.Debug;
import smw.sound.SoundPlayer;
import smw.ui.screen.GameFrame;
import smw.world.World;

public class WorldPreviewTest implements Runnable {  
  private Thread thread;
  //TODO don't like this but not sure how player should tell Game to bump screen
  private static WorldPreviewGameFrame gameFrame;
  public static Set<WorldPreview> worlds;
  public static SoundPlayer soundPlayer = new SoundPlayer();
  /** The desired frames per second. */
  public double FPS = 30.0;
  
  /** Indicates if main game loop is running. */
  private boolean running = false;
  
  public WorldPreviewTest() {
    worlds = new HashSet<WorldPreview>();
    
    //Float for all the divisions
    float numRows = 3;

   
    //TODO this is all for goofing... basically setup worlds however you want and pass it into the WorldPreviewGameFrame
    /* This was trying to include borders
    int borderX = 10;
    float spaceForRows = GameFrame.res_width - (borderX*(numRows + 1));
    float scaleFactor = spaceForRows/(GameFrame.res_width*numRows);
    float spaceForColumns = scaleFactor*numRows*GameFrame.res_height;
    
    float tempBorder = (GameFrame.res_height - spaceForColumns)/(numRows + 1);
    int borderY = (int)tempBorder;
    
    int tileX = (int)(spaceForRows/ numRows);
    int tileY = tileX*GameFrame.res_height/GameFrame.res_width;
    
    for(int x = borderX ; x <= GameFrame.res_width ; x = x + tileX + borderX){
      for(int y = borderY ; y <= GameFrame.res_height ; y = y + tileY + borderY){
        worlds.add(new WorldPreview("Link901_MileHigh Madness.map", x, y, scaleFactor, scaleFactor));
      }
    }
    */
    
    float scaleFactor = (1/numRows);
    
    
    int tileX = (int)(GameFrame.res_width/ numRows);
    int tileY = (int)(GameFrame.res_height/numRows);
    
    for(int x = 0 ; x < numRows ; x++){
      for(int y = 0 ; y <= numRows ; y++){
        worlds.add(new WorldPreview(x*tileX, y*tileY, scaleFactor, scaleFactor));
      }
    }

    gameFrame = new WorldPreviewGameFrame(worlds);
    
    soundPlayer.playBGM();
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
      needRender = false; 

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
    gameFrame.render();
  }

  private void updateGame(double timeDelta_ns) { 
    for(World w : worlds){
      w.update((float) (timeDelta_ns)/1000000);
    }
  }
}
