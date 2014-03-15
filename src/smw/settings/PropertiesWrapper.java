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
  
  public boolean getBoolean(String key, boolean defaultValue){
    if(prop != null && prop.containsKey(key)){
      return prop.getProperty(key).equalsIgnoreCase(trueString);
    }
    
    return defaultValue;
  }
  
  public float getFloat(String key, float defaultValue){
    if(prop != null && prop.containsKey(key)){
      try{
        return Float.parseFloat(prop.getProperty(key));
      } catch(Exception e){
        logger.log(Level.WARNING, "Could not parse Float for key " + key, e);
      }
    }
    
    return defaultValue;
  }
  
  public int getInt(String key, int defaultValue){
    if(prop != null && prop.containsKey(key)){
      try{
        return Integer.parseInt(prop.getProperty(key));
      } catch(Exception e){
        logger.log(Level.WARNING, "Could not parse Integer for key " + key, e);
      }
    }
    
    return defaultValue;
  }
  
	public String getString(String key, String defaultValue){
		if(prop != null && prop.containsKey(key)){
		  try{
		    return prop.getProperty(key, defaultValue);
      } catch(Exception e){
        logger.log(Level.WARNING, "Could not parse String for key " + key, e);
      }
		}
		
		return defaultValue;
	}

  public <T extends Enum<T>> T getEnum(Class<T> c, String key, T defaultValue) {
    if(prop != null && prop.containsKey(key)){
      try{
        return Enum.valueOf(c, prop.getProperty(key));
      } catch(Exception e){
        logger.log(Level.WARNING, "Could not parse Enum in class " + c.getName() + " for key " + key, e);
      }
    }
    
    return defaultValue;
  }
}