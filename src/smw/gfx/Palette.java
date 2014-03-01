package smw.gfx;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

/******************************************************
 * This class is used to process Sprites. This entails 
 * implementing the team color and replacing "magenta" 
 * with transparency.
 *****************************************************/
public class Palette {
  private static Palette instance;

  static {
    instance = new Palette();
  }

  private Palette() { 
    loadPalette();
  }    

  public static Palette getInstance() {
    return instance;
  }
	
  public enum ColorScheme {
    RED(0), GREEN(1), YELLOW(2), BLUE(3);
    
    public final int index;
    private ColorScheme(int index) {
      this.index = index;
    }
  }
  
	HashMap<Integer, int[]> colorMap = new HashMap<Integer, int[]>();
	
	final static int NUM_SCHEMES = 9;
	final static int redIndex = 0;
	final static int greenIndex = 1;
	final static int yellowIndex = 2;
	final static int blueIndex = 3;
	
	byte getRed(int color) {
		return (byte) ((color >> 16) & 0x000000FF);
	}
	
	byte getGreen(int color) {
		return (byte) ((color >> 8) & 0x000000FF);
	}
	
	byte getBlue(int color) {
		return (byte) ((color) & 0x000000FF);
	}
	
	public void loadPalette() {
	  int[] alphaInt = {0x00000000, Color.TRANSLUCENT, 0x00000000, 0x00000000};
	  colorMap.put(0xffff00ff, alphaInt);
		try{
			BufferedImage imageBuffer = ImageIO.read(this.getClass().getClassLoader().getResource("palette.png"));
			final int width = imageBuffer.getWidth();

		  //These values are strictly based on what I saw in the palette image -mk
		  for(int i = 0 ; i < width ; ++i) {
		    int baseColor = imageBuffer.getRGB(i, 0);   //Key
		    int[] values = {imageBuffer.getRGB(i,  1),  //Red
		                    imageBuffer.getRGB(i, 10),  //Green
		                    imageBuffer.getRGB(i, 19),  //Yellow
		                    imageBuffer.getRGB(i, 28)}; //Blue
		    colorMap.put(baseColor, values);
		  }
		} catch (IOException e) {
      e.printStackTrace();
    }
	}

	public void applyAlpha(BufferedImage image, int alpha) {
	  for (int w = 0 ; w < image.getWidth() ; ++w) {
      for (int h = 0 ; h < image.getHeight(); ++h) {
        int currentColor = image.getRGB(w, h);
        if (currentColor != Color.TRANSLUCENT) {
          currentColor = currentColor << 8;
          currentColor = currentColor >> 8;
          currentColor += (alpha << 24);
          image.setRGB(w, h, currentColor);
        }
      }
    }
	}

	/**
	 * Colors the provided sprite image based on the provided color scheme.
	 * This also converts megenta to transparent (alpha channel).
	 * @param color The color scheme.
	 * @param image The sprite image.
	 */
	public void colorSprite(ColorScheme color, BufferedImage image) {	  
	  for (int w = 0 ; w < image.getWidth() ; ++w) {
	    for (int h = 0 ; h < image.getHeight(); ++h) {
	      int currentColor = image.getRGB(w, h);
	      if (colorMap.containsKey(currentColor) ){
	        image.setRGB(w, h, colorMap.get(currentColor)[color.index]);
	      }
	      
	      if (image.getRGB(w, h) == 0xffff00ff) {
          image.setRGB(w, h, Color.TRANSLUCENT);
        }
	    }
	  }
	}
	
	 public void implementTransparent(BufferedImage image, int colorToRemove) {
    for (int w = 0 ; w < image.getWidth() ; ++w) {
      for (int h = 0 ; h < image.getHeight(); ++h) {
        if (image.getRGB(w, h) == colorToRemove) {
          image.setRGB(w, h, Color.TRANSLUCENT);
        }
      } 
    } 
	}
	 
	public void implementTransparent(BufferedImage image) {
	  // 0xFFFF00FF
	  implementTransparent(image, 0xffff00ff);
  }
	
	public void convertAllFilesToPNG() {
	  URL url = this.getClass().getClassLoader().getResource("sprites/");
	  File folder = new File(url.getFile());
	  File[] listOfFiles = folder.listFiles();
	  for (File f : listOfFiles) {
	    convertBitmapToPNG(f.getName());
	  }
	}
	
	//This outputs to bin... might want to just hard code the output directory...
	public void convertBitmapToPNG(String fileName) {
	  try {
	    URL url = this.getClass().getClassLoader().getResource("sprites/" + fileName);
	    String[] newFileName = fileName.split("\\.");
	    BufferedImage imageBuffer = ImageIO.read(url);
	    URL newUrl = this.getClass().getClassLoader().getResource("sprites/");
	    String newFilePath = newUrl.getFile();
	    File outputfile = new File(newFilePath + newFileName[0] + ".png");
	    if (!outputfile.exists()) {
	      outputfile.mkdirs();
	      ImageIO.write(imageBuffer, "png", outputfile);
	    }
    } catch (IOException e) {
      e.printStackTrace();
    }
	}
	
}
