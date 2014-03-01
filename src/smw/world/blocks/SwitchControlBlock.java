package smw.world.blocks;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import smw.Game;
import smw.Updatable;
import smw.entity.Player;
import smw.world.Tile;

public abstract class SwitchControlBlock extends SolidBlock implements Updatable {
  final List<SwitchBlock> blocksToChange;
  final int tileSheetYOff;
  
  final static float BUMP_LENGTH = 50; // 0.05 seconds
  float timeRunning;
  boolean bumped;

  public SwitchControlBlock(int colorType, int x, int y, List<SwitchBlock> blocksToChange) {
    super(x, y);
    
    tileSheetX = colorType*Tile.SIZE;
    tileSheetY =         0*Tile.SIZE;
    tileSheetYOff = tileSheetY + Tile.SIZE;
    
    this.blocksToChange = blocksToChange;
  }

  @Override
  public float collideWithBottom(Player player, float newY) {
    if(!bumped){
      bumped = true;
      timeRunning = 0;
      
      Game.soundPlayer.sfxSwitchPress();
    }
    
    return super.collideWithBottom(player, newY);
  }
  
  @Override
  public void update(float timeDif_ms) {
    if(bumped){
      timeRunning += timeDif_ms;
      
      if(timeRunning > BUMP_LENGTH){
        bumped = false;
      }
    }
  }
  
  public void setBlocks(boolean blocksOff){
    for(SwitchBlock block : blocksToChange){
      block.hidden = blocksOff;
    }
  }
  
  /**
   * Create a static class for each block switch and 
   * give it its own list of blocks to update.
   */
  public static class Red extends SwitchControlBlock{
    static final List<SwitchBlock> blocksToChange = new LinkedList<SwitchBlock>();
    public static boolean blocksOn = false;
    
    public Red(int colorType, int x, int y) {
      super(colorType, x, y, blocksToChange);
    }
    
    @Override
    public float collideWithBottom(Player player, float newY){
      blocksOn = !blocksOn;
      setBlocks(blocksOn);
      return super.collideWithBottom(player, newY);
    }
    
    @Override
    BufferedImage getImage(){
      return tileSheet.getTileImg(tileSheetX, (blocksOn) ? tileSheetYOff : tileSheetY);
    }
    
    public static void registerBlock(SwitchBlock block){
      blocksToChange.add(block);
    }
  }
  
  public static class Green extends SwitchControlBlock{
    static final List<SwitchBlock> blocksToChange = new LinkedList<SwitchBlock>();
    public static boolean blocksOn = false;
    
    public Green(int colorType, int x, int y) {
      super(colorType, x, y, blocksToChange);
    }
    
    @Override
    public float collideWithBottom(Player player, float newY){
      blocksOn = !blocksOn;
      setBlocks(blocksOn);
      return super.collideWithBottom(player, newY);
    }
    
    @Override
    BufferedImage getImage(){
      return tileSheet.getTileImg(tileSheetX, (blocksOn) ? tileSheetYOff : tileSheetY);
    }
    
    public static void registerBlock(SwitchBlock block){
      blocksToChange.add(block);
    }
  }
  
  public static class Yellow extends SwitchControlBlock{
    static final List<SwitchBlock> blocksToChange = new LinkedList<SwitchBlock>();
    public static boolean blocksOn = false;
    
    public Yellow(int colorType, int x, int y) {
      super(colorType, x, y, blocksToChange);
    }
    
    @Override
    public float collideWithBottom(Player player, float newY){
      blocksOn = !blocksOn;
      setBlocks(blocksOn);
      return super.collideWithBottom(player, newY);
    }
    
    @Override
    BufferedImage getImage(){
      return tileSheet.getTileImg(tileSheetX, (blocksOn) ? tileSheetYOff : tileSheetY);
    }
    
    public static void registerBlock(SwitchBlock block){
      blocksToChange.add(block);
    }
  }
  
  public static class Blue extends SwitchControlBlock{
    static final List<SwitchBlock> blocksToChange = new LinkedList<SwitchBlock>();
    public static boolean blocksOn = false;
    
    public Blue(int colorType, int x, int y) {
      super(colorType, x, y, blocksToChange);
    }
    
    @Override
    public float collideWithBottom(Player player, float newY){
      blocksOn = !blocksOn;
      setBlocks(blocksOn);
      return super.collideWithBottom(player, newY);
    }
    
    @Override
    BufferedImage getImage(){
      return tileSheet.getTileImg(tileSheetX, (blocksOn) ? tileSheetYOff : tileSheetY);
    }
    
    public static void registerBlock(SwitchBlock block){
      blocksToChange.add(block);
    }
  }
}
