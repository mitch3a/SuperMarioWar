package smw.gfx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;

import smw.entity.Player;

public class Scoreboard {
	Player[] players;
	final int topY;
	final int shadeWidth = 150;
	final int shadeHeight = 50;
	Rectangle[] shadeRects;
	Color shadeColor;
	
	public Scoreboard(Player[] players, int topY){
		this.players = players;
		this.topY = topY;
		
		shadeRects = new Rectangle[players.length];
		
		for(int i = 0 ; i < players.length ; ++i){
			shadeRects[i] = new Rectangle(50 + i*200, topY + 10, shadeWidth, shadeHeight);
		}
		
		shadeColor = new Color(0, 0, 0, 150);
	}
	
	private void drawPlayerScore(Graphics2D graphics, Image image, int score, int startingX, int shadeRectIndex, ImageObserver observer){
		graphics.fill(shadeRects[shadeRectIndex]);
		graphics.drawImage(image, startingX, topY, observer);
		startingX += image.getWidth(observer) + 10;
		Font font = Font.getInstance();
		font.drawScore(graphics, score, startingX, topY, observer);
	}
	
	public void draw(Graphics2D graphics, ImageObserver observer){
		int x = 50;
		graphics.setColor(shadeColor);
		for(int i = 0 ; i < players.length ; ++i){
			drawPlayerScore(graphics, players[i].getImage(), players[i].getScore(), x, i, observer);
			x += 200;
		}
	}
}
