package smw.gfx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

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
	ArrayList<WinnerText> winnerTextList = new ArrayList<WinnerText>();
	long timeLastWinnerBorn;
	Player winningPlayer = null;
	
	public Scoreboard(Player[] players){
	  final int topY = 25; //Top of everything
	  final int shadeWidth = 100; //width of shaded area
	  final int shadeHeight = 44; //hight of shaded area
	  final int widthSpace = 20; //space in between shaded areas
	  final int spriteXOffset = 6; //offset from top left corner of shaded area
	  final int spriteYOffset = 6; //offset from top left corner of shaded area
	  final int scoreXOffset = 2*spriteXOffset + 32; //offset from top left corner of shaded area
	  final int scoreYOffset = 14; //offset from top left corner of shaded area
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
	
	public void update(){
	  int count = 0;
	  for(Player p : players){
	    if(!p.isOut()){
	      ++count;
	    }
	  }
	  
	  if(winningPlayer == null){
	    if(count == 1){
	      for(Player p : players){
	        if(!p.isOut()){
	          winningPlayer = p; 
	          break;
	        }
	      }
	    }
	  }
	  else{
  	  long currentTime = System.currentTimeMillis();
  	  if(currentTime > timeLastWinnerBorn + WinnerText.TIME_BETWEEN_WINNERS_MS){
  	    WinnerText temp = new WinnerText((int)winningPlayer.x, winningPlayer.y);
  	    winnerTextList.add(temp);
  	    
  	    if(temp.x < 0){
  	      winnerTextList.add(new WinnerText((int)(winningPlayer.x + GameFrame.res_width), winningPlayer.y));
  	    }
  	    else if(temp.x + WinnerText.WINNER_TEXT_WIDTH > GameFrame.res_width){
  	      winnerTextList.add(new WinnerText((int)(winningPlayer.x - GameFrame.res_width), winningPlayer.y));
  	    }
  	    timeLastWinnerBorn = currentTime;
  	  }
  	    
      if(winnerTextList != null && !winnerTextList.isEmpty()){
        for(int i = winnerTextList.size() - 1 ; i >= 0 ; --i){
          WinnerText temp = winnerTextList.get(i);
          temp.update();
          if(temp.shouldBeRemoved()){
            winnerTextList.remove(i);
          }
        }
      }
	  }
	}
	
	public void draw(Graphics2D graphics, ImageObserver observer){
    Font font = Font.getInstance();
		graphics.setColor(shadeColor);
		for(int i = 0 ; i < players.length ; ++i){
		  Player p = players[i];
		  if (p != null) {
		    int score = Math.max(p.getScore(), 0);
  		  graphics.fill(shadeRects[i]);
  	    graphics.drawImage(p.getImage(), spriteStart[i].x, spriteStart[i].y, observer);
  	    font.drawScore(graphics, score, scoreStart[i].x, scoreStart[i].y, observer);
		  }
		}
		
		//TODO NOT THE PLACE FOR THIS
		update();
		
		if(winnerTextList != null && !winnerTextList.isEmpty()){
      for(WinnerText text : winnerTextList){
        text.draw(graphics, observer);
      }
    }
	}
}
