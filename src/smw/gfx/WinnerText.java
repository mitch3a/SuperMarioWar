package smw.gfx;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import smw.ui.screen.GameFrame;

public class WinnerText {
  private static final float WINNER_TEXT_ACCELERATION =.5f;
  private static final float WINNER_TEXT_STARTING_VELOCITY = -10;
  public static final long TIME_BETWEEN_WINNERS_MS = 300;
  public static final String winningString = "Winner!";
  public static final BufferedImage winnerImage = Font.getInstance().getLargeText(winningString);
  public static final int WINNER_TEXT_WIDTH = winnerImage.getWidth();
  float y;
  final int x;
  float velocityY;
  
  public WinnerText(int x, float y){
    this.x = x - WINNER_TEXT_WIDTH/2;
    this.y = y;
    velocityY = WINNER_TEXT_STARTING_VELOCITY;
  }
  
  public boolean shouldBeRemoved(){
    return y > GameFrame.res_height;//TODO should NOT be hardcoded
  }
  
  public void update(){
    y += velocityY;
    velocityY += WINNER_TEXT_ACCELERATION;
  }
  
  public void draw(Graphics2D graphics, ImageObserver observer){
    graphics.drawImage(winnerImage, x, (int) y, observer);
  }
}
