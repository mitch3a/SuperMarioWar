package smw.settings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

import lombok.Getter;
import lombok.Setter;

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
  static final KeyDefaultPair<Boolean> FULLSCREEN = new KeyDefaultPair<Boolean>("Fullscreen", false);
  static final KeyDefaultPair<Boolean> STRETCHMODE = new KeyDefaultPair<Boolean>("StretchMode", false);
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
  
  @Getter @Setter GamePlaySettings gamePlay;
  @Getter @Setter TeamSettings team;
  @Getter @Setter ItemSelectionSettings itemSelection;
  @Getter @Setter ItemSettings item;
  @Getter @Setter WeaponsAndProjectilesSettings weaponsAndProjectiles;
  @Getter @Setter WeaponUseLimitsSettings weaponUseLimits;
  
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
      
      fullscreen  = prop.getBoolean(FULLSCREEN);
      stretchMode = prop.getBoolean(STRETCHMODE);
      
      //TODO volumeMaster = prop.getFloat(KEY_VOLUME_MASTER, DEFAULT_VOLUME_MASTER);
      //TODO volumeBGM    = prop.getFloat(KEY_VOLUME_BGM, DEFAULT_VOLUME_BGM);
      //TODO volumeSFX    = prop.getFloat(KEY_VOLUME_SFX, DEFAULT_VOLUME_SFX);

      gamePlay = new GamePlaySettings(prop);
      team = new TeamSettings(prop);
      gamePlay = new GamePlaySettings(prop);
      team = new TeamSettings(prop);
      itemSelection = new ItemSelectionSettings(prop);
      item = new ItemSettings(prop);
      weaponsAndProjectiles = new WeaponsAndProjectilesSettings(prop);
      weaponUseLimits = new WeaponUseLimitsSettings(prop);
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public synchronized void saveSettings() {
    //Want to store these in order
    Properties prop = new Properties(){
      @Override
      public synchronized Enumeration<Object> keys() {
          return Collections.enumeration(new TreeSet<Object>(super.keySet()));
      }
    };
  
    OutputStream output = null;

    try {
      File f = new File(CONFIG_FILE);
      
      if(!f.exists()){
        f.createNewFile();
      }
      
      output = new FileOutputStream(CONFIG_FILE);

      prop.setProperty(FULLSCREEN.key,  fullscreen  ? TRUE : FALSE);
      prop.setProperty(STRETCHMODE.key, stretchMode ? TRUE : FALSE);

      prop.setProperty(KEY_VOLUME_MASTER,   Float.toString(volumeMaster));
      prop.setProperty(KEY_VOLUME_BGM,      Float.toString(volumeBGM));
      prop.setProperty(KEY_VOLUME_SFX,      Float.toString(volumeSFX));
      
      gamePlay.add(prop);
      team.add(prop);
      itemSelection.add(prop);
      item.add(prop);
      weaponsAndProjectiles.add(prop);
      weaponUseLimits.add(prop);

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
