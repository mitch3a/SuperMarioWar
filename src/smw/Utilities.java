package smw;

import java.io.OutputStream;
import java.io.PrintStream;

public class Utilities {
  
  /** Instance of the original output stream. */
  private static PrintStream systemOutStream = System.out;
  /** Instance of the original error stream. */
  private static PrintStream systemErrStream = System.err;
    
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
  
  /** Disables system output. */
  public static void disableOutput() {
    PrintStream noOpStream = new PrintStream(new OutputStream() {
      public void write(int b) { }
    });
    System.setOut(noOpStream);
  }
  
  /** Enables system output. */
  public static void enableOutput() {
    System.setOut(systemOutStream);
  }
  
  /** Disables output to the error stream. */
  public static void disableErrOut() {
    PrintStream noOpStream = new PrintStream(new OutputStream() {
      public void write(int b) { }
    });
    System.setErr(noOpStream);
  }
  
  /** Enables output to the error stream. */
  public static void enableErrOut() {
    System.setErr(systemErrStream);
  }
}
