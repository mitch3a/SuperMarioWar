package smw.world;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import smw.gfx.Palette;

public class AnimatedTile extends AnimatedDrawable{
  /**
   * 
   */
  private static final long serialVersionUID = -4608576932031411676L;
  static final int ANIMATED_TILE_STATES = 4;
  static BufferedImage masterImage = null; //TODO not crazy about how this is done but quick/efficient

  /*** column is in sets of four (so the index 8th tile would be columnOfFour index 2) ***/
  public AnimatedTile(int row, int columnOfFour, int x, int y){
    super(x, y);
    
    //TODO mk stole from Animated Block
    frameRate = 4; // TODO - not sure how this should be set or if it's the same for all block animations
    updateRate_ms = 1000 / frameRate;
    
    try {
      if(masterImage == null){
        BufferedImage bigImg = ImageIO.read(this.getClass().getClassLoader().getResource("map/tilesheets/tile_animation.png"));
        // Must convert to a BufferedImage that allows transparency (read above uses TYPE_3BYTE_BGR).
        masterImage = new BufferedImage(bigImg.getWidth(), bigImg.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        masterImage.getGraphics().drawImage(bigImg, 0, 0, null);
        // Setup alpha channel
        Palette p = Palette.getInstance();
        p.loadPalette();
        p.implementTransparent(masterImage);
      }
      
      // Number of images in the animation assuming our file is laid out horizontally
      images = new BufferedImage[ANIMATED_TILE_STATES];
      
      int xStart = ANIMATED_TILE_STATES*columnOfFour*Tile.SIZE;
      int yStart = row*Tile.SIZE;
      
      for (int i = 0; i < ANIMATED_TILE_STATES; ++i ) {
        images[i] = masterImage.getSubimage(xStart , yStart, Tile.SIZE, Tile.SIZE);
        animationTime_ms += updateRate_ms;
        xStart += Tile.SIZE;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public boolean hasImage(){
    return true;
  }
}
