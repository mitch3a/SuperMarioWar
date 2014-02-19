package smw.gfx;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import smw.Game;
import smw.Drawable;
import smw.ui.screen.GameFrame;

//TODO mk its obviously more involved than this but wanted something
//     so that if sound was off, you'd still realize it was paused (and 
//     not think everything was broken!
public class PauseDisplay implements Drawable{
  public static final String pauseString = "Paused";
  public static final BufferedImage pauseImage = Font.getInstance().getLargeText(pauseString);
  public static final int X = (GameFrame.res_width - pauseImage.getWidth() )/2;
  public static final int Y = (GameFrame.res_height- pauseImage.getHeight())/2;

  
  public PauseDisplay(){
    
  }
  
  public boolean shouldBeRemoved(){
    return !Game.paused;
  }

  
  public void draw(Graphics2D graphics, ImageObserver observer){
    graphics.drawImage(pauseImage, X, Y, observer);
  }
}
