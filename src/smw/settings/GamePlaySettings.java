package smw.settings;

import java.util.Properties;
import java.util.logging.Logger;


public class GamePlaySettings implements SubSetting{
  static final String CATEGORY_NAME = "GamePlay";
  static final Logger logger = Logger.getLogger(GamePlaySettings.class.getName());
  
  static final String KEY_RESPAWN_TIME    = "RespawnTime";
  static final String KEY_SHIELD_STYLE    = "ShieldStyle";
  static final String KEY_SHIELD_TIME     = "ShieldTime";
  static final String KEY_BOUNDS_TIME     = "BoundsTime";
  static final String KEY_SUICIDE_TIME    = "SuicideTime";
  static final String KEY_WARP_LOCK_STYLE = "WarpLockStyle";
  static final String KEY_WARP_LOCK_TIME  = "WarpLockTime";
  static final String KEY_BOT_DIFFICULTY  = "BotDifficulty";
  static final String KEY_POINT_SPEED     = "PointSpeed";
  
  public enum ShieldStyle{
    noShield, shield, soft, softWithStomp, hard;
  }
  
  public enum WarpLockStyle{
    exitOnly, entranceAndExit, entireConnection, allWarps;
  }
  
  public enum BotDifficulty{
    veryEasy, easy, moderate, hard, veryHard;
  }
  
  public enum PointSpeed{
    verySlow, slow, moderate, fast, veryFast;
  }
  
  float DEFAULT_RESPAWN_TIME = 1.0f;
  ShieldStyle DEFAULT_SHIELD_STYLE = ShieldStyle.noShield;
  float DEFAULT_SHIELD_TIME = 1.0f;
  float DEFAULT_BOUNDS_TIME = 5.0f;
  float DEFAULT_SUICIDE_TIME = 5.0f;
  WarpLockStyle DEFAULT_WARP_LOCK_STYLE = WarpLockStyle.allWarps;
  float DEFAULT_WARP_LOCK_TIME = 3.0f;
  BotDifficulty DEFAULT_BOT_DIFFICULTY = BotDifficulty.moderate;
  PointSpeed DEFAULT_POINT_SPEED = PointSpeed.moderate;
  
  float respawnTime;
  ShieldStyle shieldStyle;
  float shieldTime = 1.0f;
  float boundsTime = 5.0f;
  float suicideTime = 5.0f;
  WarpLockStyle warpLockStyle;
  float warpLockTime = 3.0f;
  BotDifficulty botDifficulty;
  PointSpeed pointSpeed;
  
  public GamePlaySettings(PropertiesWrapper prop){
    respawnTime = prop.getFloat(KEY_RESPAWN_TIME, DEFAULT_RESPAWN_TIME);
    shieldStyle = ShieldStyle.valueOf(prop.getString(KEY_SHIELD_STYLE, DEFAULT_SHIELD_STYLE));
    
    shieldTime  = Float.parseFloat(prop.getProperty(KEY_SHIELD_TIME));
    boundsTime  = Float.parseFloat(prop.getProperty(KEY_BOUNDS_TIME));
    suicideTime  = Float.parseFloat(prop.getProperty(KEY_SUICIDE_TIME));

    try{
      warpLockStyle = WarpLockStyle.valueOf(prop.getProperty(KEY_WARP_LOCK_STYLE));
    } catch(Exception e){
      logger.warning("Bad value for " + KEY_WARP_LOCK_STYLE + " " + e.toString());
      warpLockStyle = WarpLockStyle.allWarps;
    }
    
    warpLockTime  = Float.parseFloat(prop.getProperty(KEY_WARP_LOCK_TIME)); 
    
    try{
      botDifficulty = BotDifficulty.valueOf(prop.getProperty(KEY_BOT_DIFFICULTY));
    } catch(Exception e){
      logger.warning("Bad value for " + KEY_BOT_DIFFICULTY + " " + e.toString());
      botDifficulty = BotDifficulty.easy;
    }
    
    try{
      pointSpeed = PointSpeed.valueOf(prop.getProperty(KEY_POINT_SPEED));
    } catch(Exception e){
      logger.warning("Bad value for " + KEY_POINT_SPEED + " " + e.toString());
      pointSpeed = PointSpeed.verySlow;
    }
  }
  
  @Override
  public void add(Properties prop) {
    prop.setProperty(KEY_RESPAWN_TIME,    Float.toString(respawnTime));
    prop.setProperty(KEY_SHIELD_STYLE,    shieldStyle.toString());
    prop.setProperty(KEY_SHIELD_TIME,     Float.toString(shieldTime));
    prop.setProperty(KEY_BOUNDS_TIME,     Float.toString(boundsTime));
    prop.setProperty(KEY_SUICIDE_TIME,    Float.toString(suicideTime));
    prop.setProperty(KEY_WARP_LOCK_STYLE, warpLockStyle.toString());
    prop.setProperty(KEY_WARP_LOCK_TIME,  Float.toString(warpLockTime));
    prop.setProperty(KEY_BOT_DIFFICULTY,  botDifficulty.toString());
    prop.setProperty(KEY_POINT_SPEED,     pointSpeed.toString());
  }

  @Override
  public String getCategoryName() {
    return CATEGORY_NAME;
  }
  
  public synchronized float getRespawnTime(){
    return respawnTime;
  }
  
  public synchronized void setRespawnTime(float respawnTime){
    this.respawnTime = respawnTime;
  }

  public synchronized ShieldStyle getShieldStyle(){
    return shieldStyle;
  }
  
  public synchronized void setRespawnTime(ShieldStyle shieldStyle){
    this.shieldStyle = shieldStyle;
  }
  
  public synchronized void setShieldTime(float shieldTime){
    this.shieldTime = shieldTime;
  }
  
  public synchronized float getShieldTime(){
    return respawnTime;
  }
  public synchronized void setBoundsTime(float boundsTime){
    this.boundsTime = boundsTime;
  }
  
  public synchronized float getBoundsTime(){
    return respawnTime;
  }
  
  public synchronized void setSuicideTime(float suicideTime){
    this.suicideTime = suicideTime;
  }
  
  public synchronized float getSuicideTime(){
    return suicideTime;
  }

  public synchronized WarpLockStyle getWarpLockStyle(){
    return warpLockStyle;
  }
  
  public synchronized void setRespawnTime(WarpLockStyle warpLockStyle){
    this.warpLockStyle = warpLockStyle;
  }
  
  public synchronized void setWarpLockTime(float warpLockTime){
    this.warpLockTime = warpLockTime;
  }
  
  public synchronized float getWarpLockTime(){
    return warpLockTime;
  }
  
  public synchronized BotDifficulty getBotDifficulty(){
    return botDifficulty;
  }
  
  public synchronized void setBotDifficulty(BotDifficulty botDifficulty){
    this.botDifficulty = botDifficulty;
  }
  
  public synchronized PointSpeed getPointSpeed(){
    return pointSpeed;
  }
  
  public synchronized void setPointSpeed(PointSpeed pointSpeed){
    this.pointSpeed = pointSpeed;
  }
}
