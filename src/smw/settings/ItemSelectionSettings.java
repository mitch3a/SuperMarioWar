package smw.settings;

import lombok.Getter;
import lombok.Setter;

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
  
  static final KeyDefaultPair<Boolean> SCREEN_CRUNCH    = new KeyDefaultPair<Boolean>(CATEGORY_NAME + "." + "ScreenCrunch", false);
  
  static final KeyDefaultPairEnum<UseSettingsFrom> USE_SETTINGS_FROM = new KeyDefaultPairEnum<UseSettingsFrom>(CATEGORY_NAME + "." + "UseSettingsFrom", UseSettingsFrom.mapOnly);
  static final KeyDefaultPairEnum<ItemSet> ITEM_SET = new KeyDefaultPairEnum<ItemSet>(CATEGORY_NAME + "." + "ItemSet", ItemSet.customSet1);
  
  static final KeyDefaultPair<Integer> GREEN_MUSHROOM = new KeyDefaultPair<Integer>(CATEGORY_NAME + "." + "GreenMushroom", 10);
  static final KeyDefaultPair<Integer> RED_MUSHROOM = new KeyDefaultPair<Integer>(CATEGORY_NAME + "." + "RedMushroom", 4);
  static final KeyDefaultPair<Integer> BLUE_MUSHROOM = new KeyDefaultPair<Integer>(CATEGORY_NAME + "." + "BlueMushroom", 2);
  static final KeyDefaultPair<Integer> YELLOW_MUSHROOM = new KeyDefaultPair<Integer>(CATEGORY_NAME + "." + "YellowMushroom", 1);
  static final KeyDefaultPair<Integer> QUESTION_MUSHROOM = new KeyDefaultPair<Integer>(CATEGORY_NAME + "." + "QuestionMushroom", 5);
  static final KeyDefaultPair<Integer> FLOWER = new KeyDefaultPair<Integer>(CATEGORY_NAME + "." + "Flower", 10);
  static final KeyDefaultPair<Integer> HAMMER = new KeyDefaultPair<Integer>(CATEGORY_NAME + "." + "Hammer", 4);
  static final KeyDefaultPair<Integer> BOOMERANG = new KeyDefaultPair<Integer>(CATEGORY_NAME + "." + "Boomerang", 6);
  static final KeyDefaultPair<Integer> DEATH_MUSHROOM = new KeyDefaultPair<Integer>(CATEGORY_NAME + "." + "DeathMushroom", 5);
  static final KeyDefaultPair<Integer> STAR = new KeyDefaultPair<Integer>(CATEGORY_NAME + "." + "Star", 8);
  static final KeyDefaultPair<Integer> BOMB = new KeyDefaultPair<Integer>(CATEGORY_NAME + "." + "Bomb", 4);
  static final KeyDefaultPair<Integer> TANOOKI = new KeyDefaultPair<Integer>(CATEGORY_NAME + "." + "Tanooki", 6);
  static final KeyDefaultPair<Integer> TIMER = new KeyDefaultPair<Integer>(CATEGORY_NAME + "." + "Timer", 4);
  static final KeyDefaultPair<Integer> BULLET_BILL = new KeyDefaultPair<Integer>(CATEGORY_NAME + "." + "BulletBill", 2);
  static final KeyDefaultPair<Integer> FIREBALL = new KeyDefaultPair<Integer>(CATEGORY_NAME + "." + "Fireball", 4);
  static final KeyDefaultPair<Integer> POW_BLOCK = new KeyDefaultPair<Integer>(CATEGORY_NAME + "." + "PowBlock", 2);

  @Getter @Setter UseSettingsFrom useSettingsFrom;
  @Getter @Setter ItemSet itemSet;  
  @Getter @Setter int greenMushroom;
  @Getter @Setter int redMushroom;
  @Getter @Setter int blueMushroom;
  @Getter @Setter int yellowMushroom;
  @Getter @Setter int questionMushroom;
  @Getter @Setter int flower;
  @Getter @Setter int hammer;
  @Getter @Setter int boomerang;
  @Getter @Setter int deathMushroom;
  @Getter @Setter int star;
  @Getter @Setter int bomb;
  @Getter @Setter int tanooki;
  @Getter @Setter int timer;
  @Getter @Setter int bulletBill;
  @Getter @Setter int fireball;
  @Getter @Setter int powBlock;
  
  public ItemSelectionSettings(PropertiesWrapper prop){
    useSettingsFrom = prop.getEnum(USE_SETTINGS_FROM);
    itemSet = prop.getEnum(ITEM_SET);
    
    greenMushroom    = prop.getInt(GREEN_MUSHROOM);
    redMushroom      = prop.getInt(RED_MUSHROOM);
    blueMushroom     = prop.getInt(BLUE_MUSHROOM);
    yellowMushroom   = prop.getInt(YELLOW_MUSHROOM);
    questionMushroom = prop.getInt(QUESTION_MUSHROOM);
    flower           = prop.getInt(FLOWER);
    hammer           = prop.getInt(HAMMER);
    boomerang        = prop.getInt(BOOMERANG);
    deathMushroom    = prop.getInt(DEATH_MUSHROOM);
    star             = prop.getInt(STAR);
    bomb             = prop.getInt(BOMB);
    tanooki          = prop.getInt(TANOOKI);
    timer            = prop.getInt(TIMER);
    bulletBill       = prop.getInt(BULLET_BILL);
    fireball         = prop.getInt(FIREBALL);
    powBlock         = prop.getInt(POW_BLOCK);
  }
  
  @Override
  public void add(PropertiesWrapper prop) {
    prop.setProperty(GREEN_MUSHROOM, greenMushroom);
    prop.setProperty(RED_MUSHROOM, redMushroom);
    prop.setProperty(BLUE_MUSHROOM, blueMushroom);
    prop.setProperty(YELLOW_MUSHROOM, yellowMushroom);
    prop.setProperty(QUESTION_MUSHROOM, questionMushroom);
    prop.setProperty(FLOWER, flower);
    prop.setProperty(HAMMER, hammer);
    prop.setProperty(BOOMERANG, boomerang);
    prop.setProperty(DEATH_MUSHROOM, deathMushroom);
    prop.setProperty(STAR, star);
    prop.setProperty(BOMB, bomb);
    prop.setProperty(TANOOKI, tanooki);
    prop.setProperty(TIMER, timer);
    prop.setProperty(BULLET_BILL, bulletBill);
    prop.setProperty(FIREBALL, fireball);
    prop.setProperty(POW_BLOCK, powBlock);
  }

  @Override
  public String getCategoryName() {
    return CATEGORY_NAME;
  }
}
