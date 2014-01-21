package smw.gfx;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

//This class is a singleton because you only need one.
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
	
  public enum ColorScheme{
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
	
	byte getRed(int color){
		return (byte) ((color >> 16) & 0x000000FF);
	}
	
	byte getGreen(int color){
		return (byte) ((color >> 8) & 0x000000FF);
	}
	
	byte getBlue(int color){
		return (byte) ((color) & 0x000000FF);
	}
	
	public void loadPalette(){
	  int[] alphaInt = {0, 0, 0, 0};
	  colorMap.put(0xffff00ff, alphaInt);
		try{
			BufferedImage imageBuffer = ImageIO.read(this.getClass().getClassLoader().getResource("sprites/palette.bmp"));
			
			final int width = imageBuffer.getWidth();

		  //These values are strictly based on what I saw in the palette image -mk
		  for(int i = 0 ; i < width ; ++i){
		    int baseColor = imageBuffer.getRGB(i, 0);   //Key
		    int[] values = {imageBuffer.getRGB(i,  1),  //Red
		                    imageBuffer.getRGB(i, 10),  //Green
		                    imageBuffer.getRGB(i, 19),  //Yellow
		                    imageBuffer.getRGB(i, 28)}; //Blue
		    colorMap.put(baseColor, values);
		  }
		} catch (IOException e) {
      e.printStackTrace();  // TODO UH OH...error handling...
    }
	}
	
	/** This also removes megenta (into alpha zero) **/
	public void colorSprite(ColorScheme color, BufferedImage image){
	  int colorIndex = color.index;
	  
	  for(int w = 0 ; w < image.getWidth() ; ++w){
	    for(int h = 0 ; h < image.getHeight(); ++h){
	      int currentColor = image.getRGB(w, h);
	      if(colorMap.containsKey(currentColor)){
	        image.setRGB(w, h, colorMap.get(currentColor)[colorIndex]);
	      }
	    }
	  }
	}
}