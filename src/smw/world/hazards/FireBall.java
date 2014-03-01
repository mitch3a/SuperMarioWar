package smw.world.hazards;

import smw.entity.Player;
import smw.gfx.Sprite;

public class FireBall extends AnimatedHazard{
  
  public FireBall(int x, int y) {
    //(32-18)/2 = 6 -> to center it
    super(x + 6, y + 6, "fireball.png", 18, 18, 100, 4, 0, 0);
  }

  @Override
  public float collideX(Player player, float newX, float newY) {
    //Let CollideY take care of it
    return newX;
  }

  @Override
  public float collideY(Player player, float newX, float newY) {
    if(intersects(newX, newY, Sprite.IMAGE_WIDTH, Sprite.IMAGE_HEIGHT)){
      player.death();
      return newY;
    }
    
    return newY;
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
