package smw.settings;


public class KeyDefaultPairEnum<T extends Enum<T>> extends KeyDefaultPair<T>{
  Class<T> classType;
  
  /**
   * @param key - The string to store in properties file
   * @param defaultValue
   */
  public KeyDefaultPairEnum(String key, T defaultValue){ 
    super(key, defaultValue);
    
    this.classType = defaultValue.getDeclaringClass();
  }
}