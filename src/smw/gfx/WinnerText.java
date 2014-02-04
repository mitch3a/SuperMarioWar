package smw.gfx;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

public class WinnerText {
  
  private static final float WINNER_TEXT_ACCELERATION =.5f;
  private static final float WINNER_TEXT_STARTING_VELOCITY = -10;
  public static final long TIME_BETWEEN_WINNERS_MS = 300;
  public static final String winningString = "Winner!";
  float y;
  final int x;
  float velocityY;
  
  public WinnerText(int x, int y){
    this.x = x;
    this.y = (float)y;
    velocityY = WINNER_TEXT_STARTING_VELOCITY;
  }
  
  public boolean shouldBeRemoved(){
    return y > 480;//TODO should NOT be hardcoded
  }
  
  public void update(){
    y += velocityY;
    velocityY += WINNER_TEXT_ACCELERATION;
  }
  
  public void draw(Graphics2D graphics, ImageObserver observer){
    Font font = Font.getInstance();
    font.drawLargeText(graphics, winningString, x, (int) y, observer);
  }
}
