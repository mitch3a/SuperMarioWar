package smw.world.Structures;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import smw.Drawable;
import smw.Updatable;
import smw.world.Tile;

public abstract class Warp implements Updatable, Drawable{
  
  final int x, y;
  BufferedImage image;
  
  float subImageX, subImageY, subImageWidth, subImageHeight, shiftX, shiftY;
  
  public final short connection;
  public final short id;
  
  private static final float WARP_VELOCITY = 0.02f;
  
  public Warp(int column, int row, short connection, short id){
    this.x = column*Tile.SIZE;
    this.y = row*Tile.SIZE;
    this.connection = connection;
    this.id = id;
  }
  
  public void init(BufferedImage image){
    this.image = image;
    subImageX = 0;
    subImageY = 0;
    subImageWidth = image.getWidth();
    subImageHeight = image.getHeight();
    shiftX = 0;
    shiftY = 0;
  }
  
  public boolean exitWorks(WarpExit warpExit) {
    return (connection == warpExit.connection);
  }
  
  @Override
  //This isn't used traditionally becuase warps are not added to updatables/drawables.
  //They are handled by the player using them
  public boolean shouldBeRemoved() {
    return subImageWidth <= 0 || subImageHeight <= 0;
  }
  
  @Override
  public void draw(Graphics2D g, ImageObserver io) {
    g.drawImage(image.getSubimage((int)subImageX, (int)subImageY, (int)subImageWidth, (int)subImageHeight), (int)(x + shiftX), (int)(y + shiftY), io);
  }

  /**
   * LEFT WARP
   */
  public static class Left extends Warp{

    public Left(int column, int row, short connection, short id) {
      super(column, row, connection, id);
    }
    
    @Override
    public void init(BufferedImage image){
      super.init(image);
      
      shiftX = Tile.SIZE;
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
  public static class Right extends Warp{

    public Right(int column, int row, short connection, short id) {
      super(column, row, connection, id);
    }
    
    @Override
    public void init(BufferedImage image){
      super.init(image);
      
      shiftX = -Tile.SIZE;
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
  public static class Up extends Warp{

    public Up(int column, int row, short connection, short id) {
      super(column, row, connection, id);
    }
    
    @Override
    public void init(BufferedImage image){
      super.init(image);
      
      shiftY = Tile.SIZE;
    }

    @Override
    public void update(float timeDif_ms) {
      float pixelDif = WARP_VELOCITY*timeDif_ms;
      
      shiftY += pixelDif; 
      subImageHeight -= pixelDif;
    }
  }
  
  /**
   * Down WARP
   */
  public static class Down extends Warp{

    public Down(int column, int row, short connection, short id) {
      super(column, row, connection, id);
    }

    @Override
    public void init(BufferedImage image){
      super.init(image);
      
      shiftY = -Tile.SIZE + 1;
    }
    
    @Override
    public void update(float timeDif_ms) {
      float pixelDif = WARP_VELOCITY*timeDif_ms;
      
      subImageY += pixelDif; 
      subImageHeight -= pixelDif;
    }
  }

  //TODO remove both of these
  public enum Direction{
    LEFT, RIGHT, UP, DOWN;
  }

  public static Direction getOppositeDirection(short i) {
    Direction result;
    
    switch(i){
      case 0:  result = Direction.UP;
               break;
      case 1:  result = Direction.RIGHT;
               break;
      case 2:  result = Direction.DOWN;
               break;
      default: result = Direction.LEFT;
    }
    
    return result;
  }
}
