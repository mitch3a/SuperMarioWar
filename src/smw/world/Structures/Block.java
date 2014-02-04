package smw.world.Structures;

public class Block {
  public Block(int type, boolean hidden) {
    this.type = type;
    this.hidden = hidden;
  }
  public int type;
  public int[] settings = new int[26]; // TODO - I think this is mapped to NUM_POWERUPS
  public boolean hidden;
}
