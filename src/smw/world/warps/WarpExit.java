package smw.world.warps;

import java.awt.image.BufferedImage;

import smw.entity.Player;
import smw.gfx.Sprite;
import smw.world.Tile;

public abstract class WarpExit extends WarpBase{
  //TODO these values are only used because we currently expect a player to be 32x32. The original source
  //     had players as w=22, h=25. Once that change goes in, this must be removed.
  private final static int startOffSetY = Sprite.offSetY;
  private final static int startOffSetX = Sprite.offSetX;
  //Player should start full in, but is "out" once his extra on the first part + collidable part is out.
  private final static int PIXELS_TIL_OUT_X = Player.WIDTH + ((Sprite.IMAGE_WIDTH - Player.WIDTH)/2);
  private final static int PIXELS_TIL_OUT_Y = Player.HEIGHT;
  short lockx;
  short locky;

  short numblocks; 

  /**
   * 
   * @param connection
   * @param id
   * @param x - the x the player should end up at
   * @param y - the y the player should end up at
   * @param lockx
   * @param locky
   */
  public WarpExit(short connection, short id, short x, short y, 
                  short lockx, short locky) {
    super(x, y, connection, id);
    
    this.lockx = lockx;
    this.locky = locky;
  }
  
  void init(BufferedImage image){
    this.image = image;
    subImageX = 0;
    subImageY = 0;
    subImageWidth = Sprite.IMAGE_WIDTH;
    subImageHeight = Sprite.IMAGE_HEIGHT;
    shiftX = 0;
    shiftY = 0;
  }
  
  
  /**
   * Right to Left WARP exit
   */
  public static class Left extends WarpExit{

    public Left(short connection, short id, short lockx, short locky, short warpx, short warpy) {
      super(connection, id, (short)(warpx - PIXELS_TIL_OUT_X + 1), warpy, lockx, locky);
      
    }
    
    @Override
    public void init(Player player){
      super.init(player.getImage());
      
      shiftX = Player.WIDTH;
      subImageWidth = Sprite.IMAGE_HEIGHT - PIXELS_TIL_OUT_X;
    }

    @Override
    public void update(float timeDif_ms) {
      float pixelDif = WARP_VELOCITY*timeDif_ms;
      
      shiftX -= pixelDif; 
      subImageWidth += pixelDif;
    }
    
    @Override
    public boolean shouldBeRemoved() {
      return subImageWidth  > PIXELS_TIL_OUT_X;
    }
  }
  
  /**
   * Left to Right WARP exit
   */
  public static class Right extends WarpExit{

    public Right(short connection, short id, short lockx, short locky, short warpx, short warpy) {
      super(connection, id, (short)(warpx + Tile.SIZE), warpy, lockx, locky);
      
    }
    
    @Override
    public void init(Player player){
      super.init(player.getImage());
      
      subImageX = Sprite.IMAGE_WIDTH;
      subImageWidth = 0;
    }

    @Override
    public void update(float timeDif_ms) {
      float pixelDif = WARP_VELOCITY*timeDif_ms;
      
      subImageX -= pixelDif; 
      subImageWidth += pixelDif;
    }
    
    @Override
    public boolean shouldBeRemoved() {
      return subImageWidth  > PIXELS_TIL_OUT_X;
    }
  }
  
  /**
   * Down to Up WARP exit
   */
  public static class Up extends WarpExit{

    public Up(short connection, short id, short lockx, short locky, short warpx, short warpy) {
      super(connection, id, warpx, (short)(warpy - Sprite.IMAGE_HEIGHT + 1), lockx, locky);
      
    }
    
    @Override
    public void init(Player player){
      BufferedImage image = player.getImage();
      super.init(image);
      
      shiftY = Tile.SIZE;
      subImageHeight = 0;
    }

    @Override
    public void update(float timeDif_ms) {
      float pixelDif = WARP_VELOCITY*timeDif_ms;
      
      shiftY -= pixelDif; 
      subImageHeight += pixelDif;
    }
    
    @Override
    public boolean shouldBeRemoved() {
      return subImageHeight  > PIXELS_TIL_OUT_Y;
    }
  }
  
  /**
   * Up to Down WARP exit
   */
  public static class Down extends WarpExit{

    public Down(short connection, short id, short lockx, short locky, short warpx, short warpy) {
      super(connection, id, warpx, (short)(warpy + Tile.SIZE), lockx, locky);
      
    }
    
    @Override
    public void init(Player player){
      BufferedImage image = player.getImage();
      super.init(image);
      
      subImageY = Sprite.IMAGE_HEIGHT;
      subImageHeight = 0;
    }

    @Override
    public void update(float timeDif_ms) {
      float pixelDif = WARP_VELOCITY*timeDif_ms;
      
      subImageY -= pixelDif; 
      subImageHeight += pixelDif;
    }
    
    @Override
    public boolean shouldBeRemoved() {
      return subImageHeight  > PIXELS_TIL_OUT_Y;
    }
  }
}
