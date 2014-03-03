package smw.world.warps;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import smw.Drawable;
import smw.Updatable;
import smw.entity.Player;

public abstract class WarpBase implements Updatable, Drawable{
  static final float WARP_VELOCITY = 0.02f;
  public enum Direction{
    LEFT, RIGHT, UP, DOWN;
  }
  
  public final int x, y;
  BufferedImage image;
  
  float subImageX, subImageY, subImageWidth, subImageHeight, shiftX, shiftY;
  
  public final short connection;
  public final short id;
  
  public WarpBase(int x, int y, short connection, short id){
    this.x = x;
    this.y = y;
    this.connection = connection;
    this.id = id;
  }
  
  public float getX(){
    return x + shiftX;
  }
  
  public float getY(){
    return y + shiftY;
  }
  
  public abstract void init(Player player);
  
  @Override
  public void draw(Graphics2D g, ImageObserver io) {
    int width  = (int)subImageWidth;
    int height = (int)subImageHeight;
    
    if(width != 0 && height != 0){
      g.drawImage(image.getSubimage((int)subImageX, (int)subImageY, width, height), (int)(x + shiftX), (int)(y + shiftY), io);
    }
  }
}
