package smw.settings;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PropertiesWrapper {
  public static String trueString  = "true";
  static final Logger logger = Logger.getLogger(PropertiesWrapper.class.getName());
  
  Properties prop;
  
  public PropertiesWrapper() {
    FileInputStream input = null;
    
    try{
      prop = new Properties();
      input = new FileInputStream("config.properties");
      prop.load(input);
    } catch(Exception e){
      logger.warning(e.toString());
    } finally{
      if(input != null){
        try{
          input.close();  
        } catch(Exception e){
          logger.log(Level.WARNING, "Could not close input file.", e);
        }
      }
    }
    
    
  }
  
  public boolean getBoolean(KeyDefaultPair<Boolean> pair){
    if(prop != null && prop.containsKey(pair.key)){
      return prop.getProperty(pair.key).equalsIgnoreCase(trueString);
    }
    
    return pair.defaultValue;
  }
  
  public float getFloat(KeyDefaultPair<Float> pair){
    if(prop != null && prop.containsKey(pair.key)){
      try{
        return Float.parseFloat(prop.getProperty(pair.key));
      } catch(Exception e){
        logger.log(Level.WARNING, "Could not parse Float for key " + pair.key, e);
      }
    }
    
    return pair.defaultValue;
  }
  
  public int getInt(KeyDefaultPair<Integer> pair){
    if(prop != null && prop.containsKey(pair.key)){
      try{
        return Integer.parseInt(prop.getProperty(pair.key));
      } catch(Exception e){
        logger.log(Level.WARNING, "Could not parse Integer for key " + pair.key, e);
      }
    }
    
    return pair.defaultValue;
  }
  
  public String getString(KeyDefaultPair<String> pair){
    if(prop != null && prop.containsKey(pair.key)){
      try{
        return prop.getProperty(pair.key);
      } catch(Exception e){
        logger.log(Level.WARNING, "Could not parse String for key " + pair.key, e);
      }
    }
    
    return pair.defaultValue;
  }
  

  public <T extends Enum<T>> T getEnum(KeyDefaultPairEnum<T> pair){
    if(prop != null && prop.containsKey(pair.key)){
      try{
        return Enum.valueOf(pair.classType, prop.getProperty(pair.key));
      } catch(Exception e){
        logger.log(Level.WARNING, "Could not parse Enum in class " + pair.getClass().toString() + " for key " + pair.key, e);
      }
    }
    
    return pair.defaultValue;
  }
}