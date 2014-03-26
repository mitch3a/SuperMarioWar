package smw.settings;

/**
 * Helper pair for Primitive Types. Used for saving/loading settings
 * 
 * @param <T>
 */
public class KeyDefaultPair<T> {

  T defaultValue;
  String key;
  
  public KeyDefaultPair(String key, T defaultValue){
    this.key = key;
    this.defaultValue = defaultValue;
  }
}
