package smw.level;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

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
       graphics.drawImage(Level.tileSet.getTileImg(tiles[i][j].col, tiles[i][j].row), x, y, observer);
       y += Level.TILE_SIZE;
     }
     
     x += Level.TILE_SIZE;
    }
  }
}
