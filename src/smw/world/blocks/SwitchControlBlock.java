package smw.world.blocks;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import smw.Game;
import smw.entity.Player;
import smw.world.Tile;

public abstract class SwitchControlBlock extends SolidBlock {
  final List<SwitchBlock> blocksToChange;
  final int tileSheetYOff;
  boolean blocksOff;

  public SwitchControlBlock(int colorType, int x, int y, List<SwitchBlock> blocksToChange) {
    super(x, y);
    
    tileSheetX = colorType*Tile.SIZE;
    tileSheetY =         0*Tile.SIZE;
    tileSheetYOff = tileSheetY + Tile.SIZE;
    
    this.blocksToChange = blocksToChange;
    blocksOff = false;
  }

  @Override
  public int collideWithBottom(Player player, int newY) {
    Game.soundPlayer.sfxSwitchPress();
    blocksOff = !blocksOff;
    
    //TODO don't love this but works for now
    for(SwitchBlock block : blocksToChange){
      block.hidden = blocksOff;
    }
    
    return super.collideWithBottom(player, newY);
  }
  
  @Override
  BufferedImage getImage(){
    return tileSheet.getTileImg(tileSheetX, (blocksOff) ? tileSheetYOff : tileSheetY);
  }
  
  /**
   * Create a static class for each block switch and 
   * give it its own list of blocks to update
   */
  public static class Red extends SwitchControlBlock{
    static final List<SwitchBlock> blocksToChange = new LinkedList<SwitchBlock>();
    
    public Red(int colorType, int x, int y) {
      super(colorType, x, y, blocksToChange);
    }
    
    public static void registerBlock(SwitchBlock block){
      blocksToChange.add(block);
    }
  }
  
  public static class Green extends SwitchControlBlock{
    static final List<SwitchBlock> blocksToChange = new LinkedList<SwitchBlock>();
    
    public Green(int colorType, int x, int y) {
      super(colorType, x, y, blocksToChange);
    }
    
    public static void registerBlock(SwitchBlock block){
      blocksToChange.add(block);
    }
  }
  
  public static class Yellow extends SwitchControlBlock{
    static final List<SwitchBlock> blocksToChange = new LinkedList<SwitchBlock>();
    
    public Yellow(int colorType, int x, int y) {
      super(colorType, x, y, blocksToChange);
    }
    
    public static void registerBlock(SwitchBlock block){
      blocksToChange.add(block);
    }
  }
  
  public static class Blue extends SwitchControlBlock{
    static final List<SwitchBlock> blocksToChange = new LinkedList<SwitchBlock>();
    
    public Blue(int colorType, int x, int y) {
      super(colorType, x, y, blocksToChange);
    }
    
    public static void registerBlock(SwitchBlock block){
      blocksToChange.add(block);
    }
  }
}
