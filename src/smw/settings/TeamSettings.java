package smw.settings;

import java.util.Properties;
import java.util.logging.Logger;

public class TeamSettings implements SubSetting{
  static final String CATEGORY_NAME = "Team";
  static final Logger logger = Logger.getLogger(TeamSettings.class.getName());
  
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
  
  PlayerCollision playerCollision;
  Colors colors;
  TournamentControl tournamentControl;
  
  public TeamSettings(Properties prop){
    try{
      playerCollision = PlayerCollision.valueOf(prop.getProperty(KEY_PLAYER_COLLISION));
    } catch(Exception e){
      logger.warning("Bad value for " + KEY_PLAYER_COLLISION + " " + e.toString());
      playerCollision = PlayerCollision.on;
    }
    
    try{
      colors = Colors.valueOf(prop.getProperty(KEY_COLORS));
    } catch(Exception e){
      logger.warning("Bad value for " + KEY_COLORS + " " + e.toString());
      colors = Colors.team;
    }
    
    try{
      tournamentControl = TournamentControl.valueOf(prop.getProperty(KEY_TOURNAMENT_CONTROL));
    } catch(Exception e){
      logger.warning("Bad value for " + KEY_TOURNAMENT_CONTROL + " " + e.toString());
      tournamentControl = TournamentControl.all;
    }
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
