package smw.world.blocks;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import smw.Game;
import smw.entity.Player;
import smw.world.Tile;
import smw.world.MovingPlatform.StraightSegmentPath;

public class NoteBlock extends AnimatedBlock {
  /**
   * 
   */
  private static final long serialVersionUID = 1033602744934518447L;
  static final float PIXELS_TO_MOVE = 16;
  static final float HIT_BLOCK_STARTING_VELOCITY = 0.5f;
  boolean isHit;
  StraightSegmentPath path;
  float timeElapsed;
  
  //TODO mk found a bug where if the floor is same height of block and you stand
  //     on the right side (left half on the note and right half on the solid ground)
  //     then something whacky happens. found in MrMister_Hill.map
  public NoteBlock(int x, int y, String tileSheet) {
    super(x, y, tileSheet);
    
    //To avoid any other collide with grounds
    //TODO mk might not need (all) of this with the ints->floats change
    left   -= 1;
    right  += 1;
    top    -= 1;
    bottom += 1;
    
    isHit = false;
  }
  
  @Override
  public void update(float timeDif_ms) {
    if(isHit){
      //TODO this should rely on the actual time
      timeElapsed += timeDif_ms;
      
      path.move(timeDif_ms);
      if(path.timeToCycle <= timeElapsed){
        isHit = false;
      }
    }
    
    super.update(timeDif_ms);
  }
  
  @Override
  public void draw(Graphics2D g, ImageObserver io) {
    if(isHit){
      g.drawImage(getImage(), (int)path.getX(), (int)path.getY(), io);
    }
    else{
      super.draw(g, io);
    }
  }
  
  @Override
  public float collideWithLeft(Player player, float newX) {
    if(!isHit){
      path = new StraightSegmentPath(HIT_BLOCK_STARTING_VELOCITY, x, y, x + PIXELS_TO_MOVE, y);
      timeElapsed = 0;
    }
    
    isHit = true;
    Game.soundPlayer.sfxBump();
    
    player.physics.collideWithNoteBlockRight();
    return left;
  }

  @Override
  public float collideWithRight(Player player, float newX) {
    if(!isHit){
      path = new StraightSegmentPath(HIT_BLOCK_STARTING_VELOCITY, x, y, x - PIXELS_TO_MOVE, y);
      timeElapsed = 0;
    }
    
    isHit = true;
    Game.soundPlayer.sfxBump();
    
    player.physics.collideWithNoteBlockLeft();
    return right;
  }

  @Override
  public float collideWithTop(Player player, float newY) {
    if(!isHit){
      path = new StraightSegmentPath(HIT_BLOCK_STARTING_VELOCITY, x, y, x , y + PIXELS_TO_MOVE);
      timeElapsed = 0;
    }
    
    isHit = true;
    Game.soundPlayer.sfxBump();
    
    //TODO dif sound/velocity for color note
    player.physics.collideWithNoteBlockBottom();
    return top;
  }

  @Override
  public float collideWithBottom(Player player, float newY) {
    if(!isHit){
      path = new StraightSegmentPath(HIT_BLOCK_STARTING_VELOCITY, x, y, x, y - PIXELS_TO_MOVE);
      timeElapsed = 0;
    }
    
    isHit = true;
    Game.soundPlayer.sfxBump();
    
    player.physics.collideWithNoteBlockTop();
    return bottom;
  }

  public static class BlueNoteBlock extends NoteBlock{
    /**
     * 
     */
    private static final long serialVersionUID = -8269677930369680889L;

    public BlueNoteBlock(int x, int y){
      super(x, y, "noteblock.png");
    }
  }
  
  public static class WhiteNoteBlock extends NoteBlock{
    /**
     * 
     */
    private static final long serialVersionUID = -1136426417874019400L;

    public WhiteNoteBlock(int x, int y){
      super(x, y, "noteblock.png");
      this.tileSheetY += Tile.SIZE;
    }
  }
  
  public static class RedNoteBlock extends NoteBlock{
    /**
     * 
     */
    private static final long serialVersionUID = -1866002606577333198L;

    public RedNoteBlock(int x, int y){
      super(x, y, "noteblock.png");
      this.tileSheetY += 2*Tile.SIZE;
    }
  }
}
