package smw.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import com.sun.istack.internal.logging.Logger;

/**
 * Provides flags to indicate whether a given function/area of code is being
 * debugged. This includes enabling logging and disabling certain features of
 * the game. There are also some miscellaneous utilities in this class.
 */
public class Settings {  
  private static Settings INSTANCE;

  static final Logger logger = Logger.getLogger(Settings.class);
  
  static final String TRUE = "true";
  static final String FALSE = "false";

  static final String CONFIG_FILE = "config.properties";
  static final String KEY_FULLSCREEN = "Fullscreen";
  static final String KEY_STRETCHMODE = "StretchMode";
  static final String KEY_VOLUME_MASTER = "VolumeMaster";
  static final String KEY_VOLUME_BGM = "VolumeBackgroundMusic";
  static final String KEY_VOLUME_SFX = "VolumeSoundFX";
  
  //static final String KEY_ = "";

  boolean fullscreen = false;
  boolean stretchMode = false;
  float volumeMaster = 0;
  float volumeBGM = 0;
  float volumeSFX = 0;
  
  GamePlaySettings gamePlay;
  TeamSettings team;
  
  public synchronized static Settings getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new Settings();
    }

    return INSTANCE;
  }

  private Settings() {
    Properties prop = new Properties();
    FileInputStream input = null;
    File file = new File("config.properties");
    if (file.isFile() && file.canRead()) {
      try {
        input = new FileInputStream(file);
        prop.load(input);
        if (!prop.isEmpty()) {
          
          // TODO - should be checking if the property exists before calling "equals"
          // if it doesn't exist this will return null
          // Also if the settings file is empty or doesn't exist, should probably just create a default one!
          fullscreen  = (prop.getProperty(KEY_FULLSCREEN ).equals(TRUE)) ? true : false;
          stretchMode = (prop.getProperty(KEY_STRETCHMODE).equals(TRUE)) ? true : false;
          
          volumeMaster = Float.parseFloat(prop.getProperty(KEY_VOLUME_MASTER));
          volumeBGM    = Float.parseFloat(prop.getProperty(KEY_VOLUME_BGM));
          volumeSFX    = Float.parseFloat(prop.getProperty(KEY_VOLUME_SFX));
    
          gamePlay = new GamePlaySettings(prop);
          team = new TeamSettings(prop);
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      } finally {
        if (input != null) {
          try {
            input.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    } else {
      System.err.println("Config file not found!");
    }
  }

  public synchronized void saveSettings() {
    Properties prop = new Properties();
    OutputStream output = null;

    try {
      output = new FileOutputStream(CONFIG_FILE);

      prop.setProperty(KEY_FULLSCREEN,  fullscreen  ? TRUE : FALSE);
      prop.setProperty(KEY_STRETCHMODE, stretchMode ? TRUE : FALSE);

      prop.setProperty(KEY_VOLUME_MASTER,   Float.toString(volumeMaster));
      prop.setProperty(KEY_VOLUME_BGM,      Float.toString(volumeBGM));
      prop.setProperty(KEY_VOLUME_SFX,      Float.toString(volumeSFX));
      
      gamePlay.add(prop);
      team.add(prop);

      prop.store(output, null);
      
    } catch (IOException io) {
      io.printStackTrace();
    } finally {
      if (output != null) {
        try {
          output.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

    }
  }
  
  public synchronized final GamePlaySettings getGamePlaySettings(){
    return gamePlay;
  }

  public synchronized boolean isFullscreen() {
    return fullscreen;
  }
  
  public synchronized void setFullscreen(boolean fullscreen) {
    this.fullscreen = fullscreen;
  }
  
  public synchronized boolean isStretchMode(){
    return stretchMode;
  }
  
  public synchronized void setStretchMode(boolean stretchMode){
    this.stretchMode = stretchMode;
  }

  public synchronized float getVolumeMaster(){
    return volumeMaster;
  }
  
  public synchronized void setVolumeMaster(float volumeMaster){
    this.volumeMaster = volumeMaster;
  }
  
  public synchronized float getVolumeBGM(){
    return volumeBGM;
  }
  
  public synchronized void setVolumeBGM(float volumeBGM){
    this.volumeBGM = volumeBGM;
  }
  
  public synchronized float getVolumeSFX(){
    return volumeSFX;
  }
  
  public synchronized void setVolumeSFX(float volumeSFX){
    this.volumeSFX = volumeSFX;
  }
}
