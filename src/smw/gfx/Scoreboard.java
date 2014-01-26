package smw.gfx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.ImageObserver;

import smw.entity.Player;
import smw.ui.screen.GameFrame;

/**********************************************
 * This class is in charge of drawing drawing 
 * the score. It should eventually be pulled
 * out into an interface for different games
 *********************************************/
public class Scoreboard {
	Player[] players;
	RoundRectangle2D[] shadeRects;
	Point[] spriteStart;
	Point[] scoreStart;
	Color shadeColor;
	
	public Scoreboard(Player[] players){
	  final int topY = 40; //Top of everything
	  final int shadeWidth = 100; //width of shaded area
	  final int shadeHeight = 50; //hight of shaded area
	  final int widthSpace = 20; //space in between shaded areas
	  final int spriteXOffset = 10; //offset from top left corner of shaded area
	  final int spriteYOffset = 10; //offset from top left corner of shaded area
	  final int scoreXOffset = 2*spriteXOffset + 26; //offset from top left corner of shaded area
	  final int scoreYOffset = 10 + 8; //offset from top left corner of shaded area
	  final float arcWidth = 15.0f;
	  final float arcHeight = 15.0f;
	  
		this.players = players;
		shadeRects = new RoundRectangle2D[players.length];
		spriteStart = new Point[players.length];
		scoreStart = new Point[players.length];
		
		int start = (int) ((GameFrame.res_width/2) - (1.5*widthSpace + (2*shadeWidth)));
		
		for(int i = 0 ; i < players.length ; ++i){
			shadeRects[i] = new RoundRectangle2D.Float((float)start, (float)topY, (float)shadeWidth, 
			                                           (float)shadeHeight, arcWidth, arcHeight);
	    spriteStart[i] = new Point();
	    scoreStart[i] = new Point();
			spriteStart[i].x = start + spriteXOffset;
			spriteStart[i].y = topY + spriteYOffset;
			scoreStart[i].x = start + scoreXOffset;
			scoreStart[i].y = topY + scoreYOffset;
		  start += shadeWidth + widthSpace;
		}
		
		shadeColor = new Color(0, 0, 0, 120);
	}
	
	public void draw(Graphics2D graphics, ImageObserver observer){
    Font font = Font.getInstance();
		graphics.setColor(shadeColor);
		for(int i = 0 ; i < players.length ; ++i){
		  Player p = players[i];
		  if (p != null) {
  		  graphics.fill(shadeRects[i]);
  	    graphics.drawImage(p.getImage(), spriteStart[i].x, spriteStart[i].y, observer);
  	    font.drawScore(graphics, p.getScore(), scoreStart[i].x, scoreStart[i].y, observer);
		  }
		}
	}
}
