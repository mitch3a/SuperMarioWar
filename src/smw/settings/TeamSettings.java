package smw.settings;

import lombok.Getter;
import lombok.Setter;

public class TeamSettings implements SubSetting{
  static final String CATEGORY_NAME = "Team";
    
  public enum PlayerCollision{
    off, assist, on;
  }
  
  public enum Colors{
    team, individual;
  }
  
  public enum TournamentControl{
    all, gameWinner, gameLoser, leadingTeams, trailingTeams, random, randomLoser, roundRobin;
  }
  
  static final KeyDefaultPairEnum<PlayerCollision>   PLAYER_COLLISION   = new KeyDefaultPairEnum<PlayerCollision>  (CATEGORY_NAME + "." + "PlayerCollision",   PlayerCollision.on);
  static final KeyDefaultPairEnum<Colors>            COLORS             = new KeyDefaultPairEnum<Colors>           (CATEGORY_NAME + "." + "Colors",            Colors.individual);
  static final KeyDefaultPairEnum<TournamentControl> TOURNAMENT_CONTROL = new KeyDefaultPairEnum<TournamentControl>(CATEGORY_NAME + "." + "TournamentControl", TournamentControl.all);
  
  @Getter @Setter PlayerCollision playerCollision;
  @Getter @Setter Colors colors;
  @Getter @Setter TournamentControl tournamentControl;
  
  public TeamSettings(PropertiesWrapper prop){
    playerCollision   = prop.getEnum(PLAYER_COLLISION);
    colors            = prop.getEnum(COLORS);
    tournamentControl = prop.getEnum(TOURNAMENT_CONTROL);
  }
  
  @Override
  public void add(PropertiesWrapper prop) {
    prop.setProperty(PLAYER_COLLISION,   playerCollision);
    prop.setProperty(COLORS,             colors);
    prop.setProperty(TOURNAMENT_CONTROL, tournamentControl);
  }
  
  @Override
  public String getCategoryName() {
    return CATEGORY_NAME;
  }
}
