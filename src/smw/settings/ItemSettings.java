package smw.settings;

import lombok.Getter;
import lombok.Setter;

public class ItemSettings implements SubSetting{
  static final String CATEGORY_NAME = "ItemSettings";
  
  public enum ItemUseSpeed{
    verySlow, slow, moderate, fast, veryFast;
  }
  
  public enum ItemSpawn{
    off, sec05, sec10, sec15, sec20, sec25, sec30, sec35, sec40, sec45, sec50, sec55, sec60;
  }
  
  public enum HiddenBlockHide{
    off, sec05, sec10, sec15, sec20, sec25, sec30, sec35, sec40, sec45, sec50, sec55, sec60;
  }
  
  public enum SwapStyle{
    blink, instant, walk;
  }
  
  public enum BonusWheel{
    tournamentWin, everyGame, off;
  }
  
  public enum BonusItem{
    untilNextSpin, keepAlways;
  }
  
  static final KeyDefaultPairEnum<ItemUseSpeed>    ITEM_USE_SPEED    = new KeyDefaultPairEnum<ItemUseSpeed>   (CATEGORY_NAME + "." + "ItemUseSpeed", ItemUseSpeed.moderate);
  static final KeyDefaultPairEnum<ItemSpawn>       ITEM_SPAWN        = new KeyDefaultPairEnum<ItemSpawn>      (CATEGORY_NAME + "." + "ItemSpawn", ItemSpawn.sec30);
  static final KeyDefaultPairEnum<HiddenBlockHide> HIDDEN_BLOCK_HIDE = new KeyDefaultPairEnum<HiddenBlockHide>(CATEGORY_NAME + "." + "HiddenBlockHide", HiddenBlockHide.sec30);
  static final KeyDefaultPairEnum<SwapStyle>       SWAP_STYLE        = new KeyDefaultPairEnum<SwapStyle>      (CATEGORY_NAME + "." + "SwapStyle", SwapStyle.blink);
  static final KeyDefaultPairEnum<BonusWheel>      BONUS_WHEEL       = new KeyDefaultPairEnum<BonusWheel>     (CATEGORY_NAME + "." + "BonusWheel", BonusWheel.tournamentWin);
  static final KeyDefaultPairEnum<BonusItem>       BONUS_ITEM        = new KeyDefaultPairEnum<BonusItem>      (CATEGORY_NAME + "." + "BonusItem", BonusItem.untilNextSpin);
  
  @Getter @Setter ItemUseSpeed    itemUseSpeed;
  @Getter @Setter ItemSpawn       itemSpawn;
  @Getter @Setter HiddenBlockHide hiddenBlockHide;
  @Getter @Setter SwapStyle       swapStyle;
  @Getter @Setter BonusWheel      bonusWheel;
  @Getter @Setter BonusItem       bonusItem;

  public ItemSettings(PropertiesWrapper prop){
    itemUseSpeed    = (ItemUseSpeed)    prop.getEnum(ITEM_USE_SPEED);
    itemSpawn       = (ItemSpawn)       prop.getEnum(ITEM_SPAWN);
    hiddenBlockHide = (HiddenBlockHide) prop.getEnum(HIDDEN_BLOCK_HIDE);
    swapStyle       = (SwapStyle)       prop.getEnum(SWAP_STYLE);
    bonusWheel      = (BonusWheel)      prop.getEnum(BONUS_WHEEL);
    bonusItem       = (BonusItem)       prop.getEnum(BONUS_ITEM);
  }
  
  @Override
  public void add(PropertiesWrapper prop) {
    prop.setProperty(ITEM_USE_SPEED,    itemUseSpeed);
    prop.setProperty(ITEM_SPAWN,        itemSpawn);
    prop.setProperty(HIDDEN_BLOCK_HIDE, hiddenBlockHide);
    prop.setProperty(SWAP_STYLE,        swapStyle);
    prop.setProperty(BONUS_WHEEL,       bonusWheel);
    prop.setProperty(BONUS_ITEM,        bonusItem);
  }
  
  @Override
  public String getCategoryName() {
    return CATEGORY_NAME;
  }
}
