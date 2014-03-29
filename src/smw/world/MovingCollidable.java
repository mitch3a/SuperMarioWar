package smw.world;

import smw.Drawable;
import smw.Updatable;
import smw.entity.Player;

public interface MovingCollidable extends Updatable, Drawable{
  public abstract float collideX(Player player, float newX);
  public abstract float collideY(Player player, float newX, float newY); 
  public abstract boolean shouldBeRemoved();
  /** Returns true if the player should not spawn at the given point **/
  public abstract boolean kills(Player player);
}
