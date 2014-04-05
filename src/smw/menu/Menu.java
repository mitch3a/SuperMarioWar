package smw.menu;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RescaleOp;
import java.io.IOException;

import javax.imageio.ImageIO;

import smw.gfx.Font;
import smw.gfx.Palette;

/** Base menu class for all menus in game. */
public abstract class Menu {
    
  public static MenuInput input = new MenuInput();
  
  private static final int PIPE_TILE_SIZE = 32;
  private static final int PIPE_TEXT_OFFSET_X = 24;
  private static final int PIPE_TEXT_OFFSET_Y = 4;
  
  private static final int LEFT_INDEX = 0;
  private static final int MID_INDEX = 1;
  private static final int RIGHT_INDEX = 2;
  
  private static final int GRN_NOT_SEL = 0;
  private static final int GRN_SEL = 1;
  private static final int GRAY_NOT_SEL = 2;
  private static final int GRAY_SEL = 3;
  
  /** The background image. */
  protected BufferedImage backgroundImg;
  
  /** Pipe menu field images, indexed by: left/middle/right, green/gray selected or not. */
  private BufferedImage pipes[][] = new BufferedImage[3][4]; 
  
  /** Select field images to be indexed by: left/middle/right, selected or not. */
  private BufferedImage selectField[][] = new BufferedImage[3][2];
  
  private BufferedImage playerSelectIcons[] = new BufferedImage[3];
  
  private BufferedImage playerSelectAnimation[] = new BufferedImage[4];
  
  /** Blue field images, to be index by: left/middle/right. */
  private BufferedImage blueField[] = new BufferedImage[3];
  
  protected BufferedImage titleImg;
  
  //TODO - should be using Mitch's settings probably
  protected static int playerSettings[] = new int[4];
  
  /** Constructor to read in all of the needed image files to create menus. */
  Menu() {
    // Read background image and darken by 15%.
    try {
      backgroundImg = ImageIO.read(this.getClass().getClassLoader().getResource("menu/menu_background.png"));
      RescaleOp op = new RescaleOp(0.85f, 0, null);
      backgroundImg = op.filter(backgroundImg, null);
      
      // Read title, implement transparency, then center on top of background image.
      BufferedImage tempImg = ImageIO.read(this.getClass().getClassLoader().getResource("menu/menu_smw.png"));
      titleImg = new BufferedImage(tempImg.getWidth(), tempImg.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
      titleImg.getGraphics().drawImage(tempImg, 0, 0, null);
      Palette p = Palette.getInstance();
      p.loadPalette();
      p.implementTransparent(titleImg);
      
      // Read the sprite sheet for the pipe menu fields.
      tempImg = ImageIO.read(this.getClass().getClassLoader().getResource("menu/menu_selectfield.png"));
      BufferedImage selectFieldImg = new BufferedImage(tempImg.getWidth(), tempImg.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
      selectFieldImg.getGraphics().drawImage(tempImg, 0, 0, null);
      p.implementTransparent(selectFieldImg);
      
      // Setup buffered images for all types of pipes.
      for (int i = 0; i < 4; i++) {
        int y = 0;
        switch (i) {
        case GRN_NOT_SEL:
          y = 0;
          break;
        case GRN_SEL:
          y = 32;
          break;
        case GRAY_NOT_SEL:
          y = 128;
          break;
        case GRAY_SEL:
          y = 160;
          break;
        }
        pipes[LEFT_INDEX][i] = selectFieldImg.getSubimage(0, y, 32, 32);
        pipes[MID_INDEX][i] = selectFieldImg.getSubimage(32, y, 32, 32);
        pipes[RIGHT_INDEX][i] = selectFieldImg.getSubimage(480, y, 32, 32);
      }
            
      // Read the sprite sheet for the player select menu field.
      tempImg = ImageIO.read(this.getClass().getClassLoader().getResource("menu/menu_player_select.png"));
      BufferedImage playerSelect = new BufferedImage(tempImg.getWidth(), tempImg.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
      playerSelect.getGraphics().drawImage(tempImg, 0, 0, null);
      p.implementTransparent(playerSelect);
      
      for (int i = 0; i < 2; i++) {
        final int y = (i == 1) ? 64 : 0; 
        selectField[LEFT_INDEX][i] = playerSelect.getSubimage(0, y, 16, 64);
        selectField[MID_INDEX][i] = playerSelect.getSubimage(32, y, 16, 64);
        selectField[RIGHT_INDEX][i] = playerSelect.getSubimage(32 * 15 + 16, y, 16, 64);
      }
      
      for (int i = 0; i < 3; i++) {
        playerSelectIcons[i] = playerSelect.getSubimage(i * 34 + 32, 206, 34, 32);
      }
      
      for (int i = 0; i < 4; i++) {
        playerSelectAnimation[i] = playerSelect.getSubimage(i * 75 + 36, 132, 75, 75); 
      }
      
      // Store the plain blue field image.
      tempImg = ImageIO.read(this.getClass().getClassLoader().getResource("menu/menu_plain_field.png"));
      BufferedImage plainFieldImg = new BufferedImage(tempImg.getWidth(), tempImg.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
      plainFieldImg.getGraphics().drawImage(tempImg, 0, 0, null);
      p.implementTransparent(plainFieldImg);
      blueField[LEFT_INDEX] = plainFieldImg.getSubimage(0, 0, 32, 32);
      blueField[MID_INDEX] = plainFieldImg.getSubimage(32, 0, 32, 32);
      blueField[RIGHT_INDEX] = plainFieldImg.getSubimage(480, 0, 32, 32);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Draws the menu.
   * @param g
   * @param io
   */
  public abstract void draw(Graphics2D g, ImageObserver io);
      
  /**
   * Updates the menu.
   * @param deltaTime_ms
   */
  public abstract void update(double deltaTime_ms);
  
  /**
   * Draws the background image.
   * @param g
   * @param io
   */
  public void drawBackground(Graphics2D g, ImageObserver io){
    g.drawImage(backgroundImg, 0, 0, io);
  }
  
  /**
   * Draws the provided menu item.
   * @param g
   * @param m
   * @param selected
   */
  public void drawMenuItem(Graphics2D g, MenuItem m, boolean selected) {
    switch (m.type) {
    case PIPE_GREEN:
      drawPipeField(g, m.label, m.length, m.x, m.y, selected ? GRN_SEL : GRN_NOT_SEL, m.centerText);
      break;
    case PIPE_GRAY:
      drawPipeField(g, m.label, m.length, m.x, m.y, selected ? GRAY_SEL : GRAY_NOT_SEL, m.centerText);
      break;
    case PLAYER_SELECT:
      drawPlayerSelectField(g, m.label, m.x, m.y, selected);
      break;
      case PLAYER_SELECT_BUTTON:
        drawPlayerSelectButton(g, m, selected);
        break;
      default:
        break;
    }
  }

  /**
   * Draw the player select button.
   * @param g
   * @param x
   * @param y
   * @param selected
   */
  private void drawPlayerSelectButton(Graphics2D g, MenuItem m, boolean selected) {
    // TODO - get setting for player type and display based on that (empty, player, bot)
    g.drawImage(playerSelectIcons[playerSettings[m.data - 1]], m.x, m.y, null);
  }

  /**
   * Draw the blue field.
   * @param g
   * @param text
   * @param length
   * @param x
   * @param y
   */
  public void drawBlueField(Graphics2D g, String text, int length, int x, int y, boolean centerText) {
    final int segments = (length / PIPE_TILE_SIZE) - 2;
    final int leftOver = length % PIPE_TILE_SIZE;
    int textX = x + PIPE_TEXT_OFFSET_X;
    final int textY = y + PIPE_TEXT_OFFSET_Y;
    
    g.drawImage(blueField[LEFT_INDEX], x, y, null);
    x += PIPE_TILE_SIZE;
    for (int i = 0; i < segments; i++) {
      g.drawImage(blueField[MID_INDEX], x, y, null);
      x += PIPE_TILE_SIZE;
    }
    if (leftOver > 0) {
      g.drawImage(blueField[MID_INDEX], x, y, null);
      x += leftOver;
    }
    g.drawImage(blueField[RIGHT_INDEX], x, y, null);
    if (text != null) {
      Font f = Font.getInstance();
      if (centerText) {
        textX -= PIPE_TEXT_OFFSET_X;
        textX += ((length - f.getLargeFontSize() * text.length()) / 2);
      }
      f.drawLargeText(g, text.toCharArray(), textX, textY, null);
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
  public void drawPipeField(Graphics2D g, String text, int length, int x, int y, int selected, boolean centerText) {
    final int segments = (length / PIPE_TILE_SIZE) - 2;
    final int leftOver = length % PIPE_TILE_SIZE;
    int textX = x + PIPE_TEXT_OFFSET_X;
    final int textY = y + PIPE_TEXT_OFFSET_Y;
    
    g.drawImage(pipes[LEFT_INDEX][selected], x, y, null);
    x += PIPE_TILE_SIZE;
    for (int i = 0; i < segments; i++) {
      g.drawImage(pipes[MID_INDEX][selected], x, y, null);
      x += PIPE_TILE_SIZE;
    }
    if (leftOver > 0) {
      g.drawImage(pipes[MID_INDEX][selected], x, y, null);
      x += leftOver;
    }
    g.drawImage(pipes[RIGHT_INDEX][selected], x, y, null);
    if (text != null) {
      Font f = Font.getInstance();
      if (centerText) {
        textX -= PIPE_TEXT_OFFSET_X;
        textX += ((length - f.getLargeFontSize() * text.length()) / 2);
      }
      f.drawLargeText(g, text.toCharArray(), textX, textY, null);      
    }
  }
    
  /** 
   * Draws the player select field.
   * @param g
   * @param text
   * @param x
   * @param y
   */
  public void drawPlayerSelectField(Graphics2D g, String text, int x, int y, boolean selected) {
    Font font = Font.getInstance();
        
    final int selectedIndex = selected ? 1 : 0;
    BufferedImage leftField = selectField[LEFT_INDEX][selectedIndex];
    BufferedImage middleField = selectField[MID_INDEX][selectedIndex];
    BufferedImage rightField = selectField[RIGHT_INDEX][selectedIndex];
    
    g.drawImage(leftField, x, y, null);
    x+= leftField.getWidth();
    for (int i = 0; i < 7; i++) {
      g.drawImage(middleField, x, y, null);
      x+=middleField.getWidth();
    }
    g.drawImage(rightField, x, y, null);
    x+= rightField.getWidth();
    font.drawLargeText(g, text.toCharArray(), 128+16, y+18, null);
    
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
  }
}