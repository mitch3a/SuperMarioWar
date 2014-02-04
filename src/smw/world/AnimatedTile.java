package smw.world;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import smw.gfx.Palette;

/** AnimatedTile provides a way to animate blocks based on existing map files. */
public class AnimatedTile {

  // Animated block types.
  public static final int BREAKABLE_BLOCK = 0;
  public static final int QUESTION_BLOCK = 1;
  public static final int FLIP_BLOCK = 3;
  public static final int WHITE_NOTE_BLOCK = 5;
  public static final int BLUE_THROW_BLOCK = 6;
  public static final int RED_THROW_BLOCK = 16;
  public static final int RED_NOTE_BLOCK = 17;
  public static final int BLUE_NOTE_BLOCK = 18;
  public static final int WHITE_THROW_BLOCK = 19;
  
  /** Images used for each frame of animation. */
  private BufferedImage[] images;
  /** Current image index in the animation. */
  private int currentImg = 0;
  /** The frame rate of the animation. */
  private int frameRate;
  /** The time per frame. */
  private int updateRate_ms;
  /** Time it takes for a complete animation (all frames). */
  private int animationTime_ms;
  /** The total time that has elapsed (for all frames). */
  private int elapsedTime_ms;
  /** The time that has elapsed for the current frame. */
  private int frameTime_ms;

  /**
   * Constructs animated tile based on type.
   * @param type Block type defined by map.
   */
  public AnimatedTile(int type) {
    // TODO - eventually support all animated tile types Not sure how this giant tile sheet fits into the picture (\res\map\tilesheets\tile_animation.png)
    String image = "powerupblock.png";
    int yOffset = 0;
    switch (type) {
      case BREAKABLE_BLOCK:
        image = "breakableblock.png";
        break;
      case QUESTION_BLOCK:
        image = "powerupblock.png";
        break;
      case FLIP_BLOCK:
        image = "flipblock.png";
        break;
      case WHITE_NOTE_BLOCK:
        yOffset = 1;
        image = "noteblock.png";
        break; 
      case BLUE_THROW_BLOCK:
        image = "throwblock.png"; 
        break;
      case RED_THROW_BLOCK:
        yOffset = 2;
        image = "throwblock.png"; 
        break;
      case RED_NOTE_BLOCK:
        yOffset = 2; 
        image = "noteblock.png";
        break;
      case BLUE_NOTE_BLOCK:
        image = "noteblock.png"; 
        break;
      case WHITE_THROW_BLOCK:
        yOffset = 1;
        image = "throwblock.png";
        break;
    }
 
    frameRate = 4; // TODO - not sure how this should be set or if it's the same for all block animations
    
    updateRate_ms = 1000 / frameRate;
    
    try {
      BufferedImage bigImg = ImageIO.read(this.getClass().getClassLoader().getResource("map/blocks/" + image));
      // Must convert to a BufferedImage that allows transparency (read above uses TYPE_3BYTE_BGR).
      BufferedImage convertedImg = new BufferedImage(bigImg.getWidth(), bigImg.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
      convertedImg.getGraphics().drawImage(bigImg, 0, 0, null);
      // Setup alpha channel
      Palette p = Palette.getInstance();
      p.loadPalette();
      p.implementTransparent(convertedImg);
      
      // Number of images in the animation assuming our file is laid out horizontally
      images = new BufferedImage[convertedImg.getWidth() / Tile.SIZE];
      
      for (int i = 0; i < images.length; i++) {
        images[i] = convertedImg.getSubimage(i * Tile.SIZE, yOffset * Tile.SIZE, Tile.SIZE, Tile.SIZE);
        animationTime_ms += updateRate_ms;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
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
  
  /**
   * Returns whether the provided type is an animated block type.
   * @param type Animated block type.
   * @return true = animated
   */
  public static boolean isTypeAnimated(int type) {
    switch (type) {
    case BREAKABLE_BLOCK:
    case QUESTION_BLOCK:
    // TODO - flip blocks are only animated when they get hit by a player! case FLIP_BLOCK:
    case WHITE_NOTE_BLOCK:
    case BLUE_THROW_BLOCK:
    case RED_THROW_BLOCK:
    case RED_NOTE_BLOCK:
    case BLUE_NOTE_BLOCK:
    case WHITE_THROW_BLOCK:
        return true;
    }
    return false;
  }
}
