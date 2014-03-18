package smw.settings;

import java.util.Properties;

import lombok.Getter;
import lombok.Setter;

//TODO
public class GraphicsSettings implements SubSetting{
  static final String CATEGORY_NAME = "GraphicsSettings";
  
  public enum ShieldStyle{
    noShield, shield, soft, softWithStomp, hard;
  }
  
  static final KeyDefaultPairEnum<ShieldStyle> SHIELD_STYLE      = new KeyDefaultPairEnum<ShieldStyle>  (CATEGORY_NAME + "." + "ShieldStyle", ShieldStyle.noShield);
  
  @Getter @Setter ShieldStyle shieldStyle;
  
  public GraphicsSettings(PropertiesWrapper prop){
    shieldStyle   = (ShieldStyle)   prop.getEnum(SHIELD_STYLE);
  }
  
  @Override
  public void add(Properties prop) {
    prop.setProperty(SHIELD_STYLE.key,   shieldStyle.toString());
  }
  
  @Override
  public String getCategoryName() {
    return CATEGORY_NAME;
  }
}