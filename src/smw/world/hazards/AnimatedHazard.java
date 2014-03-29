package smw.world.hazards;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import smw.Collidable;
import smw.Drawable;
import smw.Updatable;
import smw.entity.Player;
import smw.gfx.Sprite;
import smw.world.MovingCollidable;
import smw.world.TileSheet;
import smw.world.TileSheetManager;

@SuppressWarnings("serial")
public abstract class AnimatedHazard extends Collidable.Death implements MovingCollidable{
  TileSheet tileSheet = null;
  
  /** The time per frame. */
  protected final int updateRate_ms;
  /** The time that has elapsed for the current frame. */
  private int frameTime_ms = 0;
  /** Indicates if the animation is running. */
  protected boolean isAnimating = true;
  
  final int startingTileSheetX;
  final int startingTileSheetY;  
  
  int offsetX, offsetY = 0;
  
  //TODO left side
  public AnimatedHazard(int x, int y, String tileSheetFile, int width, int height, int timePerFrame, int tileSheetX, int tileSheetY) {
    super(x, y, width, height);
    
    tileSheet = TileSheetManager.getInstance().getTileSheet("gfx/packs/Classic/hazards/" + tileSheetFile);
    
    this.height = height;
    this.width  = width;
    updateRate_ms = timePerFrame; //TODO this should probably be a parameter
    startingTileSheetX = tileSheetX;
    startingTileSheetY = tileSheetY;
  }
  
  void reset(){
    frameTime_ms = 0;
  }

  @Override
  public void update(float timeDif_ms) {
    if (isAnimating) {
      frameTime_ms += timeDif_ms;
      
      if (frameTime_ms >= updateRate_ms) {
        frameTime_ms = 0;
        nextFrame();
      }
    }
  }
  
  @Override
  public float collideX(Player player, float newX) {
    if(isAnimating && intersects(newX, player.y, Sprite.IMAGE_WIDTH, Sprite.IMAGE_HEIGHT)){
      player.death();
    }
    
    return newX;
  }

  @Override
  public float collideY(Player player, float newX, float newY) {
    if(isAnimating && intersects(newX, newY, Sprite.IMAGE_WIDTH, Sprite.IMAGE_HEIGHT)){
      player.death();
    }
    
    return newY;
  }
  
  @Override
  public void draw(Graphics2D g, ImageObserver io) {
    if(isAnimating){
      g.drawImage(getImage(), (int)x, (int)y, io);
    }
  }
  
  BufferedImage getImage(){
    return tileSheet.getTileImg(startingTileSheetX + offsetX, startingTileSheetY + offsetY, (int)width, (int)height);
  }
  
  //This is the method to override for sheets that don't have 
  //exact across the top animations
  public abstract void nextFrame();
  
}
