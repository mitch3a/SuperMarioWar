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
  private BufferedImage selectFieldImg;
  
  BufferedImage leftPipe;
  BufferedImage middlePipe;
  BufferedImage rightPipe;
  
  private int selection;
  
  
    
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
      
      // Read the sprite sheet for the menu fields.
      tempImg = ImageIO.read(this.getClass().getClassLoader().getResource("menu/menu_selectfield.png"));
      selectFieldImg = new BufferedImage(tempImg.getWidth(), tempImg.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
      selectFieldImg.getGraphics().drawImage(tempImg, 0, 0, null);
      p.implementTransparent(selectFieldImg);
      
      leftPipe = selectFieldImg.getSubimage(0, 0, 32, 32);
      middlePipe = selectFieldImg.getSubimage(32, 0, 32, 32);
      rightPipe = selectFieldImg.getSubimage(32 * 15, 0, 32, 32);

      /*
      drawPipeField(g, "Start", 8, 137, 240);
      drawPipeField(g, "Go!", 1, 457, 240);
      
      drawPipeField(g, "Options", 10, 137, 353);
      drawPipeField(g, "Controls", 10, 137, 393);
      drawPipeField(g, "Exit", 10, 137, 432);
      */
      drawPipeField(g, "Start", 8, 137 - 7, 240 - 30);
      drawPipeField(g, "Go!", 1, 457 - 7, 240 - 30);
      
      drawPipeField(g, "Options", 10, 137 - 7, 353 - 30);
      drawPipeField(g, "Controls", 10, 137 - 7, 393 - 30);
      drawPipeField(g, "Exit", 10, 137 - 7, 432 - 30);
      
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  
  // TODO - maybe add an enum value for pipe type to draw!
  public void drawPipeField(Graphics2D g, String text, int length, int x, int y) {
    int textX = x + 24;
    int textY = y + 4;
    g.drawImage(leftPipe, x, y, null);
    x+= leftPipe.getWidth();
    for (int i = 0; i < length; i++) {
      g.drawImage(middlePipe, x + (i * middlePipe.getWidth()), y, null);
    }
    g.drawImage(rightPipe, x + (length * middlePipe.getWidth()), y, null);
    
    if (text != null) {
      Font font = Font.getInstance();
      font.drawLargeText(g, text, textX, textY, null);
    }
  }
  
  @Override
  public void draw(Graphics2D g, ImageObserver io) {
    drawBackground(g, io);
    // TODO - draw user selection?
  }

  @Override
  public void update() {
    //Game.soundPlayer.playMenuMusic();
    // TODO - update based on user selection
  }
  
  public void drawBackground(Graphics2D g, ImageObserver io){
    g.drawImage(backgroundImg, 0, 0, io);
  }

}
