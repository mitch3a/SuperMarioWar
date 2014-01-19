package smw.gfx;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/* Stole this from the C++ stuff... didn't quite finish
public class Palette {

	public Palette(){
		
	}
	
  //[numplayers][colorscheme][colorcomponents][numcolors]
	byte[][][][] colorschemes;
	//[colorcomponent][code]
	byte[][]   colorCodes;
	final static int NUM_SCHEMES = 9;
	final static int redIndex = 0;
	final static int greenIndex = 1;
	final static int blueIndex = 2;
	
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
		try{
			BufferedImage imageBuffer = ImageIO.read(this.getClass().getClassLoader().getResource("sprites/palette.bmp"));
			
			final int width = imageBuffer.getWidth();
		  final int height = imageBuffer.getHeight();
		  int[] pixels = new int[width * height];
		  imageBuffer.getRGB(0, 0, width, height, pixels, 0, width);

		  int numColors = pixels.length;
		  
		  colorCodes = new byte[3][width];
		  colorschemes = new byte[4][NUM_SCHEMES][3][width];
			
		  //The first row contains the color codes
			for(int k = 0; k < width; k++){
				int color = pixels[k];
				colorCodes[redIndex][k]   = getRed(color);
				colorCodes[greenIndex][k] = getGreen(color);
				colorCodes[blueIndex][k]  = getBlue(color);
			}
			
			int pixelCounter = width; //Skip the first row because it's the colorCode
			//The rest is the color scheme
			for(int i = 0; i < 4; ++i)
			{
				for(int j = 0; j < NUM_SCHEMES; ++j)
				{
					for(int m = 0; m < width; ++m)
					{
						int color = pixels[pixelCounter++];
						colorschemes[i][j][redIndex][m]   = getRed(color);
						colorschemes[i][j][greenIndex][m] = getGreen(color);
						colorschemes[i][j][blueIndex][m]  = getBlue(color);
					}
				}
			}	  
		} catch (IOException e) {
      e.printStackTrace();  // TODO UH OH...error handling...
    }
	}
	

	void createSkin(BufferedImage[][] image, Uint8 r, Uint8 g, Uint8 b, short colorScheme, bool expand)
	{
		for(int j = 0; j < image.length; j++)
		{
			for(int i = 0; i < image[0].length ; i++)
			{
				BufferedImage imageBuffer = image[j][i];
		
				final int width = imageBuffer.getWidth();
			  final int height = imageBuffer.getHeight();
			  int[] pixels = new int[width * height];
			  imageBuffer.getRGB(0, 0, width, height, pixels, 0, width);
			  
			  for(int currentPixel = 0 ; currentPixel < pixels.length ; ++currentPixel){
			  	int color = pixels[currentPixel];
			  	byte red   = getRed(color);
					byte green = getGreen(color);
					byte blue  = getBlue(color);
					
					bool fFoundColor = false;
					for(int m = 0; m < numcolors; m++){
						//TODO mk  there better be a reason why color codes is not just an int...
						if(red == colorCodes[redIndex][m] && green == colorCodes[greenIndex][m] && blue == colorCodes[blueIndex][m])
						{
							for(int k = 0; k < NUM_SCHEMES; k++)
							{
								temppixels[tempcounter + k * 96 + reverseoffset + iRedOffset] = colorschemes[colorScheme][k][0][m];
								temppixels[tempcounter + k * 96 + reverseoffset + iGreenOffset] = colorschemes[colorScheme][k][1][m];
								temppixels[tempcounter + k * 96 + reverseoffset + iBlueOffset] = colorschemes[colorScheme][k][2][m];
							}
							
							fFoundColor = true;
							break;
						}
					}
			  }

				if(!fFoundColor)
				{
					for(int k = 0; k < loops; k++)
					{
						temppixels[tempcounter + k * 96 + reverseoffset + iRedOffset] = iColorByte1;
						temppixels[tempcounter + k * 96 + reverseoffset + iGreenOffset] = iColorByte2;
						temppixels[tempcounter + k * 96 + reverseoffset + iBlueOffset] = iColorByte3;
					}
				}
	
				skincounter += 3;
				tempcounter += 3;
			}
	
			skincounter += 480 + skin->pitch - (skin->w * 3);
			tempcounter += 96 * (loops - 1) + temp->pitch - (temp->w * 3);
		}

	
		return final;
	}
}
*/
