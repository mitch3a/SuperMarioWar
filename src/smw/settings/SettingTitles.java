package smw.settings;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * This class is for loading/returning the string to display based on the given key
 */
public class SettingTitles {
  private static final String ENGLISH_TITLES = "Settings.english";

  private static final Logger logger = Logger.getLogger(Settings.class.getName());

  public enum Language{
    english;
  }

  private final Properties prop = new Properties();
  
  public SettingTitles(Language language){
    loadFile(getLanguageFile(language));
  }
  
  private String getLanguageFile(Language language){
    switch(language){
      default: 
    }
    
    return ENGLISH_TITLES;
  }
  
  private void loadFile(String settingsFileName){
    try(FileInputStream input = new FileInputStream(settingsFileName)){
      prop.load(input);
    } catch(Exception e){
      logger.warning(e.toString());
    } 
  }
  
  public <T> String getTitle(KeyDefaultPair<T> pair){
    return prop.getProperty(pair.key, "NULL");
  }
  
  public String getTitle(SubSetting settingsClass){
    return prop.getProperty(settingsClass.getCategoryName(), "NULL");
  }
}
