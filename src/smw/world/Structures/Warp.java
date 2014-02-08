package smw.world.Structures;

import smw.world.Tile;

public class Warp {
  public enum Direction{
    LEFT, RIGHT, UP, DOWN;
  }
  
  public Warp(int column, int row){
    this.x = column*Tile.SIZE;
    this.y = row*Tile.SIZE;
  }
  
  public final int x, y;
  
  public Direction direction;
  public short connection;
  public short id;
  
  public static Direction getDirection(int i){
    Direction result;
    
    switch(i){
      case 0:  result = Direction.DOWN;
               break;
      case 1:  result = Direction.LEFT;
               break;
      case 2:  result = Direction.UP;
               break;
      default: result = Direction.RIGHT;
    }
    
    return result;
  }

  public boolean exitWorks(WarpExit warpExit) {
    return (connection == warpExit.connection);
  }

  public static Direction getOppositeDirection(short i) {
    Direction result;
    
    switch(i){
      case 0:  result = Direction.UP;
               break;
      case 1:  result = Direction.RIGHT;
               break;
      case 2:  result = Direction.DOWN;
               break;
      default: result = Direction.LEFT;
    }
    
    return result;
  }
}
