package smw.gfx;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Sprite {
	public static enum Action {
		NONE(0), RUNNING_STEP(1), RUNNING_NO_STEP(0), JUMPING(2), SKIDDING(3), DYING(4), SQUASHED(5);

	  private final int index;
	  private Action(int index) {
	    this.index = index;
	  }
	};

	public static enum Direction {
		RIGHT(0), LEFT(1);
		
		public static int NUM_DIRECTIONS = 2;
	  public final int index;
	  private Direction(int index) {
	    this.index = index;
	  }
	};

  public static final int IMAGE_WIDTH = 32;
  public static final int IMAGE_HEIGHT = 32;
  public static final int NUM_IMAGES = 6;
  public static final long TIME_TO_HALF_STEP_NS = 80000000; // .08 seconds
  
  //Keep a copy of each direction to avoid image flipping processing
  BufferedImage[][] sprites = new BufferedImage[Direction.NUM_DIRECTIONS][NUM_IMAGES];
  
  Action    currentAction    = Action.NONE;
  Direction currentDirection  = Direction.RIGHT;
 
  long timeActionChange_ns = 0;
  Point2D position;

  public Sprite() {
    position = new Point2D.Float();
  }

  public void init(int x, int y, String image) {
    position.setLocation((double)x, (double)y);
    initSpriteImage(image);
  }
  
  public void initSpriteImage(String image){
    try {
      BufferedImage bigImg = ImageIO.read(this.getClass().getClassLoader().getResource("sprites/" + image));
      
      // Change magenta to transparent.
      // Start by converting buffered image to pixels.
      final int width = bigImg.getWidth();
      final int height = bigImg.getHeight();
      int[] pixels = new int[width * height];
      bigImg.getRGB(0, 0, width, height, pixels, 0, width);
      
      // Change each magenta pixel.
      for (int i = 0; i < pixels.length; i++) {
        if (pixels[i] == 0xffff00ff) {
          pixels[i] = 0; // This might be -1 if we're using alpha channel, not sure.
        }
      }
      
      // Convert pixels back into buffered image.
      int[] bitMasks = new int[]{0xFF0000, 0xFF00, 0xFF, 0xFF000000};
      SinglePixelPackedSampleModel sm = new SinglePixelPackedSampleModel(
      DataBuffer.TYPE_INT, width, height, bitMasks);
      DataBufferInt db = new DataBufferInt(pixels, pixels.length);
      WritableRaster wr = Raster.createWritableRaster(sm, db, new Point());
      bigImg = new BufferedImage(ColorModel.getRGBdefault(), wr, false, null);
      
      //Create Transform to flip image for left facing versions
      AffineTransform result = AffineTransform.getScaleInstance(-1.0, 1.0);
      result.translate(-IMAGE_WIDTH, 0);
      AffineTransformOp op = new AffineTransformOp(result, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
      
      for (int i = 0; i < NUM_IMAGES; i++) {
        sprites[Direction.RIGHT.index][i] = bigImg.getSubimage(i * IMAGE_WIDTH, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
        sprites[Direction.LEFT.index ][i] = op.filter(sprites[Direction.RIGHT.index][i], null);
      }
    } catch (IOException e) {
      e.printStackTrace();  // TODO UH OH...error handling...
    }
  }

  public int getX() {
    return (int)position.getX();
  }

  public int getY() {
    return (int)position.getY();
  }

  // It is assumed this method is only called after collision detection passed
  public void move(float dx, float dy, boolean isJumping, boolean isSkidding) {
    if( dx != 0){
      currentDirection = (dx < 0) ? Direction.LEFT : Direction.RIGHT;
    }
    
    if(isJumping){
      currentAction = Action.JUMPING;
    }
    else if(isSkidding){
      currentAction = Action.SKIDDING;
    }
    else{
      switch (currentAction) {
  	    case NONE:
  	      checkForRunning(dx);
  	      break;
  	    case RUNNING_STEP:
  	      checkForRunningStepChange(dx, Action.RUNNING_NO_STEP);
  	      break;
  	    case RUNNING_NO_STEP:
  	      checkForRunningStepChange(dx, Action.RUNNING_STEP);
  	      break;
  	    case JUMPING:
  	      currentAction = (dx == 0) ? Action.NONE : Action.RUNNING_STEP;
  	    case SKIDDING:
          currentAction = (dx == 0) ? Action.NONE : Action.RUNNING_STEP;
  	    case SQUASHED: // TODO
  	      break;
  	    case DYING: // TODO
  	      break;
      }
    }

    position.setLocation(position.getX() + dx, position.getY() + dy);
  }

  private void checkForRunningStepChange(float dx, Action nextRunningAction) {
    if (dx == 0) {
      currentAction = Action.NONE;
    } 
    else {
      long currentTime_ns = System.nanoTime();
      if (currentTime_ns - timeActionChange_ns > TIME_TO_HALF_STEP_NS) {
        currentAction = nextRunningAction;
        timeActionChange_ns = currentTime_ns;
      }
    }
  }

  private void checkForRunning(float dx) {
    if (dx != 0) {
      currentAction = Action.RUNNING_STEP;
      timeActionChange_ns = System.nanoTime();
    }
  }

  public Image getImage() {
    return sprites[currentDirection.index][currentAction.index];
  }

  public void setJumping() {
    currentAction = Action.JUMPING;
  }

	public void movingLeft() {
	  currentDirection = Direction.LEFT;
  }
	
	public void movingRight() {
	  currentDirection = Direction.RIGHT;
  }

	public void setY(int i) {
	  //TODO this is just temp
		position.setLocation(position.getX(), i);
  }
}
