package smw.settings;

import java.util.Properties;

import lombok.Getter;
import lombok.Setter;

public class WeaponUseLimitsSettings implements SubSetting{
  static final String CATEGORY_NAME = "WeaponUseLimitsSettings";
  
  public enum Limit{
    unlimited, limit2, limit5, limit8, limit10, limit12, limit15, limit20, limit25, limit30, limit40, limit50;
  }
  
  static final KeyDefaultPairEnum<Limit> FIREBALL  = new KeyDefaultPairEnum<Limit>  (CATEGORY_NAME + "." + "Fireball",  Limit.unlimited);
  static final KeyDefaultPairEnum<Limit> HAMMER    = new KeyDefaultPairEnum<Limit>  (CATEGORY_NAME + "." + "Hammer",    Limit.unlimited);
  static final KeyDefaultPairEnum<Limit> BOOMERANG = new KeyDefaultPairEnum<Limit>  (CATEGORY_NAME + "." + "Boomerang", Limit.unlimited);
  static final KeyDefaultPairEnum<Limit> FEATHER   = new KeyDefaultPairEnum<Limit>  (CATEGORY_NAME + "." + "Feather",   Limit.unlimited);
  static final KeyDefaultPairEnum<Limit> LEAF      = new KeyDefaultPairEnum<Limit>  (CATEGORY_NAME + "." + "Leaf",      Limit.unlimited);
  static final KeyDefaultPairEnum<Limit> P_WING    = new KeyDefaultPairEnum<Limit>  (CATEGORY_NAME + "." + "PWing",     Limit.unlimited);
  static final KeyDefaultPairEnum<Limit> TANOOKI   = new KeyDefaultPairEnum<Limit>  (CATEGORY_NAME + "." + "Tanooki",   Limit.unlimited);
  static final KeyDefaultPairEnum<Limit> BOMB      = new KeyDefaultPairEnum<Limit>  (CATEGORY_NAME + "." + "Bomb",      Limit.unlimited);
  static final KeyDefaultPairEnum<Limit> WAND      = new KeyDefaultPairEnum<Limit>  (CATEGORY_NAME + "." + "Wand",      Limit.unlimited);
  
  @Getter @Setter Limit fireball;
  @Getter @Setter Limit hammer;
  @Getter @Setter Limit boomerang;
  @Getter @Setter Limit feather;
  @Getter @Setter Limit leaf;
  @Getter @Setter Limit pWing;
  @Getter @Setter Limit tanooki;
  @Getter @Setter Limit bomb;
  @Getter @Setter Limit wand;
  
  public WeaponUseLimitsSettings(PropertiesWrapper prop){
    fireball  = prop.getEnum(FIREBALL);
    hammer    = prop.getEnum(HAMMER);
    boomerang = prop.getEnum(BOOMERANG);
    feather   = prop.getEnum(FEATHER);
    leaf      = prop.getEnum(LEAF);
    pWing     = prop.getEnum(P_WING);
    tanooki   = prop.getEnum(TANOOKI);
    bomb      = prop.getEnum(BOMB);
    wand      = prop.getEnum(WAND);
  }
  
  @Override
  public void add(PropertiesWrapper prop) {
    prop.setProperty(FIREBALL, fireball);
    prop.setProperty(HAMMER, hammer);
    prop.setProperty(BOOMERANG, boomerang);
    prop.setProperty(FEATHER, feather);
    prop.setProperty(LEAF, leaf);
    prop.setProperty(P_WING, pWing);
    prop.setProperty(TANOOKI, tanooki);
    prop.setProperty(BOMB, bomb);
    prop.setProperty(WAND, wand);
  }
  
  @Override
  public String getCategoryName() {
    return CATEGORY_NAME;
  }
}