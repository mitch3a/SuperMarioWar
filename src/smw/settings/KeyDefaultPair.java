package smw.settings;

public class KeyDefaultPair<T> {

  T defaultValue;
  String key;
  
  public KeyDefaultPair(String key, T defaultValue){
    this.key = key;
    this.defaultValue = defaultValue;
  }
}
