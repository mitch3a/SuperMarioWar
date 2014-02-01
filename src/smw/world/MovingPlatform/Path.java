package smw.world.MovingPlatform;

public interface Path{
  public abstract void move(float timeDif);
  public abstract int getX();
  public abstract int getY();
}
