package smw.settings;

import java.util.Properties;

import lombok.Getter;
import lombok.Setter;

import smw.settings.GamePlaySettings.BotDifficulty;
import smw.settings.GamePlaySettings.PointSpeed;
import smw.settings.GamePlaySettings.ShieldStyle;
import smw.settings.GamePlaySettings.WarpLockStyle;
import smw.settings.TeamSettings.PlayerCollision;

public class ItemSelectionSettings implements SubSetting{
  static final String CATEGORY_NAME = "ItemSelection";
  
  static final String KEY_USE_SETTINGS_FROM = "UseSettingsFrom";
  static final String KEY_ITEM_SET = "UseSettingsFrom";
  
  public enum UseSettingsFrom{
    mapOnly, gameOnly, basicAverage, weightedAverage;
  }

  public enum ItemSet{
    customSet1, customSet2, customSet3, customSet4, customSet5,
    balancedSet, weaponsOnly, koopaBrosWeapons, supportItems,
    boomsAndShakes, flyAndGlide, shellsOnly, mushroomsOnly, 
    superMarioBros1, superMarioBros2, superMarioBros3, 
    superMarioWorld;
  }
  
  static final UseSettingsFrom DEFAULT_SHIELD_STYLE = UseSettingsFrom.mapOnly;
  static final ItemSet DEFAULT_ITEM_SET = ItemSet.customSet1;
  static final float DEFAULT_GREEN_MUSHROOM = 10;
  static final float DEFAULT_RED_MUSHROOM = 4;
  static final float DEFAULT_BLUE_MUSHROOM = 2;
  static final float DEFAULT_YELLOW_MUSHROOM = 1;
  static final float DEFAULT_QUESTION_MUSHROOM = 5;
  static final float DEFAULT_FLOWER = 10;
  static final float DEFAULT_HAMMER = 4;
  static final float DEFAULT_BOOMERANG = 6;
  static final float DEFAULT_DEATH_MUSHROOM = 5;
  static final float DEFAULT_STAR = 8;
  static final float DEFAULT_BOMB = 4;
  static final float DEFAULT_TANOOKI = 6;
  static final float DEFAULT_TIMER = 4;
  static final float DEFAULT_BULLET_BILL = 2;
  static final float DEFAULT_FIREBALL = 4;
  static final float DEFAULT_POW_BLOCK = 2;

  
  @Getter @Setter float greenMushroom;
  @Getter @Setter float redMushroom;
  @Getter @Setter float blueMushroom;
  @Getter @Setter float yellowMushroom;
  @Getter @Setter float questionMushroom;
  @Getter @Setter float flower;
  @Getter @Setter float hammer;
  @Getter @Setter float boomerang;
  @Getter @Setter float deathMushroom;
  @Getter @Setter float star;
  @Getter @Setter float bomb;
  @Getter @Setter float tanooki;
  @Getter @Setter float timer;
  @Getter @Setter float bulletBill;
  @Getter @Setter float fireball;
  @Getter @Setter float powBlock;
  
  public ItemSelectionSettings(PropertiesWrapper prop){
    
    
    /*
    respawnTime = prop.getFloat(KEY_RESPAWN_TIME, DEFAULT_RESPAWN_TIME);
    shieldStyle = (ShieldStyle) prop.getEnum(ShieldStyle.class, KEY_SHIELD_STYLE, DEFAULT_SHIELD_STYLE);
    shieldTime  = prop.getFloat(KEY_SHIELD_TIME,  DEFAULT_SHIELD_TIME);
    boundsTime  = prop.getFloat(KEY_BOUNDS_TIME,  DEFAULT_BOUNDS_TIME);
    suicideTime = prop.getFloat(KEY_SUICIDE_TIME, DEFAULT_SUICIDE_TIME);
    warpLockStyle = (WarpLockStyle) prop.getEnum(WarpLockStyle.class, KEY_WARP_LOCK_STYLE, DEFAULT_WARP_LOCK_STYLE);
    warpLockTime  = prop.getFloat(KEY_WARP_LOCK_TIME, DEFAULT_WARP_LOCK_TIME); 
    botDifficulty = (BotDifficulty) prop.getEnum(BotDifficulty.class, KEY_BOT_DIFFICULTY, DEFAULT_BOT_DIFFICULTY);
    pointSpeed    = (PointSpeed) prop.getEnum(PointSpeed.class, KEY_POINT_SPEED, DEFAULT_POINT_SPEED);
    */
  }
  
  @Override
  public void add(Properties prop) {
    /*
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
    */
  }

  @Override
  public String getCategoryName() {
    return CATEGORY_NAME;
  }
}
