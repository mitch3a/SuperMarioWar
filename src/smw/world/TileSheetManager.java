package smw.world;

import java.util.HashMap;
import java.util.Map;

public class TileSheetManager {
  
  /**
   * Singleton Implementation.... TODO consider making this stuff generic (done in several places)
   */
  static TileSheetManager instance;

  protected TileSheetManager(){
    
  }
  
  public static TileSheetManager getInstance(){
    if(instance == null){
      instance = new TileSheetManager();
    }
    
    return instance;
  }
  
  /*** END SINGLETON IMPLEMENTATION ***/
  
  Map<String, TileSheet> tileSheets = new HashMap<String, TileSheet>();
  
  public TileSheet getTileSheet(String tileSheet){
    if(tileSheet == null || tileSheet == "" || tileSheet.contains("null")){
      return null; //TODO consider keeping a "valid tilesheets" list
    }
    
    if(!tileSheets.containsKey(tileSheet)){
      tileSheets.put(tileSheet, new TileSheet(tileSheet));
    }
    
    return tileSheets.get(tileSheet);
  }
}
