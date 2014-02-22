package smw.menu;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import smw.ui.Keyboard;
import smw.ui.PlayerControlBase;

/** Base menu class for all menus in game. */
public abstract class Menu {
  
  public abstract void draw(Graphics2D g, ImageObserver io);
  
  public abstract void update(boolean up, boolean down, boolean left, boolean right, boolean select, boolean esc);
  
  public abstract void update(double deltaTime_ms, Keyboard kb);
}
