package smw.settings;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Provides flags to indicate whether a given function/area of code is being
 * debugged. This includes enabling logging and disabling certain features of
 * the game. There are also some miscellaneous utilities in this class.
 */
public class Settings {
  public enum Test{
    AB, CD, EF;
  }
  private static Settings INSTANCE;

  static final String TRUE = "true";
  static final String FALSE = "false";

  static final String CONFIG_FILE = "config.properties";
  static final String KEY_FULLSCREEN = "Fullscreen";
  static final String KEY_STRETCHMODE = "StretchMode";
  static final String KEY_VOLUME_MASTER = "VolumeMaster";
  static final String KEY_VOLUME_BGM = "VolumeBackgroundMusic";
  static final String KEY_VOLUME_SFX = "VolumeSoundFX";
  
  //GameplayOptions
  static final String KEY_RESPAWN_TIME    = "RespawnTime";
  static final String KEY_SHIELD_STYLE    = "ShieldStyle";
  static final String KEY_SHIELD_TIME     = "ShieldTime";
  static final String KEY_BOUNDS_TIME     = "BoundsTime";
  static final String KEY_SUICIDE_TIME    = "SuicideTime";
  static final String KEY_WARP_LOCK_STYLE = "WarpLockStyle";
  static final String KEY_WARP_LOCK_TIME  = "WarpLockTime";
  static final String KEY_BOT_DIFFICULTY  = "BotDifficulty";
  static final String KEY_POINT_SPEED     = "PointSpeed";
  //static final String KEY_ = "";

  boolean fullscreen = false;
  boolean stretchMode = false;
  float volumeMaster = 0;
  float volumeBGM = 0;
  float volumeSFX = 0;
  float respawnTime = 1.0f;
  Test testVar = Test.AB;
  //TODO shieldStyle;
  float shieldTime = 1.0f;
  float boundsTime = 5.0f;
  float suicideTime = 5.0f;
  //TODO warpStyle;
  float warpLockTime = 3.0f;
  //TODO botDifficulty;
  //TODO pointSpeed;
  
  public synchronized static Settings getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new Settings();
    }

    return INSTANCE;
  }

  private Settings() {
    Properties prop = new Properties();
    InputStream input = null;

    try {
      input = new FileInputStream("config.properties");
      prop.load(input);

      fullscreen  = (prop.getProperty(KEY_FULLSCREEN ).equals(TRUE)) ? true : false;
      stretchMode = (prop.getProperty(KEY_STRETCHMODE).equals(TRUE)) ? true : false;
      
      volumeMaster = Float.parseFloat(prop.getProperty(KEY_VOLUME_MASTER));
      volumeBGM    = Float.parseFloat(prop.getProperty(KEY_VOLUME_BGM));
      volumeSFX    = Float.parseFloat(prop.getProperty(KEY_VOLUME_SFX));

      respawnTime  = Float.parseFloat(prop.getProperty(KEY_VOLUME_MASTER));;
      //TODO shieldStyle;
      shieldTime  = Float.parseFloat(prop.getProperty(KEY_VOLUME_MASTER));
      boundsTime  = Float.parseFloat(prop.getProperty(KEY_VOLUME_MASTER));
      suicideTime  = Float.parseFloat(prop.getProperty(KEY_VOLUME_MASTER));
      //TODO warpLockStyle;
      warpLockTime  = Float.parseFloat(prop.getProperty(KEY_VOLUME_MASTER));    
      //TODO botDifficulty;
      //TODO pointSpeed
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
  }

  public synchronized void saveSettings() {
    Properties prop = new Properties();
    OutputStream output = null;

    try {
      output = new FileOutputStream(CONFIG_FILE);

      prop.setProperty(KEY_FULLSCREEN,  fullscreen  ? TRUE : FALSE);
      prop.setProperty(KEY_STRETCHMODE, stretchMode ? TRUE : FALSE);
      prop.setProperty(KEY_VOLUME_MASTER, Float.toString(volumeMaster));
      prop.setProperty(KEY_VOLUME_BGM,    Float.toString(volumeBGM));
      prop.setProperty(KEY_VOLUME_SFX,    Float.toString(volumeSFX));
      prop.setProperty(KEY_RESPAWN_TIME,  Float.toString(respawnTime));
      //TODO shieldStyle
      prop.setProperty(KEY_SHIELD_TIME,   Float.toString(shieldTime));
      prop.setProperty(KEY_BOUNDS_TIME,   Float.toString(boundsTime));
      prop.setProperty(KEY_SUICIDE_TIME,  Float.toString(suicideTime));
      //TODO warpLockStyle
      prop.setProperty(KEY_WARP_LOCK_TIME,  Float.toString(warpLockTime));
      //TODO botDifficulty;
      //TODO pointSpeed
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
  
  public float getRespawnTime(){
    return respawnTime;
  }
  
  public void setRespawnTime(float respawnTime){
    this.respawnTime = respawnTime;
  }

  //TODO shieldStyle;
  
  public void setShieldTime(float shieldTime){
    this.shieldTime = shieldTime;
  }
  
  public float getShieldTime(){
    return respawnTime;
  }
  public void setBoundsTime(float boundsTime){
    this.boundsTime = boundsTime;
  }
  
  public float getBoundsTime(){
    return respawnTime;
  }
  
  public void setSuicideTime(float suicideTime){
    this.suicideTime = suicideTime;
  }
  
  public float getSuicideTime(){
    return suicideTime;
  }

  //TODO warpStyle;
  public void setWarpLockTime(float warpLockTime){
    this.warpLockTime = warpLockTime;
  }
  
  public float getWarpLockTime(){
    return warpLockTime;
  }
  
  //TODO botDifficulty;
  //TODO pointSpeed;
}
