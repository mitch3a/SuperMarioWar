package smw.world.blocks;

import smw.entity.Player;
import smw.world.Tile;

public class QuestionBlock extends AnimatedBlock{
  final float ANIMATION_LENGTH = 10000; //10 seconds
  float timeRunning;
  
  public QuestionBlock(int x, int y){
    super(x, y, "powerupblock.png");
    
    //For this block, the animation is running if the block
    //has not yet been hit
    running = true;
  }
  
  @Override
  public void update(float timeDif_ms){
    if(!running){
      timeRunning += timeDif_ms;
      
      if(timeRunning > ANIMATION_LENGTH){
        running = true;
        subImageX = 0;
        subImageY = 0;
      }
    }

    super.update(timeDif_ms);
  }

  @Override
  public int collideWithBottom(Player player, int newY) {    
    if(running){
      running = false;
      subImageX = 0;
      subImageY = Tile.SIZE;
      timeRunning = 0;
    }
    
    return super.collideWithBottom(player, newY);
  }
}
