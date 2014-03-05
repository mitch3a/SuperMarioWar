package smw.world;

import smw.entity.Player;

public interface MovingCollidable {
  public abstract float collideX(Player player, float newX);
  public abstract float collideY(Player player, float newX, float newY); 
  public abstract boolean shouldBeRemoved();
  public abstract boolean kills(Player player);
}
