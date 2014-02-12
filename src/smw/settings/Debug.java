package smw.settings;

import java.io.File;

import smw.world.Structures.WorldBuffer;

/** Provides flags to indicate whether a given function/area of code is being debugged for logging purposes. */
public class Debug {
  public static final boolean LOG_FRAMERATE = false;
  public static final boolean LOG_WORLD_INFO = true;
  public static final boolean LOG_TILE_TYPE_INFO = false;
  public static final boolean MUTE = false;
  public static final boolean MUTE_SFX = false;
  public static final boolean MUTE_MUSIC = false;
  
  /*
   * Prints the map name, version, and tilesets
   */
  public static void printAllMapsAndVersions(){
    File folder = new File(Debug.class.getClassLoader().getResource("map/").getFile());
    File[] listOfFiles = folder.listFiles();
    for(File f : listOfFiles){
      String name = f.getName();
      if(name.endsWith(".map")){
        System.out.println(name + ": " + getMapVersion(name));
      }
    }
  }
  
  /*
   * This is a helper method to print all the map information. It skips
   * through the buffer to the information its looking for. For more
   * information, see the Level class on how a map is actually loaded
   */
  public static String getMapVersion(String name){
    String result = "";
    try {
      WorldBuffer buffer = new WorldBuffer(name);
      int version = buffer.getVersion();
      
      result += "Version: " + version;

      // For now only support latest map files (1.8+)
      if (version >= 1800) {

        for (int i = 0; i < 12; i++) {
          buffer.getInt();
        }
        
        buffer.getInt(); // unused 32 bits after auto filter section
                
        // Load tile set information.
        final int tileSetCount = buffer.getInt();

        result += " with tilesets: ";
        for (int i = 0; i < tileSetCount; i++) {
          buffer.getInt(); //Not used
          buffer.getString();
        }
      }
      buffer.close();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    
    return result;
  }
}
