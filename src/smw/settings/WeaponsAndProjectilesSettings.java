package smw.settings;

import lombok.Getter;
import lombok.Setter;

public class WeaponsAndProjectilesSettings implements SubSetting{
  static final String CATEGORY_NAME = "WeaponsAndProjectilesSettings";
  
  public enum LifeShort{
    seconds1, seconds2, seconds3, seconds4, seconds5, seconds6, seconds7, seconds8, seconds9, seconds10;
  }
  
  public enum LifeLong{
    seconds1, seconds2, seconds3, seconds4, seconds5, seconds6, seconds7, seconds8, seconds9, seconds10, seconds15, seconds20, seconds25, seconds30, unlimited;
  }
  
  public enum Style{
    smb3, zelda, random, flat;
  }
  
  public enum Jumps{
    one, two, three, four, five
  }
  
  public enum Freeze{
    seconds1, seconds2, seconds3, seconds4, seconds5, seconds6, seconds7, seconds8, seconds9, seconds10, seconds12, seconds15, seconds18, seconds20;
  }
  
  public enum HammerLife{
    noLimit, milliseconds500, milliseconds600, milliseconds700, milliseconds800, milliseconds900, milliseconds1000, milliseconds1100, milliseconds1200;
  }
  
  public enum HammerDelay{
    noLimit, milliseconds100, milliseconds200, milliseconds300, milliseconds400, milliseconds500, milliseconds600, milliseconds700, milliseconds800, milliseconds900, milliseconds1000;
  }
  
  public enum HammerPower{
    oneKill, multipleKills;
  }
  
  static final KeyDefaultPairEnum<LifeShort> FIREBALL_LIFE  = new KeyDefaultPairEnum<LifeShort>  (CATEGORY_NAME + "." + "FireballLife", LifeShort.seconds5);
  static final KeyDefaultPairEnum<Jumps> FEATHER_JUMPS      = new KeyDefaultPairEnum<Jumps>      (CATEGORY_NAME + "." + "FeatherJumps", Jumps.one);  
  static final KeyDefaultPairEnum<Style> BOOMERANG_STYLE    = new KeyDefaultPairEnum<Style>      (CATEGORY_NAME + "." + "BoomerangStyle", Style.smb3);
  static final KeyDefaultPairEnum<LifeShort> BOOMERANG_LIFE = new KeyDefaultPairEnum<LifeShort>  (CATEGORY_NAME + "." + "BoomerangLife", LifeShort.seconds5);
  static final KeyDefaultPairEnum<LifeLong>   SHELL_LIFE    = new KeyDefaultPairEnum<LifeLong>   (CATEGORY_NAME + "." + "FireballLife", LifeLong.seconds5);
  static final KeyDefaultPairEnum<Freeze>     WAND_FREEZE   = new KeyDefaultPairEnum<Freeze>     (CATEGORY_NAME + "." + "WandFreeze",   Freeze.seconds5);
  static final KeyDefaultPairEnum<HammerLife> HAMMER_LIFE   = new KeyDefaultPairEnum<HammerLife> (CATEGORY_NAME + "." + "HammerLife", HammerLife.milliseconds500);
  static final KeyDefaultPairEnum<HammerDelay> HAMMER_DELAY = new KeyDefaultPairEnum<HammerDelay>(CATEGORY_NAME + "." + "HammerDelay", HammerDelay.milliseconds100);
  static final KeyDefaultPairEnum<HammerPower> HAMMER_POWER = new KeyDefaultPairEnum<HammerPower>(CATEGORY_NAME + "." + "HammerPower", HammerPower.oneKill);
  static final KeyDefaultPairEnum<LifeLong> BLUE_LIFE  = new KeyDefaultPairEnum<LifeLong>(CATEGORY_NAME + "." + "BlueLife", LifeLong.seconds5);
  static final KeyDefaultPairEnum<LifeLong> GRAY_LIFE  = new KeyDefaultPairEnum<LifeLong>(CATEGORY_NAME + "." + "GrayLife", LifeLong.seconds5);
  static final KeyDefaultPairEnum<LifeLong> RED_LIFE   = new KeyDefaultPairEnum<LifeLong>(CATEGORY_NAME + "." + "RedLife",  LifeLong.seconds5);
  
  @Getter @Setter LifeShort fireballLife;
  @Getter @Setter Jumps featherJumps;
  @Getter @Setter Style boomerangStyle;
  @Getter @Setter LifeShort boomerangLive;
  @Getter @Setter LifeLong shellLife;
  @Getter @Setter Freeze wandFreeze;
  @Getter @Setter HammerLife hammerLife;
  @Getter @Setter HammerDelay hammerDelay;
  @Getter @Setter HammerPower hammerPower;
  @Getter @Setter LifeLong blueLong;
  @Getter @Setter LifeLong grayLong;
  @Getter @Setter LifeLong redLong;
  
  public WeaponsAndProjectilesSettings(PropertiesWrapper prop){
    fireballLife   = prop.getEnum(FIREBALL_LIFE);
    featherJumps   = prop.getEnum(FEATHER_JUMPS);
    boomerangStyle = prop.getEnum(BOOMERANG_STYLE);
    boomerangLive  = prop.getEnum(BOOMERANG_LIFE);
    shellLife   = prop.getEnum(SHELL_LIFE);
    wandFreeze  = prop.getEnum(WAND_FREEZE);
    hammerLife  = prop.getEnum(HAMMER_LIFE);
    hammerDelay = prop.getEnum(HAMMER_DELAY);
    hammerPower = prop.getEnum(HAMMER_POWER);
    blueLong = prop.getEnum(BLUE_LIFE);
    grayLong = prop.getEnum(GRAY_LIFE);
    redLong  = prop.getEnum(RED_LIFE);
  }
  
  @Override
  public void add(PropertiesWrapper prop) {
    prop.setProperty(FIREBALL_LIFE,   fireballLife);
    prop.setProperty(FEATHER_JUMPS,   featherJumps);
    prop.setProperty(BOOMERANG_STYLE, boomerangStyle);
    prop.setProperty(BOOMERANG_LIFE,  boomerangLive);
    prop.setProperty(SHELL_LIFE,   shellLife);
    prop.setProperty(WAND_FREEZE,  wandFreeze);
    prop.setProperty(HAMMER_LIFE,  hammerLife);
    prop.setProperty(HAMMER_DELAY, hammerDelay);
    prop.setProperty(HAMMER_POWER, hammerPower);
    prop.setProperty(BLUE_LIFE, blueLong);
    prop.setProperty(GRAY_LIFE, grayLong);
    prop.setProperty(RED_LIFE,  redLong);
  }
  
  @Override
  public String getCategoryName() {
    return CATEGORY_NAME;
  }
}