package smw.world.hazards;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;

import smw.entity.Player;
import smw.gfx.Sprite;
import smw.world.MovingPlatform.EllipticalPath;

public class FireBallPole extends AnimatedHazard{

  final int numBalls;
  final static int spaceBetweenBalls = 28;
  
  final EllipticalPath path;
  
  public FireBallPole(int x, int y, int numBalls, float velocity) {
    //(32-18)/2 = 6 -> to center it
    super(x + 6, y + 6, "fireball.png", 18, 18, 100, 4, 0, 0);
    
    this.numBalls = numBalls;
   
    path = new EllipticalPath(velocity/16, // velocity (i believe this is based on framerate not time so convert that here)
        0, //angle, 
        spaceBetweenBalls, //radiusX, 
        spaceBetweenBalls, //radiusY, 
        0f, // centerX, 
        0f); //float centerY)
  }

  @Override
  public float collideX(Player player, float newX) {
    //Let CollideY take care of it
    return newX;
  }

  @Override
  public float collideY(Player player, float newX, float newY) {
    Rectangle2D.Float temp = new Rectangle2D.Float(newX, newY, Sprite.IMAGE_WIDTH, Sprite.IMAGE_HEIGHT);
    
    float currentX = x;
    float currentY = y;
    //TODO could use floats here but prolly no biggy
    int xForBalls = path.getX();
    int yForBalls = path.getY();
    
    for(int i = 0 ; i < numBalls ; ++i){
      if(temp.intersects(currentX, currentY, width, height)){
        player.death();
        return newY;
      }
      currentX += xForBalls;
      currentY += yForBalls;
    }
    
    return newY;
  }
  
  @Override
  public void update(float timeDif_ms) {
    super.update(timeDif_ms);
    
    path.move(timeDif_ms);
  }
  
  @Override
  public void draw(Graphics2D g, ImageObserver io) {
    if(isAnimating){
      int currentX = (int)x;
      int currentY = (int)y;
      int xForBalls = path.getX();
      int yForBalls = path.getY();
      
      for(int i = 0 ; i < numBalls ; ++i){
        g.drawImage(getImage(), currentX, currentY, io);
        currentX += xForBalls;
        currentY += yForBalls;
      }
    }
  }
  
  @Override
  public boolean shouldBeRemoved() {
    return false;
  }

  @Override
  public void nextFrame() {
    //TODO all this casting is probably not worth it
    offsetX = (offsetX + (int)width)%((int)tileSheet.getWidth());
  }
}
