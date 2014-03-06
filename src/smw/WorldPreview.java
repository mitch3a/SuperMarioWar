package smw;

import smw.world.World;

public class WorldPreview implements Updatable, Drawable { 
  private final World world;
  private final int x, y, width, height;
  
  public WorldPreview(String fileName, int x, int y, int width, int height){
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    
    //TODO we should really be throwing errors if the world returns nothing back)
    this.world = new World(fileName);
  }
  
  @Override
  public void update(float timeDif_ms){
    world.update(timeDif_ms);
  }
  
  @Override
  public void draw(Graphics2D g, ImageObserver io){
    
  }
  
  public boolean shouldBeRemoved(){
    return false; //TODO unless this class is used differently (ie not thrown in an updatable set)
  }
}
