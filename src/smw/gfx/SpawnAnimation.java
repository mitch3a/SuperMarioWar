package smw.gfx;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import smw.Drawable;
import smw.Game;
import smw.Updatable;
import smw.gfx.Palette.ColorScheme;
import smw.world.Tile;
import smw.world.TileSheet;
import smw.world.TileSheetManager;
import smw.world.MovingPlatform.SpiralPath;

public class SpawnAnimation implements Updatable, Drawable{
  static final TileSheet tileSheet = TileSheetManager.getInstance().getTileSheet("gfx/packs/Classic/eyecandy/spawnsmoke.png").applyAlpha(120);
  static final float TIME_BETWEEN_NEW_DOTS = 40.0f; 
  static final float VELOCITY = 1.0f;
  static final float NUM_ROTATIONS = 0.5f;
  static final float RADIUS = Tile.SIZE*7/2;
  
  List<SpawnAnimationPair> spawnPairs = new LinkedList<SpawnAnimationPair>();
  final ColorScheme color;
  final float x, y;
  
  float timeSinceLastDotCreated = 0;
  float nextSize = 0.0f;
  
  public SpawnAnimation(int x, int y, ColorScheme color){   
    this.x = (float)(x);
    this.y = (float)(y);
    
    this.color = color;
    
    nextBatch();
  }
  
  void nextBatch(){
    nextSize += 0.5f;
    int sizeToUse = (int)nextSize;
    
    spawnPairs.add(new SpawnAnimationPair(x, y, color, sizeToUse));
  }
  
  @Override
  public void draw(Graphics2D g, ImageObserver io) {
    for(SpawnAnimationPair pair : spawnPairs){
      g.drawImage(tileSheet.getTileImg(pair.tileSheetX, pair.tileSheetY), (int)pair.path.getX(),    (int)pair.path.getY(),    io);
      g.drawImage(tileSheet.getTileImg(pair.tileSheetX, pair.tileSheetY), pair.path.getX90(),  pair.path.getY90(),  io);
      g.drawImage(tileSheet.getTileImg(pair.tileSheetX, pair.tileSheetY), pair.path.getX180(), pair.path.getY180(), io);
      g.drawImage(tileSheet.getTileImg(pair.tileSheetX, pair.tileSheetY), pair.path.getX270(), pair.path.getY270(), io);
    }
  }

  @Override
  public void update(float timeDif_ms) {    
    Iterator<SpawnAnimationPair> iter = spawnPairs.iterator();
    while(iter.hasNext()){
      SpawnAnimationPair temp = iter.next();
      temp.path.move(timeDif_ms);
      if(temp.path.isDone){
        iter.remove();
      }
    }
    
    timeSinceLastDotCreated += timeDif_ms;
    if(nextSize < 3.5f && TIME_BETWEEN_NEW_DOTS <= timeSinceLastDotCreated){
      nextBatch();
      timeSinceLastDotCreated = 0f;
    }
  }
  
  private class SpawnAnimationPair{
    final SpiralPath path;
    final int tileSheetX;
    final int tileSheetY;
    
    public SpawnAnimationPair(float x, float y, ColorScheme color, int size){
      path = new SpiralPath.RegularVelocity(VELOCITY, 0, NUM_ROTATIONS, RADIUS, x, y);
      tileSheetX = Tile.SIZE*size;
      tileSheetY = Tile.SIZE*color.index;
    }
  }

  @Override
  public boolean shouldBeRemoved() {
    return (spawnPairs.size() == 0);
  }
}
