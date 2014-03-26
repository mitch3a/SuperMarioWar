package smw.settings;

import lombok.Getter;
import lombok.Setter;


public class GamePlaySettings implements SubSetting{
  static final String CATEGORY_NAME = "GamePlay";
    
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
  
  static final KeyDefaultPair<Float> RESPAWN_TIME                = new KeyDefaultPair<Float>            (CATEGORY_NAME + "." + "RespawnTime", 1.0f);
  static final KeyDefaultPairEnum<ShieldStyle> SHIELD_STYLE      = new KeyDefaultPairEnum<ShieldStyle>  (CATEGORY_NAME + "." + "ShieldStyle", ShieldStyle.noShield);
  static final KeyDefaultPair<Float> SHIELD_TIME                 = new KeyDefaultPair<Float>            (CATEGORY_NAME + "." + "ShieldTime", 1.0f);
  static final KeyDefaultPair<Float> BOUNDS_TIME                 = new KeyDefaultPair<Float>            (CATEGORY_NAME + "." + "BoundsTime", 5.0f);
  static final KeyDefaultPair<Float> SUICIDE_TIME                = new KeyDefaultPair<Float>            (CATEGORY_NAME + "." + "SuicideTime", 5.0f);
  static final KeyDefaultPairEnum<WarpLockStyle> WARP_LOCK_STYLE = new KeyDefaultPairEnum<WarpLockStyle>(CATEGORY_NAME + "." + "WarpLockStyle", WarpLockStyle.allWarps);
  static final KeyDefaultPair<Float> WARP_LOCK_TIME              = new KeyDefaultPair<Float>            (CATEGORY_NAME + "." + "WarpLockTime", 3.0f);
  static final KeyDefaultPairEnum<BotDifficulty> BOT_DIFFICULTY  = new KeyDefaultPairEnum<BotDifficulty>(CATEGORY_NAME + "." + "BotDifficulty", BotDifficulty.moderate);
  static final KeyDefaultPairEnum<PointSpeed> POINT_SPEED        = new KeyDefaultPairEnum<PointSpeed>   (CATEGORY_NAME + "." + "PointSpeed", PointSpeed.moderate);
  
  @Getter @Setter float respawnTime;
  @Getter @Setter ShieldStyle shieldStyle;
  @Getter @Setter float shieldTime;
  @Getter @Setter float boundsTime;
  @Getter @Setter float suicideTime;
  @Getter @Setter WarpLockStyle warpLockStyle;
  @Getter @Setter float warpLockTime;
  @Getter @Setter BotDifficulty botDifficulty;
  @Getter @Setter PointSpeed pointSpeed;
  
  public GamePlaySettings(PropertiesWrapper prop){
    respawnTime = prop.getFloat(RESPAWN_TIME);
    shieldStyle = prop.getEnum(SHIELD_STYLE);
    shieldTime  = prop.getFloat(SHIELD_TIME);
    boundsTime  = prop.getFloat(BOUNDS_TIME);
    suicideTime = prop.getFloat(SUICIDE_TIME);
    warpLockStyle = prop.getEnum(WARP_LOCK_STYLE);
    warpLockTime  = prop.getFloat(WARP_LOCK_TIME); 
    botDifficulty = prop.getEnum(BOT_DIFFICULTY);
    pointSpeed    = prop.getEnum(POINT_SPEED);
  }
  
  @Override
  public void add(PropertiesWrapper prop) {
    prop.setProperty(RESPAWN_TIME,    respawnTime);
    prop.setProperty(SHIELD_STYLE,    shieldStyle);
    prop.setProperty(SHIELD_TIME,     shieldTime);
    prop.setProperty(BOUNDS_TIME,     boundsTime);
    prop.setProperty(SUICIDE_TIME,    suicideTime);
    prop.setProperty(WARP_LOCK_STYLE, warpLockStyle);
    prop.setProperty(WARP_LOCK_TIME,  warpLockTime);
    prop.setProperty(BOT_DIFFICULTY,  botDifficulty);
    prop.setProperty(POINT_SPEED,     pointSpeed);
  }

  @Override
  public String getCategoryName() {
    return CATEGORY_NAME;
  }
}
