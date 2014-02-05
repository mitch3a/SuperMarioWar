package smw.world.Structures;

public class Block {
  public int type;
  public int[] settings = new int[26]; // TODO - I think this is mapped to NUM_POWERUPS
  public boolean hidden;
  public boolean switchOn; // TODO - if this block is a switch, it will be switched on... I guess
  
  public Block(int type, boolean hidden) {
    this.type = type;
    this.hidden = hidden;
    this.switchOn = true;
  }
  
}
