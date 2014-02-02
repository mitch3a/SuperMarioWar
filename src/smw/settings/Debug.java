package smw.settings;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import smw.world.Structures.TileSetTranslation;

/** Provides flags to indicate whether a given function/area of code is being debugged for logging purposes. */
public class Debug {
  public static final boolean LOG_FRAMERATE = false;
  public static final boolean LOG_WORLD_INFO = false;
  public static final boolean LOG_TILE_TYPE_INFO = false;
  
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
      int version = 0;
      RandomAccessFile f = new RandomAccessFile(Debug.class.getClassLoader().getResource("map/" + name).getPath().replaceAll("%20", " "), "r");
      FileChannel fc = f.getChannel();
      MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
      buffer.order(ByteOrder.LITTLE_ENDIAN); // Java defaults to BIG_ENDIAN, but MAP files were probably made on a x86 PC.
      buffer.load();
      
      // Read the map version.
      for (int i = 0; i < 4; i++) {
        version = 10*version + buffer.getInt();
      }
      
      result += "Version: " + version;

      // For now only support latest map files (1.8+)
      if (version >= 1800) {

        for (int i = 0; i < 12; i++) {
          buffer.getInt();
        }
        
        buffer.getInt(); // unused 32 bits after auto filter section
                
        // Load tile set information.
        final int tileSetCount = buffer.getInt();
        TileSetTranslation[] translation = new TileSetTranslation[tileSetCount];
        
        result += " with tilesets: ";
        for (int i = 0; i < tileSetCount; i++) {
          translation[i] = new TileSetTranslation();
          buffer.getInt(); //Not used

          
          final int ID_LENGTH = buffer.getInt();
          for (int j = 0; j < ID_LENGTH; j++) {
            translation[i].name += (char)(buffer.get());
          }
          
          result += translation[i].name + ", ";
        }
      }
      buffer.clear();
      fc.close();
      f.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    
    return result;
  }
}
