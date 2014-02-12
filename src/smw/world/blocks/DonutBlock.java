package smw.world.blocks;

import smw.Updatable;
import smw.entity.Player;
import smw.world.Tile;

public class DonutBlock extends SolidBlock implements Updatable{
  public boolean isFalling;
  boolean playerOnTop;
  float playerOnTopTime;
  float MAX_TIME_PLAYER_ON_TOP_WITHOUT_FALLING = 2000f;//2 seconds
  float fallVelocity = 0.0f;
  final float FALL_ACCELERATION = 0.01f;
  
  public DonutBlock(int x, int y) {
    super(x, y);
    
    tileSheetX = 2*Tile.SIZE;
    tileSheetY = 0*Tile.SIZE;
    
    isFalling = false;
    playerOnTop = false;
    playerOnTopTime = 0;
  }

  @Override
  public void update(float timeDif_ms) {
    if(playerOnTop){
      playerOnTopTime += timeDif_ms;
    }
    else{
      playerOnTopTime = 0;
    }
    
    if(playerOnTopTime > MAX_TIME_PLAYER_ON_TOP_WITHOUT_FALLING){
      playerOnTop = false;
      isFalling = true;
    }
    
    if(playerOnTop && !isFalling){
      //TODO shake
    }
    else if(isFalling){
      y += fallVelocity*timeDif_ms;
      fallVelocity += FALL_ACCELERATION*timeDif_ms;
    }
    
    playerOnTop = false;
  }

  //TODO this won't kill anyone on the way down
  @Override
  public int collideWithLeft(Player player, int newX){
    return (isFalling) ? newX : super.collideWithLeft(player, newX);
  }
  
  @Override
  public int collideWithRight(Player player, int newX){     
    return (isFalling) ? newX : super.collideWithRight(player, newX);
  }
    
  @Override
  public int collideWithTop(Player player, int newY){
    playerOnTop = true;
    
    return (isFalling) ? newY : super.collideWithTop(player, newY);
  }
    
  @Override
  public int collideWithBottom(Player player, int newY){
    return (isFalling) ? newY : super.collideWithBottom(player, newY);
  }
}
