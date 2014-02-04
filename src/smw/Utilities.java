package smw;

public class Utilities {
    
  /** Returns whether the OS is 64 bit.
   * @return true = 64 bit
   */
  public static boolean is64bit() {
    return System.getProperty("sun.arch.data.model").equals("64");
  }
    
  /** Returns whether the OS is Windows.
   * @return true = windows 
   */
  public static boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().contains("win");
  }
}
