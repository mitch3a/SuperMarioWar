package smw.world.warps;

import java.awt.image.BufferedImage;

import smw.entity.Player;
import smw.world.Tile;

public abstract class WarpEntrance extends WarpBase{
  public final Direction direction;
  
  public WarpEntrance(int column, int row, short connection, short id, Direction direction){
    super(column*Tile.SIZE, row*Tile.SIZE, connection, id);
    this.direction = direction;
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
    return subImageWidth <= 0  || 
           subImageHeight <= 0 ;
  }
  
  public boolean exitWorks(WarpExit warpExit) {
    return (connection == warpExit.connection);
  }

  /**
   * LEFT WARP
   */
  public static class Left extends WarpEntrance{

    public Left(int column, int row, short connection, short id) {
      super(column, row, connection, id, Direction.LEFT);
    }
    
    @Override
    public void init(Player player){
      super.init(player.getImage());
      
      shiftX = Tile.SIZE;
      shiftY = y - player.y;
    }

    @Override
    public void update(float timeDif_ms) {
      float pixelDif = WARP_VELOCITY*timeDif_ms;
      
      subImageX += pixelDif; 
      subImageWidth -= pixelDif;
    }
  }
  
  /**
   * Right WARP
   */
  public static class Right extends WarpEntrance{

    public Right(int column, int row, short connection, short id) {
      super(column, row, connection, id, Direction.RIGHT);
    }
    
    @Override
    public void init(Player player){
      super.init(player.getImage());
      
      shiftX = -Tile.SIZE;
      shiftY = y - player.y;
    }

    @Override
    public void update(float timeDif_ms) {
      float pixelDif = WARP_VELOCITY*timeDif_ms;
      
      shiftX += pixelDif; 
      subImageWidth -= pixelDif;
    }
  }
  
  /**
   * Up WARP
   */
  public static class Up extends WarpEntrance{

    public Up(int column, int row, short connection, short id) {
      super(column, row, connection, id, Direction.UP);
    }

    @Override
    public void init(Player player){
      super.init(player.getImage());
      
      shiftY = Tile.SIZE;
      shiftX = player.x - x;
    }


    @Override
    public void update(float timeDif_ms) {
      float pixelDif = WARP_VELOCITY*timeDif_ms;
      
      subImageY += pixelDif; 
      subImageHeight -= pixelDif;
    }
  }
  
  /**
   * Down WARP
   */
  public static class Down extends WarpEntrance{

    public Down(int column, int row, short connection, short id) {
      super(column, row, connection, id, Direction.DOWN);
    }
    
    @Override
    public void init(Player player){
      super.init(player.getImage());
      
      shiftY = -Tile.SIZE + 1;
      shiftX = player.x - x;
    }
    
    @Override
    public void update(float timeDif_ms) {
      float pixelDif = WARP_VELOCITY*timeDif_ms;
      
      shiftY += pixelDif; 
      subImageHeight -= pixelDif;
    }
  }
}
