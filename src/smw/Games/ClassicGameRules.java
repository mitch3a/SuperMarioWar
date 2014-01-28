package smw.Games;

import smw.Game;
import smw.entity.Player;
import smw.gfx.Sprite;
import smw.level.Level;
import smw.level.Tile;

public class ClassicGameRules extends GameRules{
  
  final int[] livesLeft;
  
  public ClassicGameRules(int startingLives, int numPlayers){
    livesLeft = new int[numPlayers];
    for(int i = 0 ; i < numPlayers ; ++i){
      livesLeft[i] = startingLives;
    }
  }

  @Override
  public void update(Player player) {
    //Only updates are handled during collide players
  }

  @Override
  public void collide(Level level, Player player) {
    int newX = (int) (player.x + player.dx);
    int newY = (int) (player.y + player.dy);

    if (level.getTileTypeAtPx(newX + Level.TILE_SIZE, newY) != Tile.NONSOLID) {
      if (player.intersects(newX, newY, Level.TILE_SIZE, Level.TILE_SIZE)) {
        player.dx = 0;
        System.out.println("X1");
        player.physics.collideWithWall();
      }
    } 
    
    if (level.getTileTypeAtPx(newX - 3, newY) != Tile.NONSOLID) {
      if (player.intersects(newX, newY, Level.TILE_SIZE, Level.TILE_SIZE)) {
        player.dx = 0;
        System.out.println("X2");
        player.physics.collideWithWall();
      }
    }
    
    if (level.getTileTypeAtPx(player.x + player.dx, newY + Level.TILE_SIZE) != Tile.NONSOLID) {
      if (player.intersects(player.x, newY, Level.TILE_SIZE, Level.TILE_SIZE)) {
        player.dy = 0;
        player.physics.collideWithFloor();
      }
    }
  }

  @Override
  public void collidePlayers(Player p1, Player p2) {
    if(p1.crushed || p2.crushed){
      return;
    }
    /*
    boolean xCollide = false;

    int newX1 = p1.x + p1.dx;
    int newY1 = p1.y + p1.dy;
    
    int newX2 = p2.x + p2.dx;
    int newY2 = p2.y + p2.dy;

    if(p1.intersects(newX2, p2.y, Sprite.IMAGE_WIDTH, Sprite.IMAGE_HEIGHT)){
      //Collision so put them next to each other
      if(p1.x > newX2){
        p2.dx = p2.x - Sprite.IMAGE_WIDTH - 1;
      }
      else{
        p2.dx = p2.x + Sprite.IMAGE_HEIGHT + 1;
      }
      
      p1.dx = 0;
      xCollide = true;
    }
    else if(p2.intersects(newX1, p1.y, Sprite.IMAGE_WIDTH, Sprite.IMAGE_HEIGHT)){
      //Collision so put them next to each other
      if(p2.x > newX1){
        p1.dx = p1.x - Sprite.IMAGE_WIDTH - 1;
      }
      else{
        p1.dx = p1.x + Sprite.IMAGE_HEIGHT + 1;
      }
      
      p2.dx = 0;
      xCollide = true; 
    }

    if(p1.intersects(p2.x, newY2, Sprite.IMAGE_WIDTH, Sprite.IMAGE_HEIGHT)){
      if(!xCollide){
        if(p1.getY() < newY2){
          --livesLeft[p2.playerIndex];
          p2.crush();
        }
        else{
          --livesLeft[p1.playerIndex];
          p1.crush();
        }
      }
      p2.physics.collideWithFloor();
      p2.dy = 0;
    }
    else if(p2.intersects(p1.x, newY1, Sprite.IMAGE_WIDTH, Sprite.IMAGE_HEIGHT)){
      if(!xCollide){
        if(p2.getY() < newY1){
          --livesLeft[p1.playerIndex];
          p1.crush();
        }
        else{
          --livesLeft[p2.playerIndex];
          p2.crush();
        }
      }
      p1.physics.collideWithFloor();
      p1.dy = 0;
    }
    */
  }

  @Override
  public boolean isGameOver() {
    int count = 0;
    for(int lives : livesLeft){
      if(lives < 1){
        count++;
      }
    }
    
    if(count == livesLeft.length){
      //TODO something bad happened
    }
    
    return (count == 1);
  }

}
