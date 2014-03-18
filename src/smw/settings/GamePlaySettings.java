package smw.settings;

import java.util.Properties;

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
    shieldStyle = (ShieldStyle) prop.getEnum(SHIELD_STYLE);
    shieldTime  = prop.getFloat(SHIELD_TIME);
    boundsTime  = prop.getFloat(BOUNDS_TIME);
    suicideTime = prop.getFloat(SUICIDE_TIME);
    warpLockStyle = (WarpLockStyle) prop.getEnum(WARP_LOCK_STYLE);
    warpLockTime  = prop.getFloat(WARP_LOCK_TIME); 
    botDifficulty = (BotDifficulty) prop.getEnum(BOT_DIFFICULTY);
    pointSpeed    = (PointSpeed) prop.getEnum(POINT_SPEED);
  }
  
  @Override
  public void add(Properties prop) {
    prop.setProperty(RESPAWN_TIME.key,    Float.toString(respawnTime));
    prop.setProperty(SHIELD_STYLE.key,    shieldStyle.toString());
    prop.setProperty(SHIELD_TIME.key,     Float.toString(shieldTime));
    prop.setProperty(BOUNDS_TIME.key,     Float.toString(boundsTime));
    prop.setProperty(SUICIDE_TIME.key,    Float.toString(suicideTime));
    prop.setProperty(WARP_LOCK_STYLE.key, warpLockStyle.toString());
    prop.setProperty(WARP_LOCK_TIME.key,  Float.toString(warpLockTime));
    prop.setProperty(BOT_DIFFICULTY.key,  botDifficulty.toString());
    prop.setProperty(POINT_SPEED.key,     pointSpeed.toString());
  }

  @Override
  public String getCategoryName() {
    return CATEGORY_NAME;
  }
}
