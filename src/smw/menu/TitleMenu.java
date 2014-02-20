package smw.menu;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import smw.Game;
import smw.gfx.Font;
import smw.gfx.Palette;
import smw.ui.PlayerControlBase;

public class TitleMenu extends Menu {
  
  /** Defines the type of menu items. */
  public enum ItemType {PIPE, PLAYER_SELECT};
  
  /** Encapsulates all data needed for a menu item. */
  final class MenuItem {
    public ItemType type;
    public String label;
    public int length;
    public int x;
    public int y;

    public MenuItem(ItemType type, String label, int length, int x, int y) {
      this.type = type;
      this.label = label;
      this.length = length;
      this.x = x;
      this.y = y;
    }
  }
  
  private static final int PIPE_TILE_SIZE = 32;
  private static final int PIPE_TEXT_OFFSET_X = 24;
  private static final int PIPE_TEXT_OFFSET_Y = 4;
  
  private BufferedImage backgroundImg;
  
  // TODO - Should probably just load these puppies into one sprite sheet then manage it accordingly
  private BufferedImage leftPipe;
  private BufferedImage middlePipe;
  private BufferedImage rightPipe;
  private BufferedImage selectFieldImg;
  private BufferedImage leftField;
  private BufferedImage middleField;
  private BufferedImage rightField;
  
  private List<MenuItem> menuItems = new ArrayList<MenuItem>();
  
  /** The current user selection. */
  private int selection;
  private int veritcalOptionCount;
  
  /** Indicates if sound setup is needed for the menu. */
  private boolean setupSound = true; 
    
  public TitleMenu() {
    try {
      // Read background image and darken by 15%.
      backgroundImg = ImageIO.read(this.getClass().getClassLoader().getResource("menu/menu_background.png"));
      RescaleOp op = new RescaleOp(0.85f, 0, null);
      backgroundImg = op.filter(backgroundImg, null);
      
      // Read title, implement transparency, then center on top of background image.
      BufferedImage tempImg = ImageIO.read(this.getClass().getClassLoader().getResource("menu/menu_smw.png"));
      BufferedImage titleImg = new BufferedImage(tempImg.getWidth(), tempImg.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
      titleImg.getGraphics().drawImage(tempImg, 0, 0, null);
      Palette p = Palette.getInstance();
      p.loadPalette();
      p.implementTransparent(titleImg);
      Graphics2D g = backgroundImg.createGraphics();
      g.drawImage(titleImg, (backgroundImg.getWidth() - titleImg.getWidth()) / 2, 30, null);
      
      // Read the sprite sheet for the pipe menu fields.
      tempImg = ImageIO.read(this.getClass().getClassLoader().getResource("menu/menu_selectfield.png"));
      selectFieldImg = new BufferedImage(tempImg.getWidth(), tempImg.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
      selectFieldImg.getGraphics().drawImage(tempImg, 0, 0, null);
      p.implementTransparent(selectFieldImg);
      
      leftPipe = selectFieldImg.getSubimage(0, 0, 32, 32);
      middlePipe = selectFieldImg.getSubimage(32, 0, 32, 32);
      rightPipe = selectFieldImg.getSubimage(32 * 15, 0, 32, 32);
            
      // Read the sprite sheet for the player select menu field.
      tempImg = ImageIO.read(this.getClass().getClassLoader().getResource("menu/menu_player_select.png"));
      BufferedImage playerSelect = new BufferedImage(tempImg.getWidth(), tempImg.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
      playerSelect.getGraphics().drawImage(tempImg, 0, 0, null);
      p.implementTransparent(playerSelect);
      
      leftField = playerSelect.getSubimage(0, 0, 16, 64);
      middleField = playerSelect.getSubimage(32, 0, 16, 64);
      rightField = playerSelect.getSubimage(32 * 15 + 16, 0, 16, 64);
      
      menuItems.add(new MenuItem(ItemType.PIPE, "Start", 310, 120, 210));
      menuItems.add(new MenuItem(ItemType.PIPE, "Go!", 80, 440, 210));
      menuItems.add(new MenuItem(ItemType.PLAYER_SELECT, "Players", 400, 120, 250));
      menuItems.add(new MenuItem(ItemType.PIPE, "Options", 400, 120, 322));
      menuItems.add(new MenuItem(ItemType.PIPE, "Controls", 400, 120, 362));
      menuItems.add(new MenuItem(ItemType.PIPE, "Exit", 400, 120, 402));
      veritcalOptionCount = menuItems.size();
      
      // TODO - could make a menu item be responsible for drawing itself...
      for (MenuItem m : menuItems) {
        switch (m.type) {
        case PIPE:
          drawPipeField(g, m.label, m.length, m.x, m.y);
          break;
        case PLAYER_SELECT:
          drawPlayerSelectField(g, m.label, m.x, m.y);
          break;
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Draws a pipe field at the specified length with the provided text if any.
   * A pipe field will always be at least the start and end pipe segments.
   * @param g The graphics to draw on.
   * @param text The text to draw.
   * @param length The length of the pipe field in pixels.
   * @param x The graphics x pixel coordinate.
   * @param y The graphics y pixel coordinate.
   */
  // TODO - probably need a way to specify if it's selected or not, that will change which pipe images we use from the sprite sheet!
  public void drawPipeField(Graphics2D g, String text, int length, int x, int y) {
    final int segments = (length / PIPE_TILE_SIZE) - 2;
    final int leftOver = length % PIPE_TILE_SIZE;
    final int textX = x + PIPE_TEXT_OFFSET_X;
    final int textY = y + PIPE_TEXT_OFFSET_Y;
    
    g.drawImage(leftPipe, x, y, null);
    x += PIPE_TILE_SIZE;
    for (int i = 0; i < segments; i++) {
      g.drawImage(middlePipe, x, y, null);
      x += PIPE_TILE_SIZE;
    }
    if (leftOver > 0) {
      g.drawImage(middlePipe, x, y, null);
      x += leftOver;
    }
    g.drawImage(rightPipe, x, y, null);
    if (text != null) {
      Font.getInstance().drawLargeText(g, text, textX, textY, null);
    }
  }
    
  // TODO
  public void drawPlayerSelectField(Graphics2D g, String text, int x, int y) {
    Font font = Font.getInstance();
    
    g.drawImage(leftField, x, y, null);
    x+= leftField.getWidth();
    for (int i = 0; i < 7; i++) {
      g.drawImage(middleField, x, y, null);
      x+=middleField.getWidth();
    }
    g.drawImage(rightField, x, y, null);
    x+= rightField.getWidth();
    font.drawLargeText(g, text, 128+16, y+18, null);
    
    x-=3; // Nudge player select up against first box.
    g.drawImage(leftField, x, y, null);
    x+= leftField.getWidth();
    for (int i = 0; i < 14; i++) {
      g.drawImage(middleField, x, y, null);
      x+=middleField.getWidth();
    }
    x-=13;
    g.drawImage(middleField, x, y, null);
    x+=middleField.getWidth();
    
    g.drawImage(rightField, x, y, null);
    x+= rightField.getWidth();
    
    // TODO - get and store player, bot, none icons, also small menu text for font..., selection animations
  }
  
  @Override
  public void draw(Graphics2D g, ImageObserver io) {
    drawBackground(g, io);

    // TODO - this should be in a drawing method probably
    MenuItem m = menuItems.get(selection);
    switch (m.type) {
    case PIPE:
      // drawPipeField(g, m.label, m.length, m.x, m.y);
      drawPipeField(g, "TEST TODO", m.length, m.x, m.y);
      break;
    case PLAYER_SELECT:
      drawPlayerSelectField(g, m.label, m.x, m.y);
      break;
    }
  }

  @Override
  public void update(boolean up, boolean down, boolean left, boolean right, boolean select, boolean esc) {
    if (setupSound) {
      Game.soundPlayer.playMenuMusic();
    }
    
    // TODO - update based on user selection, need to cleanup, and handle player select!
    if (up)
      selection++;
    if (down)
      selection--;
    // Handle veritcal selection wrap.
    if (selection < 0)
      selection += veritcalOptionCount;
    else if (selection >= veritcalOptionCount)
      selection -= veritcalOptionCount;
  }
  
  public void drawBackground(Graphics2D g, ImageObserver io){
    g.drawImage(backgroundImg, 0, 0, io);
  }

}
