package smw;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public interface Drawable  {
  public void draw(Graphics2D g, ImageObserver io);
  public abstract boolean shouldBeRemoved();
}
