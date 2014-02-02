package smw.world.MovingPlatform;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import smw.world.Tile;
import smw.world.Tile.TileType;

public class MovingPlatform {
  Path path;
  Tile[][] tiles;
  
  public MovingPlatform(Tile[][] tiles, Path path){
    this.path = path;
    this.tiles = tiles;
  }
  
  public void move(float timeDif){
    path.move(timeDif);
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
       if(tile.ID >= 0){
         graphics.drawImage(tile.getImage(), x, y, observer);
       }
       y += Tile.SIZE;
     }
     
     x += Tile.SIZE;
    }
  }

  public TileType getTile(int x, int y) {
    int difX = x - path.getX();
    int difY = y - path.getY();
    
    int indexX = (difX >= 0) ? difX/Tile.SIZE : -1;
    int indexY = (difY >= 0) ? difY/Tile.SIZE : -1;
    
    if(indexX >= 0 && indexX < tiles.length){
      if(indexY >= 0 && indexY < tiles[indexX].length){
        return tiles[indexX][indexY].specialTile.type;
      }
    }
    
    return Tile.TileType.NONSOLID;
  }

  public int getXChange() {
    return path.getXChange();
  }
}
