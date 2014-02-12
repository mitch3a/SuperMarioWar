package smw.menu;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

/** Base menu class for all menus in game. */
public abstract class Menu {
  
  public abstract void draw(Graphics2D g, ImageObserver io);
  
  public abstract void update();
}
