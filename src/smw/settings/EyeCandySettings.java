package smw.settings;

import lombok.Getter;
import lombok.Setter;

public class EyeCandySettings implements SubSetting{
  static final String CATEGORY_NAME = "EyeCandySettings";
  
  public enum SpawnStyle{
    swirl, instant, door;
  }
  
  public enum AwardStyle{
    fireworks, spiral, ring, souls, text, none;
  }
  
  public enum ScoreLocation{
    top, bottom, corners;
  }
  
  static final KeyDefaultPairEnum<SpawnStyle>    SPAWN_STYLE    = new KeyDefaultPairEnum<SpawnStyle>   (CATEGORY_NAME + "." + "SpawnStyle", SpawnStyle.swirl);
  static final KeyDefaultPairEnum<AwardStyle>    AWARD_STYLE    = new KeyDefaultPairEnum<AwardStyle>   (CATEGORY_NAME + "." + "AwardStyle", AwardStyle.fireworks);
  static final KeyDefaultPairEnum<ScoreLocation> SCORE_LOCATION = new KeyDefaultPairEnum<ScoreLocation>(CATEGORY_NAME + "." + "StoreLocation", ScoreLocation.top);
  static final KeyDefaultPair<Boolean> SCREEN_CRUNCH    = new KeyDefaultPair<Boolean>(CATEGORY_NAME + "." + "ScreenCrunch", false);
  static final KeyDefaultPair<Boolean> LEADER_CROWN     = new KeyDefaultPair<Boolean>(CATEGORY_NAME + "." + "LeaderCrown", false);
  static final KeyDefaultPair<Boolean> START_COUNTDOWN  = new KeyDefaultPair<Boolean>(CATEGORY_NAME + "." + "StartCountdown", false);
  static final KeyDefaultPair<Boolean> SHOW_MODE        = new KeyDefaultPair<Boolean>(CATEGORY_NAME + "." + "ShowMode", false);
  static final KeyDefaultPair<Boolean> DEAD_TEAM_NOTICE = new KeyDefaultPair<Boolean>(CATEGORY_NAME + "." + "DeadTeamNotice", false);
  
  @Getter @Setter SpawnStyle    spawnStyle;
  @Getter @Setter AwardStyle    awardStyle;
  @Getter @Setter ScoreLocation scoreLocation;
  @Getter @Setter boolean screenCrunch;
  @Getter @Setter boolean leaderCrown;
  @Getter @Setter boolean startCountdown;
  @Getter @Setter boolean showMode;
  @Getter @Setter boolean deadTeamNotice;
  
  public EyeCandySettings(PropertiesWrapper prop){
    spawnStyle     = prop.getEnum(SPAWN_STYLE);
    awardStyle     = prop.getEnum(AWARD_STYLE);
    scoreLocation  = prop.getEnum(SCORE_LOCATION);
    screenCrunch   = prop.getBoolean(SCREEN_CRUNCH);
    leaderCrown    = prop.getBoolean(LEADER_CROWN);
    startCountdown = prop.getBoolean(START_COUNTDOWN);
    showMode       = prop.getBoolean(SHOW_MODE);
    deadTeamNotice = prop.getBoolean(DEAD_TEAM_NOTICE);
  }
  
  @Override
  public void add(PropertiesWrapper prop) {
    prop.setProperty(SPAWN_STYLE,      spawnStyle);
    prop.setProperty(AWARD_STYLE,      awardStyle);
    prop.setProperty(SCORE_LOCATION,   scoreLocation);
    prop.setProperty(SCREEN_CRUNCH,    screenCrunch);
    prop.setProperty(LEADER_CROWN,     leaderCrown);
    prop.setProperty(START_COUNTDOWN,  startCountdown);
    prop.setProperty(SHOW_MODE,        showMode);
    prop.setProperty(DEAD_TEAM_NOTICE, deadTeamNotice);
  }
  
  @Override
  public String getCategoryName() {
    return CATEGORY_NAME;
  }
}