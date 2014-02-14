package smw.world.blocks;

import smw.entity.Player;

public  class FlipBlock extends AnimatedBlock{
  final float ANIMATION_LENGTH = 10000; //10 seconds
  float timeAnimationRunning;
  
  public FlipBlock(int x, int y){
    super(x, y, "flipblock.png");
    
    //For this block, the animation is running if the block
    //is inneffective (ie hidden)
    running = false;
  }
  
  @Override
  public void update(float timeDif_ms) {
    timeAnimationRunning += timeDif_ms;
    
    if(timeAnimationRunning > ANIMATION_LENGTH){
      running = false;
      tileSheetX = 0;
    }
    
    super.update(timeDif_ms);
  }
  
  @Override
  public float collideWithLeft(Player player, float newX) {
    if(running){
      return newX;
    }
    
    return super.collideWithLeft(player, newX);
  }

  @Override
  public float collideWithRight(Player player, float newX) {
    if(running){
      return newX;
    }
    
    return super.collideWithRight(player, newX);
  }

  @Override
  public float collideWithTop(Player player, float newY) {
    if(running){
      return newY;
    }
    
    return super.collideWithTop(player, newY);
  }

  @Override
  public float collideWithBottom(Player player, float newY) {    
    if(running){
      return newY;
    }
    else{
      running = true;
      timeAnimationRunning = 0;
    }
    
    return super.collideWithBottom(player, newY);
  }
}

