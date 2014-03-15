package smw.settings;

import java.util.Properties;

import smw.settings.GamePlaySettings.BotDifficulty;
import smw.settings.GamePlaySettings.PointSpeed;
import smw.settings.GamePlaySettings.ShieldStyle;
import smw.settings.GamePlaySettings.WarpLockStyle;

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
  
  float DEFAULT_RESPAWN_TIME = 1.0f;
  UseSettingsFrom DEFAULT_SHIELD_STYLE = UseSettingsFrom.mapOnly;
  ItemSet DEFAULT_ITEM_SET = ItemSet.customSet1;
  float DEFAULT_GREEN_MUSHROOM = 10;
  float DEFAULT_RED_MUSHROOM = 4;
  float DEFAULT_BLUE_MUSHROOM = 2;
  float DEFAULT_YELLOW_MUSHROOM = 1;
  float DEFAULT_QUESTION_MUSHROOM = 5;
  float DEFAULT_FLOWER = 10;
  float DEFAULT_HAMMER = 4;
  float DEFAULT_BOOMERANG = 6;
  float DEFAULT_DEATH_MUSHROOM = 5;
  float DEFAULT_STAR = 8;
  float DEFAULT_BOMB = 4;
  float DEFAULT_TANOOKI = 6;
  float DEFAULT_TIMER = 4;
  float DEFAULT_BULLET_BILL = 2;
  float DEFAULT_FIREBALL = 4;
  float DEFAULT_POW_BLOCK = 2;

  float greenMushroom;
  float redMushroom;
  float blueMushroom;
  float yellowMushroom;
  float questionMushroom;
  float flower;
  float hammer;
  float boomerang;
  float deathMushroom;
  float star;
  float bomb;
  float tanooki;
  float timer;
  float bulletBill;
  float fireball;
  float powBlock;
  
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
