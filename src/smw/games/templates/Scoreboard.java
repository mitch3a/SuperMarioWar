package smw.games.templates;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

/**********************************************
 * This class is in charge of drawing drawing 
 * the score. It should eventually be pulled
 * out into an interface for different games
 *********************************************/
public abstract class Scoreboard {
	public abstract void draw(Graphics2D graphics, ImageObserver observer);
}
