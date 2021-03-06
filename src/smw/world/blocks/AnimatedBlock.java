package smw.world.blocks;

import smw.Updatable;
import smw.world.Tile;

public abstract class AnimatedBlock extends SolidBlock implements Updatable{
  /**
   * 
   */
  private static final long serialVersionUID = -5354802552307677322L;
  /** The frame rate of the animation. */
  protected final int frameRate;
  /** The time per frame. */
  protected final int updateRate_ms;
  /** The time that has elapsed for the current frame. */
  private int frameTime_ms = 0;
  /** Indicates if the animation is running. */
  protected boolean running = true;
  
  
  public AnimatedBlock(int x, int y, String tileSheet) {
    super(x, y, tileSheet);
    
    frameRate = 4;
    updateRate_ms = 1000/frameRate;
  }

  @Override
  public void update(float timeDif_ms) {
    if (!running) {
      return;
    }
    
    frameTime_ms += timeDif_ms;
    
    if (frameTime_ms >= updateRate_ms) {
      frameTime_ms = 0;
      tileSheetX = (tileSheetX + Tile.SIZE) % tileSheet.getWidth();
    }
  }
  
  public static class BreakableBlock extends AnimatedBlock{
    /**
     * 
     */
    private static final long serialVersionUID = -5968784647438272852L;

    public BreakableBlock(int x, int y){
      super(x, y, "breakableblock.png");
    }
  }
  
  public static class BlueThrowBlock extends AnimatedBlock{
    /**
     * 
     */
    private static final long serialVersionUID = 1345599248127189587L;

    public BlueThrowBlock(int x, int y){
      super(x, y, "throwblock.png");
    }
  }
  
  public static class WhiteThrowBlock extends AnimatedBlock{
    /**
     * 
     */
    private static final long serialVersionUID = -3377620674213230781L;

    public WhiteThrowBlock(int x, int y){
      super(x, y, "throwblock.png");
      this.tileSheetY += Tile.SIZE;
    }
  }
  
  public static class RedThrowBlock extends AnimatedBlock{
    /**
     * 
     */
    private static final long serialVersionUID = 3524258128958248601L;

    public RedThrowBlock(int x, int y){
      super(x, y, "throwblock.png");
      this.tileSheetY += 2*Tile.SIZE;
    }
  }
}
