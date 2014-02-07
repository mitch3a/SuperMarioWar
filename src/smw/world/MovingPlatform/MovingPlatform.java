package smw.world.MovingPlatform;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import smw.Collidable;
import smw.Drawable;
import smw.Updatable;
import smw.world.Tile;
import smw.world.Tile.TileType;

public class MovingPlatform implements Drawable, Updatable{
  Path path;
  Tile[][] tiles;
  Collidable[][] collidables; //TODO really don't like this
  
  public MovingPlatform(Tile[][] tiles, Collidable[][] collidables, Path path){
    this.path = path;
    this.tiles = tiles;
    this.collidables = collidables;
  }
 
  public int getX(){
    return path.getX();
  }
  
  public int getY(){
    return path.getY();
  }
  
  public void draw(Graphics2D graphics, ImageObserver observer){
    int startY = path.getY();
    int x = path.getX();
    
    for(int i = 0 ; i < tiles.length ; ++i){
     int y = startY;
    
     for(int j = 0 ; j < tiles[i].length ; ++j){
       Tile tile = tiles[i][j];
       //Sometimes there is a gap in the middle
       if(tile.hasImage()){
         graphics.drawImage(tile.getImage(), x, y, observer);
       }
       y += Tile.SIZE;
     }
     
     x += Tile.SIZE;
    }
    /* TODO mk want something like this but would need to pass the x, y of path down somehow
    for(int i = 0 ; i < tiles.length ; ++i){
      for(int j = 0 ; j < tiles[i].length ; ++j){
        tiles[i][j].draw(graphics, observer);
      }
    }
    */
  }

  public TileType getTile(int x, int y) {
    int difX = x - path.getX();
    int difY = y - path.getY();
    
    int indexX = (difX >= 0) ? difX/Tile.SIZE : -1;
    int indexY = (difY >= 0) ? difY/Tile.SIZE : -1;
    
    if(indexX >= 0 && indexX < tiles.length){
      if(indexY >= 0 && indexY < tiles[indexX].length){
        return collidables[indexX][indexY].type;
      }
    }
    
    return Tile.TileType.NONSOLID;
  }

  public int getXChange() {
    return path.getXChange();
  }

  @Override
  public void update(float timeDif_ms) {
    timeDif_ms = 1.0f;//TODO
    path.move(timeDif_ms);
  }
}
