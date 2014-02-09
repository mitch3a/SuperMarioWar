package smw.world.Structures;

public class Item {
  TileType type;
  int x;
  int y;
  
  //TODO this should be removed once Items are done
  
  /** Tile types specified by the original map files. */
  public static enum TileType {
    NONSOLID, SOLID, SOLID_ON_TOP, ICE, DEATH, DEATH_ON_TOP, DEATH_ON_BOTTOM, DEATH_ON_LEFT, DEATH_ON_RIGHT,
    ICE_ON_TOP, ICE_DEATH_ON_BOTTOM, ICE_DEATH_ON_LEFT, ICE_DEATH_ON_RIGHT, 
    SUPER_DEATH, SUPER_DEATH_TOP, SUPER_DEATH_BOTTOM, SUPER_DEATH_LEFT, SUPER_DEATH_RIGHT, PLAYER_DEATH, GAP;
    
    public static final int SIZE = 20;
  }
  
  
  public static TileType getType(int type) {
    TileType result = TileType.NONSOLID;
    switch(type) {
      case 0: result = TileType.NONSOLID; break;
      case 1: result = TileType.SOLID; break;
      case 2: result = TileType.SOLID_ON_TOP; break;
      case 3: result = TileType.ICE; break;
      case 4: result = TileType.DEATH; break;
      case 5: result = TileType.DEATH_ON_TOP; break;
      case 6: result = TileType.DEATH_ON_BOTTOM; break;
      case 7: result = TileType.DEATH_ON_LEFT; break;
      case 8: result = TileType.DEATH_ON_RIGHT; break;
      case 9: result = TileType.ICE_ON_TOP; break;
      case 10: result = TileType.ICE_DEATH_ON_BOTTOM; break;
      case 11: result = TileType.ICE_DEATH_ON_LEFT; break;
      case 12: result = TileType.ICE_DEATH_ON_RIGHT; break;
      case 13: result = TileType.SUPER_DEATH; break;
      case 14: result = TileType.SUPER_DEATH_TOP; break;
      case 15: result = TileType.SUPER_DEATH_BOTTOM; break;
      case 16: result = TileType.SUPER_DEATH_LEFT; break;
      case 17: result = TileType.SUPER_DEATH_RIGHT; break;
      case 18: result = TileType.PLAYER_DEATH; break;
      case 19: result = TileType.GAP; break;
    }
    return result;
  }
}
