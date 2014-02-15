package smw.world.blocks;

import smw.Updatable;
import smw.world.Tile;

public abstract class AnimatedBlock extends SolidBlock implements Updatable{
  /** The frame rate of the animation. */
  protected int frameRate;
  /** The time per frame. */
  protected int updateRate_ms;
  /** Time it takes for a complete animation (all frames). */
  protected int animationTime_ms;
  /** The total time that has elapsed (for all frames). */
  private int elapsedTime_ms;
  /** The time that has elapsed for the current frame. */
  private int frameTime_ms;
  /** Indicates if the animation is running. */
  protected boolean running = true;
  
  public AnimatedBlock(int x, int y, String tileSheet) {
    super(x, y, tileSheet);
    
    frameRate = 4;
    updateRate_ms = 1000/frameRate;
    
    animationTime_ms = updateRate_ms*this.tileSheet.getWidth()/Tile.SIZE;
  }

  @Override
  public void update(float timeDif_ms) {
    if (!running) {
      return;
    }
    
    elapsedTime_ms += timeDif_ms;
    frameTime_ms += timeDif_ms;
    
    if (elapsedTime_ms >= animationTime_ms) {
      elapsedTime_ms = 0;
      frameTime_ms = 0;
      tileSheetX = 0;
    }
    
    if (frameTime_ms >= updateRate_ms) {
      frameTime_ms = 0;
      tileSheetX += Tile.SIZE;
    }
  }
  
  public static class BreakableBlock extends AnimatedBlock{
    public BreakableBlock(int x, int y){
      super(x, y, "breakableblock.png");
    }
  }
  
  public static class BlueThrowBlock extends AnimatedBlock{
    public BlueThrowBlock(int x, int y){
      super(x, y, "throwblock.png");
    }
  }
  
  public static class WhiteThrowBlock extends AnimatedBlock{
    public WhiteThrowBlock(int x, int y){
      super(x, y, "throwblock.png");
      this.tileSheetY += Tile.SIZE;
    }
  }
  
  public static class RedThrowBlock extends AnimatedBlock{
    public RedThrowBlock(int x, int y){
      super(x, y, "throwblock.png");
      this.tileSheetY += 2*Tile.SIZE;
    }
  }
}
