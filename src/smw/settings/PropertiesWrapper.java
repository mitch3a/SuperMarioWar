package smw.settings;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a helper class to handle all the different property types
 */
public class PropertiesWrapper {
  static final String TRUE = "true";
  static final String FALSE = "false";
  
  static final Logger logger = Logger.getLogger(PropertiesWrapper.class.getName());
  
  Properties prop;
  
  @SuppressWarnings("serial")
  public PropertiesWrapper() {
    FileInputStream input = null;
    
    try{
      prop = new Properties(){
        @Override
        public synchronized Enumeration<Object> keys() {
            return Collections.enumeration(new TreeSet<Object>(super.keySet()));
        }
      };
      
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
      return prop.getProperty(pair.key).equalsIgnoreCase(TRUE);
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
  
  public void setProperty(KeyDefaultPair<Boolean> pair, boolean value){
    prop.setProperty(pair.key, value ? TRUE : FALSE);
  }
  
  public void setProperty(KeyDefaultPair<Float> pair, float value){
    prop.setProperty(pair.key, Float.toString(value));
  }
  
  public void setProperty(KeyDefaultPair<Integer> pair, int value){
    prop.setProperty(pair.key, Integer.toString(value));
  }
  
  public void setProperty(KeyDefaultPair<String> pair, String value){
    prop.setProperty(pair.key, value);
  }
  
  public <T extends Enum<T>> void setProperty(KeyDefaultPairEnum<T> pair, T value){
    prop.setProperty(pair.key, value.toString());
  }

  public void store(OutputStream output) {
    try {
      prop.store(output, null);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Could not save settings file: ", e);
    }
  }  
}