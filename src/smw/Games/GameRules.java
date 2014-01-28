package smw.Games;

import smw.entity.Player;
import smw.level.Level;

/***********************************************
 * This class is to enforce the rules of a game.
 * This includes:
 *   1. Scoring
 *   2. Collisions
 ***********************************************/
public abstract class GameRules {
  
  public GameRules(){
    
  }
  
  /**************************************************
   * This method will return true if the game is over.
   **************************************************/
  public abstract boolean isGameOver();
  
  /**************************************************
   * This method will update the player in any way
   * a collision isn't handled. For instance, if a
   * player is timed for something, this would be a
   * place to update the player score
   * 
   * @param player
   **************************************************/
  public abstract void update(Player player);
  
  /**************************************************
   * This method will handle any collision between a
   * player and a map. It will update both with any
   * changes to expected movement, actions, etc.
   * 
   * @param level
   * @param player
   **************************************************/
  public abstract void collide(Level level, Player player);
  
  /**************************************************
   * This method will handle any collision between a
   * player and a map. It will update both with any
   * changes to expected movement, actions, etc.
   * 
   * @param level
   * @param player
   **************************************************/
  public abstract void collidePlayers(Player p1, Player p2);
}
