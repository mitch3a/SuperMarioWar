package smw.world.hazards;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.util.LinkedList;
import java.util.List;

import smw.entity.Player;
import smw.world.MovingPlatform.EllipticalPath;

public class RotoDisc  extends AnimatedHazard{
  
  final List<EllipticalPath> paths = new LinkedList<EllipticalPath>();
  static final int SIZE = 32;
  
  public RotoDisc(int centerX, int centerY, float velocity, float angle, float radius, int numDiscs) {
    super(centerX - SIZE/2, centerY - SIZE/2, "rotodisc.png", SIZE, SIZE, 100, 0, 0);
   
    velocity = velocity/16; // velocity (i believe this is based on framerate not time so convert that here)
    double angleDif = 2*Math.PI/numDiscs;
    
    for(int i = 0 ; i < numDiscs ; ++i){
      paths.add(new EllipticalPath(velocity, angle, radius, radius, centerX, centerY));
      angle += angleDif;
    }
  }

  @Override
  public float collideX(Player player, float newX) {
    //Let CollideY take care of it
    return newX;
  }

  @Override
  public float collideY(Player player, float newX, float newY) {
    Rectangle2D.Float temp = new Rectangle2D.Float(newX, newY, Player.WIDTH, Player.HEIGHT);
    
    for(EllipticalPath path : paths){
      if(temp.intersects(path.getX(), path.getY(), SIZE, SIZE)){
        player.death();
        return newY;
      }
    }
    
    return newY;
  }
  
  @Override
  public void update(float timeDif_ms) {
    super.update(timeDif_ms);
    
    for(EllipticalPath path : paths){
      path.move(timeDif_ms);
    }
  }
  
  @Override
  public void draw(Graphics2D g, ImageObserver io) {
    for(EllipticalPath path : paths){
      g.drawImage(getImage(), (int)path.getX(), (int)path.getY(), io);
    }
  }
  
  @Override
  public boolean shouldBeRemoved() {
    return false;
  }

  @Override
  public void nextFrame() {
    offsetX = (offsetX + SIZE)%tileSheet.getWidth();
  }

  @Override
  public boolean kills(Player player) {    
    for(EllipticalPath path : paths){
      if(player.intersects(path.getX(), path.getY(), SIZE, SIZE)){
        return true;
      }
    }
    
    return false;
  }
}