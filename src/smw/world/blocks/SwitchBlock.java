package smw.world.blocks;

import java.awt.image.BufferedImage;

import smw.entity.Player;
import smw.world.Tile;

/*
 * These blocks can be hidden/nonsolid. They are controlled
 * by SwitchControlBlocks
 */
public class SwitchBlock extends SolidBlock{
  boolean hidden;
  final int subImageYHidden;

  public SwitchBlock(int type, int x, int y) {
    super(x, y);

    subImageX = type*Tile.SIZE;
    subImageY =    0*Tile.SIZE;

    subImageYHidden = subImageY + Tile.SIZE;
    this.hidden = false;
  }
  
  public void setHidden(boolean value){
    hidden = value;
  }
  
  @Override
  public int collideWithLeft(Player player, int newX) {
    if(hidden){
      return newX;
    }
    
    return super.collideWithLeft(player, newX);
  }

  @Override
  public int collideWithRight(Player player, int newX) {
    if(hidden){
      return newX;
    }
    
    return super.collideWithRight(player, newX);
  }

  @Override
  public int collideWithTop(Player player, int newY) {
    if(hidden){
      return newY;
    }
    
    return super.collideWithTop(player, newY);
  }

  @Override
  public int collideWithBottom(Player player, int newY) {    
    if(hidden){
      return newY;
    }
    
    return super.collideWithBottom(player, newY);
  }
  
  @Override
  BufferedImage getImage(){
    return tileSheet.getTileImg(subImageX, (hidden) ? subImageYHidden : subImageY);
  }
}
