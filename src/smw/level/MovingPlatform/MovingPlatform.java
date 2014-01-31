package smw.level.MovingPlatform;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import smw.level.Level;
import smw.level.Path;
import smw.level.TileSetTile;

public class MovingPlatform {
  Path path;
  TileSetTile[][] tiles;
  
  public MovingPlatform(TileSetTile[][] tiles, Path path){
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
       TileSetTile tile = tiles[i][j];
       //Sometimes there is a gap in the middle
       if(tile.ID >= 0){
         graphics.drawImage(Level.tileSet.getTileImg(tile.col, tile.row), x, y, observer);
       }
       y += Level.TILE_SIZE;
     }
     
     x += Level.TILE_SIZE;
    }
  }
}
