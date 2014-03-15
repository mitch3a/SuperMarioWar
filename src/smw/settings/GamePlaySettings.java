package smw.settings;

import java.util.Properties;


public class GamePlaySettings implements SubSetting{
  static final String CATEGORY_NAME = "GamePlay";
  
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
  float shieldTime;
  float boundsTime;
  float suicideTime;
  WarpLockStyle warpLockStyle;
  float warpLockTime;
  BotDifficulty botDifficulty;
  PointSpeed pointSpeed;
  
  public GamePlaySettings(PropertiesWrapper prop){
    respawnTime = prop.getFloat(KEY_RESPAWN_TIME, DEFAULT_RESPAWN_TIME);
    shieldStyle = (ShieldStyle) prop.getEnum(ShieldStyle.class, KEY_SHIELD_STYLE, DEFAULT_SHIELD_STYLE);
    shieldTime  = prop.getFloat(KEY_SHIELD_TIME,  DEFAULT_SHIELD_TIME);
    boundsTime  = prop.getFloat(KEY_BOUNDS_TIME,  DEFAULT_BOUNDS_TIME);
    suicideTime = prop.getFloat(KEY_SUICIDE_TIME, DEFAULT_SUICIDE_TIME);
    warpLockStyle = (WarpLockStyle) prop.getEnum(WarpLockStyle.class, KEY_WARP_LOCK_STYLE, DEFAULT_WARP_LOCK_STYLE);
    warpLockTime  = prop.getFloat(KEY_WARP_LOCK_TIME, DEFAULT_WARP_LOCK_TIME); 
    botDifficulty = (BotDifficulty) prop.getEnum(BotDifficulty.class, KEY_BOT_DIFFICULTY, DEFAULT_BOT_DIFFICULTY);
    pointSpeed    = (PointSpeed) prop.getEnum(PointSpeed.class, KEY_POINT_SPEED, DEFAULT_POINT_SPEED);
  }
  
  @Override
  public void add(Properties prop) {
    shieldStyle  = ShieldStyle.hard;
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
