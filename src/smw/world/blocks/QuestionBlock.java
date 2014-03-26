package smw.world.blocks;

import smw.Game;
import smw.entity.Player;
import smw.world.Tile;

public class QuestionBlock extends AnimatedBlock{
  /**
   * 
   */
  private static final long serialVersionUID = 8112440979454055356L;
  final float ANIMATION_LENGTH = 10000; //10 seconds
  final static float BUMP_LENGTH = 100; // 0.1 seconds
  float timeRunning;
  boolean bumped;
  
  public QuestionBlock(int x, int y){
    super(x, y, "powerupblock.png");
    
    //For this block, the animation is running if the block
    //has not yet been hit
    running = true;
    bumped = false;
    timeRunning = 0;
  }
  
  @Override
  public void update(float timeDif_ms){
    timeRunning += timeDif_ms;
    
    if(!running){
      
      
      if(timeRunning > ANIMATION_LENGTH){
        running = true;
        tileSheetX = 0;
        tileSheetY = 0;
      } 
    }

    if(bumped && timeRunning > BUMP_LENGTH){
      bumped = false;
    }
    
    super.update(timeDif_ms);
  }

  @Override
  public float collideWithBottom(Player player, float newY) {    
    if(!bumped && running){
      bumped = true;
      timeRunning = 0;
    }
    
    if(running){
      running = false;
      tileSheetX = 0;
      tileSheetY = Tile.SIZE;
      timeRunning = 0;
      Game.soundPlayer.sfxBump();
    }
    
    return super.collideWithBottom(player, newY);
  }
  
  @Override
  public float collideWithTop(Player player, float newY) {    
    if(bumped){
      player.death();
      return newY;
    }
    
    return super.collideWithTop(player, newY);
  }
}
