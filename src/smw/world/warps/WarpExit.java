package smw.world.warps;

import java.awt.image.BufferedImage;

import smw.entity.Player;
import smw.gfx.Sprite;
import smw.world.Tile;

public abstract class WarpExit extends WarpBase{
  //TODO these values are only used because we currently expect a player to be 32x32. The original source
  //     had players as w=22, h=25. Once that change goes in, this must be removed.
  private final static int startOffSetY = 32 - 25;
  private final static int startOffSetX = (32 - 22)/2;
  short lockx;
  short locky;

  short warpx;
  short warpy;
  short numblocks; 

  public WarpExit(short connection, short id, short x, short y, 
                  short lockx, short locky, short warpx, short warpy) {
    super(x, y, connection, id);
    
    this.lockx = lockx;
    this.locky = locky;
    this.warpx = warpx;
    this.warpy = warpy;
  }
  
  void init(BufferedImage image){
    this.image = image;
    subImageX = 0;
    subImageY = 0;
    subImageWidth = image.getWidth();
    subImageHeight = image.getHeight();
    shiftX = 0;
    shiftY = 0;
  }
  
  @Override
  //This isn't used traditionally becuase warps are not added to updatables/drawables.
  //They are handled by the player using them
  public boolean shouldBeRemoved() {
    return subImageWidth  > image.getWidth() || 
           subImageHeight > image.getHeight();
  }
 
  /**
   * Right to Left WARP exit
   */
  public static class Left extends WarpExit{

    public Left(short connection, short id, short x, short y, 
                short lockx, short locky, short warpx, short warpy) {
      super(connection, id, (short)(x - Tile.SIZE - startOffSetX + 2), (short)(y - startOffSetY), lockx, locky, warpx, warpy);
      
    }
    
    @Override
    public void init(Player player){
      super.init(player.getImage());
      
      shiftX = Sprite.IMAGE_WIDTH;
      subImageWidth = 0;
    }

    @Override
    public void update(float timeDif_ms) {
      float pixelDif = WARP_VELOCITY*timeDif_ms;
      
      shiftX -= pixelDif; 
      subImageWidth += pixelDif;
    }
  }
  
  /**
   * Left to Right WARP exit
   */
  public static class Right extends WarpExit{

    public Right(short connection, short id, short x, short y, 
                short lockx, short locky, short warpx, short warpy) {
      super(connection, id, (short)(x + Tile.SIZE - startOffSetX), (short)(y - startOffSetY), lockx, locky, warpx, warpy);
      
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
  }
  
  /**
   * Left to Right WARP exit
   */
  public static class Up extends WarpExit{

    public Up(short connection, short id, short x, short y, 
                short lockx, short locky, short warpx, short warpy) {
      super(connection, id, (short)(x - startOffSetX), (short)(y - Tile.SIZE-1), lockx, locky, warpx, warpy);
      
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
  }
  
  /**
   * Left to Right WARP exit
   */
  public static class Down extends WarpExit{

    public Down(short connection, short id, short x, short y, 
                short lockx, short locky, short warpx, short warpy) {
      super(connection, id, (short)(x - startOffSetX), (short)(y + Tile.SIZE - startOffSetY + 1), lockx, locky, warpx, warpy);
      
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
  }
}
