package smw.gfx;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;

import javax.imageio.ImageIO;

import smw.Drawable;
import smw.Updatable;
import smw.ui.screen.GameFrame;

public class CountDownText implements Drawable, Updatable {
  private static final int XL_SIZE = 64;
  private static final int LG_SIZE = 48;
  private static final int MED_SIZE = 32;
  private static final int SM_SIZE = 16;
  private static final int TIME_PER_FRAME_MS = 150;
  
  /** The text images indexed by text size and text type. */
  private BufferedImage[][] textImages = new BufferedImage[4][4];
  
  private int animationTime_ms = 0;
  
  private int sizeIndex = 0;
  private int textIndex = 0;
  
  boolean remove = false;

  public CountDownText() {
    try {
      BufferedImage imageBuffer = ImageIO.read(getClass().getClassLoader().getResource("gfx/packs/Classic/menu/game_countdown_numbers.png"));
      BufferedImage convertedImg = new BufferedImage(imageBuffer.getWidth(), imageBuffer.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
      convertedImg.getGraphics().drawImage(imageBuffer, 0, 0, null);
      Palette p = Palette.getInstance();
      p.implementTransparent(convertedImg);
      
      for (int i = 0; i < 4; i++) {
        textImages[3][i] = convertedImg.getSubimage(i * XL_SIZE, 0, XL_SIZE, XL_SIZE);
        textImages[2][i] = convertedImg.getSubimage(i * LG_SIZE, XL_SIZE, LG_SIZE, LG_SIZE);
        textImages[0][i] = convertedImg.getSubimage(i * SM_SIZE, XL_SIZE + LG_SIZE, SM_SIZE, SM_SIZE);
      }
      
      // Medium sized numbers are off in their own area of the image file.
      int x_offset = LG_SIZE * 4;
      int y_offset = XL_SIZE;
      textImages[1][0] = convertedImg.getSubimage(x_offset, y_offset, MED_SIZE, MED_SIZE); // 3
      textImages[1][1] = convertedImg.getSubimage(x_offset + MED_SIZE, y_offset, MED_SIZE, MED_SIZE); // 2
      textImages[1][2] = convertedImg.getSubimage(x_offset, y_offset + MED_SIZE, MED_SIZE, MED_SIZE); // 1
      textImages[1][3] = convertedImg.getSubimage(x_offset + MED_SIZE, y_offset + MED_SIZE, MED_SIZE, MED_SIZE); // GO
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void update(float timeDif_ms) {
    // TODO - might need a delay or a way to know the world is loaded and the game is started
    animationTime_ms += timeDif_ms;
    if (animationTime_ms >= TIME_PER_FRAME_MS) {
      sizeIndex++;
      if (sizeIndex >= 4) {
        sizeIndex = 0;
        textIndex++;
        if (textIndex >= 4) {
          remove = true;
        }
      }
      animationTime_ms = 0;
    }
  }

  @Override
  public void draw(Graphics2D g, ImageObserver io) {
    if (textIndex < 4 && sizeIndex < 4) {
      final int x = (GameFrame.res_width - textImages[sizeIndex][textIndex].getWidth()) / 2; 
      final int y = (GameFrame.res_height - textImages[sizeIndex][textIndex].getHeight()) / 2;
      g.drawImage(textImages[sizeIndex][textIndex], x, y, io);
    }
  }

  @Override
  public boolean shouldBeRemoved() {
    return remove;
  }
}
