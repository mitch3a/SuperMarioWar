package smw.gfx;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import smw.Drawable;
import smw.Updatable;
import smw.entity.Player;
import smw.settings.Debug;
import smw.world.Tile;
import smw.world.TileSheet;
import smw.world.TileSheetManager;

/** AboveArrow is tied to a player as an off screen indicator. */
public class AboveArrow implements Drawable, Updatable {
  private final Player player;
  private final static TileSheet tileSheet = TileSheetManager.getInstance().getTileSheet("gfx/packs/Classic/eyecandy/abovearrows.png");
  private final int tileSheetX;
  private static final int tileSheetY = 0;
  private static final int y = 0;
  private static final int ARROW_IMAGE_HEIGHT = tileSheet.getHeight();
  private static final int KILL_TIME_MS = 5000;
  private static final int TIMER_OFFSET_X = 8;
  private static final int TIMER_OFFSET_Y = 12;
  private int countDown_ms = KILL_TIME_MS;
  
  /**
   * Creates above arrow instance that is tied to the provided player used for off screen conditions.
   * @param player The player to associate the arrow with.
   */
  public AboveArrow(Player player) {
    this.player = player;
    tileSheetX = player.color.ordinal() * Tile.SIZE;
  }

  @Override
  public void draw(Graphics2D g, ImageObserver io) {
    // Only draw arrow when the player is alive and off the screen.
    if (!player.isDead() && (player.y <= -Sprite.IMAGE_HEIGHT)) {
      g.drawImage(tileSheet.getTileImg(tileSheetX, tileSheetY, Tile.SIZE, ARROW_IMAGE_HEIGHT), (int)player.x, y, io);
      Font.getInstance().drawBoxedNumber(g, countDown_ms / 1000, player.color.ordinal(), (int)player.x + TIMER_OFFSET_X, y + TIMER_OFFSET_Y, io);
    }
  }

  @Override
  public boolean shouldBeRemoved() {
    return false;
  }

	@Override
	public void update(float timeDif_ms) {
	  // If player is off screen, count down, if expired, kill player and reset timer.
		if (!player.isDead() && (player.y <= -Sprite.IMAGE_HEIGHT)) {
		  if (Debug.PLAYER_DEATH_OFFSCREEN_TIMER) {
  			countDown_ms -= timeDif_ms;
  			if (countDown_ms <= 0) {
  			  countDown_ms = KILL_TIME_MS;
  				player.death();
  			}
		  }
		}
	}
	
}
