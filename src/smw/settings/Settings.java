package smw.settings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Provides flags to indicate whether a given function/area of code is being
 * debugged. This includes enabling logging and disabling certain features of
 * the game. There are also some miscellaneous utilities in this class.
 */
public class Settings {  
  private static Settings INSTANCE;

  static final String TRUE = "true";
  static final String FALSE = "false";

  static final String CONFIG_FILE = "config.properties";
  static final String KEY_FULLSCREEN = "Fullscreen";
  static final String KEY_STRETCHMODE = "StretchMode";
  static final String KEY_VOLUME_MASTER = "VolumeMaster";
  static final String KEY_VOLUME_BGM = "VolumeBackgroundMusic";
  static final String KEY_VOLUME_SFX = "VolumeSoundFX";
  
  //static final String KEY_ = "";

  boolean fullscreen;
  boolean stretchMode;
  float volumeMaster;
  float volumeBGM;
  float volumeSFX;
  
  boolean DEFAULT_FULL_SCREEN = false;
  boolean DEFAULT_STRETCH_MODE = false;
  float DEFAULT_VOLUME_MASTER = 0;
  float DEFAULT_VOLUME_BGM = 0;
  float DEFAULT_VOLUME_SFX = 0;
  
  GamePlaySettings gamePlay;
  TeamSettings team;
  
  public synchronized static Settings getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new Settings();
    }

    return INSTANCE;
  }

  private Settings() {
    PropertiesWrapper prop;
    try {
      prop = new PropertiesWrapper();
      
      fullscreen  = prop.getBoolean(KEY_FULLSCREEN, DEFAULT_FULL_SCREEN);
      stretchMode = prop.getBoolean(KEY_STRETCHMODE, DEFAULT_STRETCH_MODE);
      
      volumeMaster = prop.getFloat(KEY_VOLUME_MASTER, DEFAULT_VOLUME_MASTER);
      volumeBGM    = prop.getFloat(KEY_VOLUME_BGM, DEFAULT_VOLUME_BGM);
      volumeSFX    = prop.getFloat(KEY_VOLUME_SFX, DEFAULT_VOLUME_SFX);

      gamePlay = new GamePlaySettings(prop);
      team = new TeamSettings(prop);
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public synchronized void saveSettings() {
    Properties prop = new Properties();
    OutputStream output = null;

    try {
      File f = new File(CONFIG_FILE);
      
      if(!f.exists()){
        f.createNewFile();
      }
      
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
