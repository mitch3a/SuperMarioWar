package smw.games.templates;

import smw.entity.Player;

public interface Rules {
  public void playersCollideX(Player p1, Player p2);
  public void playersCollideY(Player p1, Player p2);
}
