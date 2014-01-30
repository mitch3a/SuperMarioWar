package smw.level;

public class MovingPlatform {
  Path path;
  Tiles[][] tiles;
  
  public MovingPlatform(Tiles[][] tiles, Path path){
    this.path = path;
    this.tiles = tiles.
  }
  
  public void move(long timeDif){
    path.move(timeDif);
  }
  
  public void draw((Graphics2D graphics, ImageObserver observer){
    int startX = path.getX();
    int y = path.getY();
    
    for(int i = 0 ; i < tiles.length ; ++i){
     x = startX;
    
     for(int j = 0 ; j < tiles[i].length ; ++j){
       graphics.drawImage(tiles[i][j].getImage(), x, y, observer);
       x += TileSize;
     }
     
     y += TileSize;
    }
  }
}
