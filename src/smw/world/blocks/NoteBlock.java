package smw.world.blocks;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import smw.Game;
import smw.entity.Player;
import smw.world.Tile;
import smw.world.MovingPlatform.StraightSegmentPath;

public class NoteBlock extends AnimatedBlock {
  static final float PIXELS_TO_MOVE = 16;
  static final float HIT_BLOCK_STARTING_VELOCITY = 0.5f;
  boolean isHit;
  StraightSegmentPath path;
  float timeElapsed;
  
  public NoteBlock(int x, int y, String tileSheet) {
    super(x, y, tileSheet);
    
    //To avoid any other collide with grounds
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
      g.drawImage(getImage(), path.getX(), path.getY(), io);
    }
    else{
      super.draw(g, io);
    }
  }
  
  @Override
  public int collideWithLeft(Player player, int newX) {
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
  public int collideWithRight(Player player, int newX) {
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
  public int collideWithTop(Player player, int newY) {
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
  public int collideWithBottom(Player player, int newY) {
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
    public BlueNoteBlock(int x, int y){
      super(x, y, "noteblock.png");
    }
  }
  
  public static class WhiteNoteBlock extends NoteBlock{
    public WhiteNoteBlock(int x, int y){
      super(x, y, "noteblock.png");
      this.tileSheetY += Tile.SIZE;
    }
  }
  
  public static class RedNoteBlock extends NoteBlock{
    public RedNoteBlock(int x, int y){
      super(x, y, "noteblock.png");
      this.tileSheetY += 2*Tile.SIZE;
    }
  }
}
