package smw.world.blocks;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;

import smw.Game;
import smw.Updatable;
import smw.entity.Player;
import smw.gfx.Sprite;
import smw.ui.screen.GameFrame;
import smw.world.MovingCollidable;
import smw.world.Tile;
import smw.world.MovingPlatform.StraightSegmentPath;

public class DonutBlock extends SolidBlock implements Updatable, MovingCollidable{
  public boolean isFalling;
  boolean playerOnTop;
  float playerOnTopTime;
  //TODO if the velocity goes higher the straight segment breaks
  static final float SHAKE_VELOCITY = 0.05f;
  final StraightSegmentPath path = new StraightSegmentPath(SHAKE_VELOCITY, x - 1, y, x + 1, y);
  static final float MAX_TIME_PLAYER_ON_TOP_WITHOUT_FALLING = 1500f;//1.5 seconds

  
  float fallVelocity = 0.0f;
  final float FALL_ACCELERATION = 0.001f;
  //TODO should be using path for all the falling
  float lastDy = 0;
  
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
      Game.world.movingCollidables.add(this);
    }
    
    if(playerOnTop && !isFalling){
      path.move(timeDif_ms);
    }
    else if(isFalling){
      lastDy = fallVelocity*timeDif_ms;
      move(0, lastDy);
      fallVelocity += FALL_ACCELERATION*timeDif_ms;
    }
    
    playerOnTop = false;
  }

  //TODO this won't kill anyone on the way down
  @Override
  public float collideWithLeft(Player player, float newX){
    return (isFalling) ? newX : super.collideWithLeft(player, newX);
  }
  
  @Override
  public float collideWithRight(Player player, float newX){     
    return (isFalling) ? newX : super.collideWithRight(player, newX);
  }
    
  @Override
  public float collideWithTop(Player player, float newY){
    playerOnTop = true;
    
    return (isFalling) ? newY : super.collideWithTop(player, newY);
  }
    
  @Override
  public float collideWithBottom(Player player, float newY){
    return (isFalling) ? newY : super.collideWithBottom(player, newY);
  }
  
  @Override
  public void draw(Graphics2D g, ImageObserver io) {
    if(playerOnTop && !isFalling){
      g.drawImage(getImage(), path.getX(),path.getY(), io);
    }
    else{
      g.drawImage(getImage(), (int)x, (int)y, io);
    }
  }

  //TODO mk these below are based on the players current position. Probably
  //     not a big deal but worth noting here
  @Override
  public float collideX(Player player, float newX) {
    if(this.intersects(player)){
      player.death();
    }
    
    return newX;
  }

  @Override
  public float collideY(Player player, float newX, float newY) {
    if(this.intersects(newX, newY, Sprite.IMAGE_WIDTH, Sprite.IMAGE_HEIGHT)){
      if(this.intersects(newX, player.y, Sprite.IMAGE_WIDTH, Sprite.IMAGE_HEIGHT)){
        player.death();
      }
      else{
        //Player is on top
        return super.collideWithTop(player, newY + lastDy);
      }
    }
    
    return newY;
  }

  @Override
  public boolean shouldBeRemoved() {
    return (y > GameFrame.res_height);
  }
  
  @Override
  public boolean willDrag(){
    return isFalling;
  }
}
