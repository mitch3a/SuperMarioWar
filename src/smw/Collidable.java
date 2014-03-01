package smw;

import java.awt.geom.Rectangle2D;

import smw.entity.Player;
import smw.gfx.Sprite;
import smw.ui.screen.GameFrame;
import smw.world.Tile;

//TODO mk as other objects are implemented, might want to replace Player with something generic
//     ALSO this should probably be an abstract type
public abstract class Collidable extends Rectangle2D.Float{
  //TODO mk i hate this enough to leave it as is to draw attention to itself for being awful
  //Converts the tile type into the flags that this tile carries (solid + ice + death, etc)
  static final short[] g_iTileTypeConversion = {0, 1, 2, 5, 121, 9, 17, 33, 65, 6, 21, 37, 69, 3961, 265, 529, 1057, 2113, 4096};
  
  protected float left;
  protected float right;
  protected float top;
  protected float bottom;
  
  public Collidable(int x, int y){
    this(x, y, Tile.SIZE, Tile.SIZE);
  }

  public Collidable(int x, int y, int width, int height) {
    super(x, y, width, height);
    
    calculateReturnValues();
  }
  
  public void move(float dx, float dy) {
    x = (x + dx + GameFrame.res_width) % GameFrame.res_width;
    y += dy;
    
    if(y < -64){
      y = (y + dy + GameFrame.res_height + 64) % GameFrame.res_height;
    }
    calculateReturnValues();
  }
  
  protected void calculateReturnValues(){
    left   = x - Tile.SIZE;
    right  = x + Tile.SIZE;
    top    = y - Tile.SIZE;
    bottom = y + Tile.SIZE;
  }

  /** by default, non-solid **/
  public float collideWithLeft(Player player, float newX){
    return newX;
  }
  
  /** by default, non-solid **/
  public float collideWithRight(Player player, float newX){
    return newX;
  }
  
  /** by default, non-solid **/
  public float collideWithTop(Player player, float newY){
    return newY;
  }

  /** by default, non-solid **/
  public float collideWithBottom(Player player, float newY){
    return newY;
  }
  
  /**
   *  this method is for moving platforms to let the caller know if
   *  it should move whatever is on top of it 
   */
  public boolean willDrag(){
    return false;
  }
  
  public static Collidable getCollideable(int type, int x, int y){
    switch(type) {
      case 0:  return new NonSolid(x, y);
      case 1:  return new Solid(x, y);
      case 2:  return new SolidOnTop(x, y);
      case 3:  return new Ice(x, y);
      case 4:  return new Death(x, y);
      case 5:  return new DeathOnTop(x, y);
      case 6:  return new DeathOnBottom(x, y);
      case 7:  return new DeathOnLeft(x, y);
      case 8:  return new DeathOnRight(x, y);
      //case 9: result = TileType.ICE_ON_TOP; break;
      case 10: return new IceDeathOnBottom(x, y);
      case 11: return new IceDeathOnLeft(x, y);
      case 12: return new IceDeathOnRight(x, y);
      case 13: return new SuperDeath(x, y);
      case 14: return new SuperDeathOnTop(x, y);
      case 15: return new SuperDeathOnBottom(x, y);
      case 16: return new SuperDeathOnLeft(x, y);
      case 17: return new SuperDeathOnRight(x, y);
      //case 18: result = TileType.PLAYER_DEATH; break;
      //case 19: result = TileType.GAP; break;
    }
    
    return new NonSolid(x, y);
  }
  
  /**
   * Non Solid
   */
  public static class NonSolid extends Collidable{
    public NonSolid(int x, int y) {
      super(x, y);
    }
  }
  
  /**
   * Solid
   */
  public static class Solid extends Collidable{
    
    public Solid(int x, int y) {
      super(x, y);
    }
    
    @Override
    public boolean willDrag(){
      return true;
    }

    @Override
    public float collideWithLeft(Player player, float newX){
      player.physics.collideWithWall();
      return left;
    }
    
    @Override
    public float collideWithRight(Player player, float newX){
        player.physics.collideWithWall();
        return right;
      }
      
    @Override
    public float collideWithTop(Player player, float newY){
      player.physics.collideWithFloor();
      return top;
    }
      
    @Override
    public float collideWithBottom(Player player, float newY){
      player.physics.collideWithCeiling();
      return bottom;
    }
  }
  
  /**
   * SolidOnTop
   */
  public static class SolidOnTop extends Collidable{
    public SolidOnTop(int x, int y) {
      super(x, y);
    }
    
    @Override
    public boolean willDrag(){
      return true;
    }
    
    @Override
    public float collideWithTop(Player player, float newY){
      //TODO just want to get moving platforms working right
      boolean wasAboveBefore = (player.y + Sprite.IMAGE_HEIGHT <= this.y + .01);//Rounding error
      if(player.canFall() && wasAboveBefore && player.physics.playerControl.isDown()){
        
      }
      else if(wasAboveBefore){
        player.physics.collideWithFloor();
        return top;
      }
      
      return newY;
    }
  }
  
  /**
   * Ice
   */
  public static class Ice extends Solid{
    public Ice(int x, int y) {
      super(x, y);
    }
    
    @Override
    public float collideWithTop(Player player, float newY){
      float result = super.collideWithTop(player, newY);
      player.slipOnIce();
      return result;
    }
  }
  
  /**
   * Death
   */
  public static class Death extends Collidable{
    
    public Death(int x, int y, int width, int height){
      super(x, y, width, height);
    }
    
    public Death(int x, int y) {
      super(x, y);
    }
    
    @Override
    public float collideWithTop(Player player, float newY){
      player.death();
      return newY;
    }
    
    @Override
    public float collideWithBottom(Player player, float newY){
      player.death();
      return newY;
    }
    
    @Override
    public float collideWithRight(Player player, float newX){
      player.death();
      return newX;
    }
    
    @Override
    public float collideWithLeft(Player player, float newX){
      player.death();
      return newX;
    }
  }
  
  /**
   * Death On Top
   */
  public static class DeathOnTop extends Solid{
    public DeathOnTop(int x, int y) {
      super(x, y);
    }
    
    @Override
    public float collideWithTop(Player player, float newY){
      player.death();
      return newY;
    }
  }
  
  /**
   * Death On Bottom
   */
  public static class DeathOnBottom extends Solid{
    public DeathOnBottom(int x, int y) {
      super(x, y);
    }
    
    @Override
    public float collideWithBottom(Player player, float newY){
      player.death();
      return newY;
    }
  }
  
  /**
   * Death On Right
   */
  public static class DeathOnRight extends Solid{
    public DeathOnRight(int x, int y) {
      super(x, y);
    }
    
    @Override
    public float collideWithRight(Player player, float newX){
      player.death();
      return newX;
    }
  }
  
  /**
   * Death On Left
   */
  public static class DeathOnLeft extends Solid{
    public DeathOnLeft(int x, int y) {
      super(x, y);
    }
    
    @Override
    public float collideWithLeft(Player player, float newY){
      player.death();
      return newY;
    }
  }
  
  /**
   * Super Death
   */
  public static class SuperDeath extends Collidable{
    public SuperDeath(int x, int y) {
      super(x, y);
    }
    
    @Override
    public float collideWithTop(Player player, float newY){
      player.superDeath();
      return newY;
    }
    
    @Override
    public float collideWithBottom(Player player, float newY){
      player.superDeath();
      return newY;
    }
    
    @Override
    public float collideWithRight(Player player, float newX){
      player.superDeath();
      return newX;
    }
    
    @Override
    public float collideWithLeft(Player player, float newX){
      player.superDeath();
      return newX;
    }
  }
  
  /**
   * Super Death On Top
   */
  public static class SuperDeathOnTop extends Solid{
    public SuperDeathOnTop(int x, int y) {
      super(x, y);
    }
    
    @Override
    public float collideWithTop(Player player, float newY){
      player.superDeath();
      return newY;
    }
  }
  
  /**
   * Super Death On Bottom
   */
  public static class SuperDeathOnBottom extends Solid{
    public SuperDeathOnBottom(int x, int y) {
      super(x, y);
    }
    
    @Override
    public float collideWithBottom(Player player, float newY){
      player.superDeath();
      return newY;
    }
  }
  
  /**
   * Super Death On Right
   */
  public static class SuperDeathOnRight extends Solid{
    public SuperDeathOnRight(int x, int y) {
      super(x, y);
    }
    
    @Override
    public float collideWithRight(Player player, float newX){
      player.superDeath();
      return newX;
    }
  }
  
  /**
   * Super Death On Left
   */
  public static class SuperDeathOnLeft extends Solid{
    public SuperDeathOnLeft(int x, int y) {
      super(x, y);
    }
    
    @Override
    public float collideWithLeft(Player player, float newY){
      player.superDeath();
      return newY;
    }
  }
  
  /**
   * Ice Death On Bottom
   */
  public static class IceDeathOnBottom extends Solid{
    public IceDeathOnBottom(int x, int y) {
      super(x, y);
    }
    
    @Override
    public float collideWithBottom(Player player, float newY){
      player.death(); //TODO verify this is correct (not super death or need its own) AND right/left same issue (next 2 classes)
      return newY;
    }
  }
  
  /**
   * Ice Death On Right
   */
  public static class IceDeathOnRight extends Solid{
    public IceDeathOnRight(int x, int y) {
      super(x, y);
    }
    
    @Override
    public float collideWithRight(Player player, float newX){
      player.death();
      return newX;
    }
  }
  
  /**
   * Ice Death On Left
   */
  public static class IceDeathOnLeft extends Solid{
    public IceDeathOnLeft(int x, int y) {
      super(x, y);
    }
    
    @Override
    public float collideWithLeft(Player player, float newY){
      player.superDeath();
      return newY;
    }
  }
}
