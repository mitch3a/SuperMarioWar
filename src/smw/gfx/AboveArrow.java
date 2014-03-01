package smw.gfx;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import smw.Drawable;
import smw.entity.Player;
import smw.world.Tile;
import smw.world.TileSheet;
import smw.world.TileSheetManager;

public class AboveArrow implements Drawable{
  final Player player;
  final static TileSheet tileSheet = TileSheetManager.getInstance().getTileSheet("gfx/packs/Classic/eyecandy/abovearrows.png");
  final private int tileSheetX;
  final private int tileSheetY;
  final static private int y = 0;
  final static private int ARROW_IMAGE_HEIGHT = tileSheet.getHeight();
  
  public AboveArrow(Player player){
    this.player = player;
    
    tileSheetX = player.color.index*Tile.SIZE;
    tileSheetY = 0;
  }

  @Override
  public void draw(Graphics2D g, ImageObserver io) {
    if(player.y <= -Sprite.IMAGE_HEIGHT){
      g.drawImage(tileSheet.getTileImg(tileSheetX, tileSheetY, Tile.SIZE, ARROW_IMAGE_HEIGHT), (int)(player.x), y, io);
    }
  }

  @Override
  public boolean shouldBeRemoved() {
    return false;
  }
}
