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
  final int tileSheetYHidden;

  public SwitchBlock(int type, int x, int y) {
    super(x, y);

    tileSheetX = type*Tile.SIZE;
    tileSheetY =    0*Tile.SIZE;

    tileSheetYHidden = tileSheetY + Tile.SIZE;
    this.hidden = false;
  }
  
  public void setHidden(boolean value){
    hidden = value;
  }
  
  @Override
  public float collideWithLeft(Player player, float newX) {
    if(hidden){
      return newX;
    }
    
    return super.collideWithLeft(player, newX);
  }

  @Override
  public float collideWithRight(Player player, float newX) {
    if(hidden){
      return newX;
    }
    
    return super.collideWithRight(player, newX);
  }

  @Override
  public float collideWithTop(Player player, float newY) {
    if(hidden){
      return newY;
    }
    
    return super.collideWithTop(player, newY);
  }

  @Override
  public float collideWithBottom(Player player, float newY) {    
    if(hidden){
      return newY;
    }
    
    return super.collideWithBottom(player, newY);
  }
  
  @Override
  BufferedImage getImage(){
    return tileSheet.getTileImg(tileSheetX, (hidden) ? tileSheetYHidden : tileSheetY);
  }
}
