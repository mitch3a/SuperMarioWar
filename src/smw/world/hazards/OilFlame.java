package smw.world.hazards;

import smw.Game;

@SuppressWarnings("serial")
public abstract class OilFlame extends AnimatedHazard{
  //The smaller images wiggle, so really you have
  // small-a, small-b, small-a, small-b, big-a, big-b, big-a, big-b then that backwards
  int stage = 0;
  int[] offSet = {0, 32, 0, 32, 0, 32, 0, 32, 64, 96, 64, 96, 64, 96, 64, 96, 64, 96, 64, 96, 64, 96, 64, 96, 0, 32, 0, 32, 0, 32, 0, 32};
  final float totalFlameOffTime = 3000f;// 3 seconds TODO i think this is semi-random
  float flameOffTime;
  
  public OilFlame(int x, int y, int totalWidth, int totalHeight, int tileSheetX, int tileSheetY) {
    super(x, y, "flame.png", totalWidth, totalHeight, 50, 4, tileSheetX, tileSheetY);
  }
  
  @Override
  public void update(float timeDif_ms) {
    super.update(timeDif_ms);
    
    if(!isAnimating){
      flameOffTime += timeDif_ms;
      
      if(flameOffTime >= totalFlameOffTime){
        isAnimating = true;
        stage = 0;
        reset();
      }
    }
  }

  @Override
  public boolean shouldBeRemoved() {
    return false;
  }
  
  public void nextFrame(){
    stage += 1;
    
    if(stage == 1){
      Game.soundPlayer.sfxFlameCannon();
    }
    
    if(stage >= offSet.length){
      isAnimating = false;
      flameOffTime = 0;
    }
  }
  
  public static abstract class OilFlameHorizontal extends OilFlame{
    
    public OilFlameHorizontal(int x, int y, int tileSheetX, int tileSheetY) {
      super(x, y, 96, 32, tileSheetX, tileSheetY);
    }
    
    public void nextFrame(){
      super.nextFrame();
      
      if(isAnimating){
        offsetY = offSet[stage];
      }
    }
  }
  
  public static class OilFlameLeftToRight extends OilFlameHorizontal{

    public OilFlameLeftToRight(int x, int y) {
      super(x, y, 0, 0);
    }
  }
  
  public static class OilFlameRightToLeft extends OilFlameHorizontal{

    public OilFlameRightToLeft(int x, int y) {
      super(x - 64, y, 96, 0); //x, y given is for the rightmost rile
    }
  }
  
  public static abstract class OilFlameVertical extends OilFlame{
    
    public OilFlameVertical(int x, int y, int tileSheetX, int tileSheetY) {
      super(x, y, 32, 96, tileSheetX, tileSheetY);
    }
    
    public void nextFrame(){
      super.nextFrame();
      
      if(isAnimating){
        offsetX = offSet[stage];
      }
    }
  }
  
  public static class OilFlameBottomToTop extends OilFlameVertical{

    public OilFlameBottomToTop(int x, int y) {
      super(x, y, 96, 96);
    }
  }
  
  public static class OilFlameTopToBottom extends OilFlameVertical{
    
    public OilFlameTopToBottom(int x, int y) {
      super(x, y, 0, 96);
    }
  }
}