package smw.world;

import java.awt.image.BufferedImage;

public class AnimatedDrawable {
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
  
  /**
   * Updates the animation based on elapsed time.
   * @param timeDif_ms time delta 
   */
  public void update(int timeDif_ms) {
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

  /**
   * Returns the current image of the animation.
   * @return current image
   */
  public BufferedImage getImage() {
    return images[currentImg];
  }
}
