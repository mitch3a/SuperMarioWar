package smw.settings;

import java.util.Properties;

public class TeamSettings implements SubSetting{
  static final String CATEGORY_NAME = "Team";
  
  static final String KEY_PLAYER_COLLISION = "PlayerCollision";
  static final String KEY_COLORS = "Colors";
  static final String KEY_TOURNAMENT_CONTROL = "TournamentControl";
  
  public enum PlayerCollision{
    off, assist, on;
  }
  
  public enum Colors{
    team, individual;
  }
  
  public enum TournamentControl{
    all, gameWinner, gameLoser, leadingTeams, trailingTeams, random, randomLoser, roundRobin;
  }
  

  private static final PlayerCollision DEFAULT_PLAYER_COLLISION = PlayerCollision.on;
  private static final Colors DEFAULT_COLORS = Colors.individual;
  private static final TournamentControl DEFAULT_TOURNAMENT_CONTROL = TournamentControl.all;
  
  PlayerCollision playerCollision;
  Colors colors;
  TournamentControl tournamentControl;
  
  public TeamSettings(PropertiesWrapper prop){
    playerCollision = (PlayerCollision) prop.getEnum(PlayerCollision.class, KEY_PLAYER_COLLISION, DEFAULT_PLAYER_COLLISION);
    colors = (Colors) prop.getEnum(Colors.class, KEY_COLORS, DEFAULT_COLORS);
    tournamentControl = (TournamentControl) prop.getEnum(TournamentControl.class, KEY_TOURNAMENT_CONTROL, DEFAULT_TOURNAMENT_CONTROL);
  }
  
  @Override
  public void add(Properties prop) {
    prop.setProperty(KEY_PLAYER_COLLISION, playerCollision.toString());
    prop.setProperty(KEY_COLORS, colors.toString());
    prop.setProperty(KEY_TOURNAMENT_CONTROL, tournamentControl.toString());
  }
  
  @Override
  public String getCategoryName() {
    return CATEGORY_NAME;
  }
  
  public synchronized PlayerCollision getPlayerCollision(){
    return playerCollision;
  }
  
  public synchronized void setPlayerCollision(PlayerCollision playerCollision){
    this.playerCollision = playerCollision;
  }
  
  public synchronized Colors getColors(){
    return colors;
  }
  
  public synchronized void getColors(Colors colors){
    this.colors = colors;
  }
  
  public synchronized TournamentControl getTournamentControl(){
    return tournamentControl;
  }
  
  public synchronized void setTournamentControl(TournamentControl tournamentControl){
    this.tournamentControl =  tournamentControl;
  }
}
