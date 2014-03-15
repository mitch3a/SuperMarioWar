package smw.settings;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class PropertiesWrapper {
  public static String trueString  = "true";
  static final Logger logger = Logger.getLogger(PropertiesWrapper.class.getName());
  
  Properties prop;
  
  public PropertiesWrapper() throws Exception{
    prop = new Properties();
    FileInputStream input = new FileInputStream("config.properties");
    prop.load(input);
    
    input.close();  
  }
  
  public boolean getBoolean(String key, boolean defaultValue){
    if(prop != null && prop.contains(key)){
      return prop.getProperty(key).equalsIgnoreCase(trueString);
    }
    
    return defaultValue;
  }
  
  public float getFloat(String key, float defaultValue){
    if(prop != null && prop.contains(key)){
      try{
        return Float.parseFloat(prop.getProperty(key));
      } catch(Exception e){
        logger.warning(e.toString());
      }
    }
    
    return defaultValue;
  }
  
  public int getInt(String key, int defaultValue){
    if(prop != null && prop.contains(key)){
      try{
        return Integer.parseInt(prop.getProperty(key));
      } catch(Exception e){
        logger.warning(e.toString());
      }
    }
    
    return defaultValue;
  }
  
	public String getString(String key, String defaultValue){
		if(prop != null && prop.contains(key)){
		  try{
		    return prop.getProperty(key, defaultValue);
      } catch(Exception e){
        logger.warning(e.toString());
      }
		}
		
		return defaultValue;
	}
}