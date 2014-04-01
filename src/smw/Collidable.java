package smw;

import java.awt.geom.Rectangle2D;

import smw.entity.Player;
import smw.gfx.Sprite;
import smw.ui.screen.GameFrame;
import smw.world.Tile;
import smw.world.warps.WarpBase.Direction;

//TODO mk as other objects are implemented, might want to replace Player with something generic
//     ALSO this should probably be an abstract type
public abstract class Collidable extends Rectangle2D.Float{
  /**
   * 
   */
  private static final long serialVersionUID = -8638553429127344457L;

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
  
  /**
   * This method is used to determine if touching will kill a player
   * @param d the direction the player is moving
   * @return
   */
  public boolean isDeath(Direction d) {
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
    /**
     * 
     */
    private static final long serialVersionUID = -7988350142248714466L;

    public NonSolid(int x, int y) {
      super(x, y);
    }
  }
  
  /**
   * Solid
   */
  public static class Solid extends Collidable{
    
    /**
     * 
     */
    private static final long serialVersionUID = -4329454001787262104L;

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
    /**
     * 
     */
    private static final long serialVersionUID = 6655601256705049256L;

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
    /**
     * 
     */
    private static final long serialVersionUID = -5363736811771340532L;

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
    
    /**
     * 
     */
    private static final long serialVersionUID = -440585211719668819L;

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
    
    @Override
    public boolean isDeath(Direction d) {
      return true;
    }
  }
  
  /**
   * Death On Top
   */
  public static class DeathOnTop extends Solid{
    /**
     * 
     */
    private static final long serialVersionUID = -3843284549022365640L;

    public DeathOnTop(int x, int y) {
      super(x, y);
    }
    
    @Override
    public float collideWithTop(Player player, float newY){
      player.death();
      return newY;
    }
    
    @Override
    public boolean isDeath(Direction d) {
      return (d == Direction.DOWN);
    }
  }
  
  /**
   * Death On Bottom
   */
  public static class DeathOnBottom extends Solid{
    /**
     * 
     */
    private static final long serialVersionUID = -8648720442591129359L;

    public DeathOnBottom(int x, int y) {
      super(x, y);
    }
    
    @Override
    public float collideWithBottom(Player player, float newY){
      player.death();
      return newY;
    }
    
    @Override
    public boolean isDeath(Direction d) {
      return (d == Direction.UP);
    }
  }
  
  /**
   * Death On Right
   */
  public static class DeathOnRight extends Solid{
    /**
     * 
     */
    private static final long serialVersionUID = 2803188035451549603L;

    public DeathOnRight(int x, int y) {
      super(x, y);
    }
    
    @Override
    public float collideWithRight(Player player, float newX){
      player.death();
      return newX;
    }
    
    @Override
    public boolean isDeath(Direction d) {
      return (d == Direction.LEFT);
    }
  }
  
  /**
   * Death On Left
   */
  public static class DeathOnLeft extends Solid{
    /**
     * 
     */
    private static final long serialVersionUID = -1788100670495257908L;

    public DeathOnLeft(int x, int y) {
      super(x, y);
    }
    
    @Override
    public float collideWithLeft(Player player, float newY){
      player.death();
      return newY;
    }
    
    @Override
    public boolean isDeath(Direction d) {
      return (d == Direction.RIGHT);
    }
  }
  
  /**
   * Super Death
   */
  public static class SuperDeath extends Collidable{
    /**
     * 
     */
    private static final long serialVersionUID = 407979696693260664L;

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
    
    @Override
    public boolean isDeath(Direction d) {
      return true;
    }
  }
  
  /**
   * Super Death On Top
   */
  public static class SuperDeathOnTop extends Solid{
    /**
     * 
     */
    private static final long serialVersionUID = -5827141992439694842L;

    public SuperDeathOnTop(int x, int y) {
      super(x, y);
    }
    
    @Override
    public float collideWithTop(Player player, float newY){
      player.superDeath();
      return newY;
    }
    
    @Override
    public boolean isDeath(Direction d) {
      return (d == Direction.DOWN);
    }
  }
  
  /**
   * Super Death On Bottom
   */
  public static class SuperDeathOnBottom extends Solid{
    /**
     * 
     */
    private static final long serialVersionUID = -6128056906664469419L;

    public SuperDeathOnBottom(int x, int y) {
      super(x, y);
    }
    
    @Override
    public float collideWithBottom(Player player, float newY){
      player.superDeath();
      return newY;
    }
    
    @Override
    public boolean isDeath(Direction d) {
      return (d == Direction.UP);
    }
  }
  
  /**
   * Super Death On Right
   */
  public static class SuperDeathOnRight extends Solid{
    /**
     * 
     */
    private static final long serialVersionUID = -8454023115425324440L;

    public SuperDeathOnRight(int x, int y) {
      super(x, y);
    }
    
    @Override
    public float collideWithRight(Player player, float newX){
      player.superDeath();
      return newX;
    }
    
    @Override
    public boolean isDeath(Direction d) {
      return (d == Direction.LEFT);
    }
  }
  
  /**
   * Super Death On Left
   */
  public static class SuperDeathOnLeft extends Solid{
    /**
     * 
     */
    private static final long serialVersionUID = -1650914518888636252L;

    public SuperDeathOnLeft(int x, int y) {
      super(x, y);
    }
    
    @Override
    public float collideWithLeft(Player player, float newY){
      player.superDeath();
      return newY;
    }
    
    @Override
    public boolean isDeath(Direction d) {
      return (d == Direction.RIGHT);
    }
  }
  
  /**
   * Ice Death On Bottom
   */
  public static class IceDeathOnBottom extends Solid{
    /**
     * 
     */
    private static final long serialVersionUID = -8238464100262577831L;

    public IceDeathOnBottom(int x, int y) {
      super(x, y);
    }
    
    @Override
    public float collideWithBottom(Player player, float newY){
      player.death(); //TODO verify this is correct (not super death or need its own) AND right/left same issue (next 2 classes)
      return newY;
    }
    
    @Override
    public boolean isDeath(Direction d) {
      return (d == Direction.UP);
    }
  }
  
  /**
   * Ice Death On Right
   */
  public static class IceDeathOnRight extends Solid{
    /**
     * 
     */
    private static final long serialVersionUID = -2929794363073454285L;

    public IceDeathOnRight(int x, int y) {
      super(x, y);
    }
    
    @Override
    public float collideWithRight(Player player, float newX){
      player.death();
      return newX;
    }
    
    @Override
    public boolean isDeath(Direction d) {
      return (d == Direction.LEFT);
    }
  }
  
  /**
   * Ice Death On Left
   */
  public static class IceDeathOnLeft extends Solid{
    /**
     * 
     */
    private static final long serialVersionUID = 6376124707297720406L;

    public IceDeathOnLeft(int x, int y) {
      super(x, y);
    }
    
    @Override
    public float collideWithLeft(Player player, float newY){
      player.superDeath();
      return newY;
    }
    
    @Override
    public boolean isDeath(Direction d) {
      return (d == Direction.RIGHT);
    }
  }
}
