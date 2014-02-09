package smw.world;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import smw.Collidable;
import smw.Drawable;
import smw.Updatable;

//TODO I don't like this being collidable... need to fix this
public class AnimatedDrawable extends Collidable.Solid implements Updatable, Drawable{
  /** Images used for each frame of animation. */
  protected BufferedImage[] images;
  /** Current image index in the animation. */
  private int currentImg = 0;
  /** The frame rate of the animation. */
  protected int frameRate;
  /** The time per frame. */
  protected int updateRate_ms;
  /** Time it takes for a complete animation (all frames). */
  protected int animationTime_ms;
  /** The total time that has elapsed (for all frames). */
  private int elapsedTime_ms;
  /** The time that has elapsed for the current frame. */
  private int frameTime_ms;
  /** Indicates if the animation is running. */
  private boolean running = true;
  
  //TODO consider moving these to a parent class
  private int x, y;
  
  public AnimatedDrawable(int x, int y){
    super(x, y);
    
    this.x = x;
    this.y = y;
  }
  
  public boolean isRunning() {
    return running;
  }

  public void stop() {
    this.running = false;
  }
  
  public void start() {
    this.running = true;
  }

  /**
   * Updates the animation based on elapsed time.
   * @param timeDif_ms time delta 
   */
  public void update(float timeDif_ms) {
    if (!running) {
      return;
    }
    
    elapsedTime_ms += timeDif_ms;
    frameTime_ms += timeDif_ms;
    
    if (elapsedTime_ms >= animationTime_ms) {
      elapsedTime_ms = 0;
      frameTime_ms = 0;
      currentImg = 0;
    }
    
    if (frameTime_ms >= updateRate_ms) {
      frameTime_ms = 0;
      currentImg++;
    }
  }

  @Override
  public void draw(Graphics2D g, ImageObserver io) {
    g.drawImage(images[currentImg], x, y, io);
  }
  
  public BufferedImage getImage(){
    return images[currentImg];
  }
}
