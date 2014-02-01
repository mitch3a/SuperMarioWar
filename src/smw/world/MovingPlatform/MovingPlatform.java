package smw.world.MovingPlatform;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import smw.world.Tile;

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
}
