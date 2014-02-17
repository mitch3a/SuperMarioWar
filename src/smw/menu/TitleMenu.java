package smw.menu;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RescaleOp;
import java.io.IOException;

import javax.imageio.ImageIO;

import smw.Game;
import smw.gfx.Font;
import smw.gfx.Palette;
import smw.ui.screen.GameFrame;

public class TitleMenu extends Menu { 
  private BufferedImage backgroundImg;
  private BufferedImage selectFieldImg; // TODO - probably don't need this big img hanging around
  
  private BufferedImage leftPipe;
  private BufferedImage middlePipe;
  private BufferedImage rightPipe;
  
  private BufferedImage leftField;
  private BufferedImage middleField;
  private BufferedImage rightField;
  
  private int selection;
  
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
      
      drawPipeField(g, "Start", 7, 120, 210, true);
      drawPipeField(g, "Go!", 0, 440, 210, false);
      drawPipeField(g, "Options", 10, 120, 323, true);
      drawPipeField(g, "Controls", 10, 120, 363, true);
      drawPipeField(g, "Exit", 10, 120, 402, true);
      
      // Read the sprite sheet for the player select menu field.
      tempImg = ImageIO.read(this.getClass().getClassLoader().getResource("menu/menu_player_select.png"));
      BufferedImage playerSelect = new BufferedImage(tempImg.getWidth(), tempImg.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
      playerSelect.getGraphics().drawImage(tempImg, 0, 0, null);
      p.implementTransparent(playerSelect);
      
      leftField = playerSelect.getSubimage(0, 0, 16, 64);
      middleField = playerSelect.getSubimage(32, 0, 16, 64);
      rightField = playerSelect.getSubimage(32 * 15 + 16, 0, 16, 64);
      
      drawPlayerSelectField(g);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  
  // TODO - maybe add an enum value for pipe type to draw!
  public void drawPipeField(Graphics2D g, String text, int length, int x, int y, boolean extra) {
    int textX = x + 24;
    int textY = y + 4;
    g.drawImage(leftPipe, x, y, null);
    x+= leftPipe.getWidth();
    for (int i = 0; i < length; i++) {
      g.drawImage(middlePipe, x, y, null);
      x += middlePipe.getWidth();
    }
    if (extra) { // TODO - this is awful, should really just be able to give a length in pixels for a field then just draw it!
      x-=16;
      g.drawImage(middlePipe, x, y, null);
      x += middlePipe.getWidth();
    } else {
      g.drawImage(middlePipe, x, y, null);
      x +=16;
    }
    
    g.drawImage(rightPipe, x, y, null);
    
    if (text != null) {
      Font.getInstance().drawLargeText(g, text, textX, textY, null);
    }
  }
  
  // TODO
  public void drawPlayerSelectField(Graphics2D g) {
    int x = 120;
    int y = 250;
    Font font = Font.getInstance();
    
    g.drawImage(leftField, x, y, null);
    x+= leftField.getWidth();
    for (int i = 0; i < 7; i++) {
      g.drawImage(middleField, x, y, null);
      x+=middleField.getWidth();
    }
    g.drawImage(rightField, x, y, null);
    x+= rightField.getWidth();
    font.drawLargeText(g, "Players", 128+16, y+18, null);
    
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
    // TODO - draw user selection?
  }

  @Override
  public void update() {
    if (setupSound) {
      Game.soundPlayer.playMenuMusic();
    }
    // TODO - update based on user selection
  }
  
  public void drawBackground(Graphics2D g, ImageObserver io){
    g.drawImage(backgroundImg, 0, 0, io);
  }

}
