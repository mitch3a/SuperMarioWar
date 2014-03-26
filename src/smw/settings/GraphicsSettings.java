package smw.settings;

import lombok.Getter;
import lombok.Setter;

public class GraphicsSettings implements SubSetting{
  static final String CATEGORY_NAME = "GraphicsSettings";
  
  public enum DropTopLayer{
    foreground, background;
  }
  
  public enum FrameLimit{
    noLimit, fps10, fps15, fps20, fps25, fps30, fps35, fps40, fps45, fps50, fps55, fps62Normal, fps66, fps71, fps77, fps83, fps90, fps100, fps11, fps125, fps142, fps166, fps200, fps250, fps333, fps500;
  }
  
  public enum ScreenSize{
    windowed, fullScreen;
  }
  
  public enum GraphicsStyle{
    classic, retro;
  }
  
  static final KeyDefaultPairEnum<DropTopLayer>  DROP_TOP_LAYER = new KeyDefaultPairEnum<DropTopLayer> (CATEGORY_NAME + "." + "DropTopLayer", DropTopLayer.foreground);
  static final KeyDefaultPairEnum<FrameLimit>    FRAME_LIMIT    = new KeyDefaultPairEnum<FrameLimit>   (CATEGORY_NAME + "." + "FrameLimit", FrameLimit.noLimit);
  static final KeyDefaultPairEnum<ScreenSize>    SCREEN_SIZE    = new KeyDefaultPairEnum<ScreenSize>   (CATEGORY_NAME + "." + "ScreenSize", ScreenSize.windowed);
  static final KeyDefaultPairEnum<GraphicsStyle> MENU_GRAPHICS  = new KeyDefaultPairEnum<GraphicsStyle>(CATEGORY_NAME + "." + "MenuGraphics", GraphicsStyle.classic);
  static final KeyDefaultPairEnum<GraphicsStyle> WORLD_GRAPHICS = new KeyDefaultPairEnum<GraphicsStyle>(CATEGORY_NAME + "." + "WorldGraphics", GraphicsStyle.retro);
  static final KeyDefaultPairEnum<GraphicsStyle> GAME_GRAPHICS  = new KeyDefaultPairEnum<GraphicsStyle>(CATEGORY_NAME + "." + "GameGraphics", GraphicsStyle.retro);
  
  @Getter @Setter DropTopLayer  dropTopLayer;
  @Getter @Setter FrameLimit    frameLimit;
  @Getter @Setter ScreenSize    screenSize;
  @Getter @Setter GraphicsStyle menuGraphics;
  @Getter @Setter GraphicsStyle worldGraphics;
  @Getter @Setter GraphicsStyle gameGraphics;
  
  public GraphicsSettings(PropertiesWrapper prop){
    dropTopLayer  = prop.getEnum(DROP_TOP_LAYER);
    frameLimit    = prop.getEnum(FRAME_LIMIT);
    screenSize    = prop.getEnum(SCREEN_SIZE);
    menuGraphics  = prop.getEnum(MENU_GRAPHICS);
    worldGraphics = prop.getEnum(WORLD_GRAPHICS);
    gameGraphics  = prop.getEnum(GAME_GRAPHICS);
  }
  
  @Override
  public void add(PropertiesWrapper prop) {
    prop.setProperty(DROP_TOP_LAYER, dropTopLayer);
    prop.setProperty(FRAME_LIMIT, frameLimit);
    prop.setProperty(SCREEN_SIZE, screenSize);
    prop.setProperty(MENU_GRAPHICS, menuGraphics);
    prop.setProperty(WORLD_GRAPHICS, worldGraphics);
    prop.setProperty(GAME_GRAPHICS, gameGraphics);
  }
  
  @Override
  public String getCategoryName() {
    return CATEGORY_NAME;
  }
}