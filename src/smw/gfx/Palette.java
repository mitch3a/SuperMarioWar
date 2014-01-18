package smw.gfx;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Palette {

	public Palette(){
		
	}
	
	byte[][]   colorCodes;
	byte[][][] colorschemes;
	final int NUM_SCHEMES = 3;
	
	public void loadPalette(){
		try{
			BufferedImage imageBuffer = ImageIO.read(this.getClass().getClassLoader().getResource("sprites/palette.bmp"));
			
			final int width = imageBuffer.getWidth();
		  final int height = imageBuffer.getHeight();
		  int[] pixels = new int[width * height];
		  imageBuffer.getRGB(0, 0, width, height, pixels, 0, width);
		  
		  int numColors = pixels.length;
		  
		  colorCodes = new byte[3][numColors];
		  colorschemes = new byte[4][NUM_SCHEMES][3];
		  
		  for(int k = 0; k < 3 ; ++k){
		  	for(int i = 0; i < 4; ++i)
					for(int j = 0; j < NUM_SCHEMES; ++j)
						colorschemes[i][j][k] = new Uint8[numcolors];;
		  }
		  
		} catch (IOException e) {
      e.printStackTrace();  // TODO UH OH...error handling...
    }
	}
		/*
		for(int k = 0; k < 3; k++)
		{
			colorcodes[k] = new Uint8[numcolors];

			for(int i = 0; i < 4; i++)
				for(int j = 0; j < NUM_SCHEMES; j++)
					colorschemes[i][j][k] = new Uint8[numcolors];
		}

		if(SDL_MUSTLOCK(palette))
			SDL_LockSurface(palette);

		int counter = 0;

		Uint8 * pixels = (Uint8*)palette->pixels;

		short iRedIndex = palette->format->Rshift >> 3;
		short iGreenIndex = palette->format->Gshift >> 3;
		short iBlueIndex = palette->format->Bshift >> 3;

		for(int k = 0; k < numcolors; k++)
		{
			colorcodes[iRedIndex][k] = pixels[counter++];
			colorcodes[iGreenIndex][k] = pixels[counter++];
			colorcodes[iBlueIndex][k] = pixels[counter++];
		}

		counter += palette->pitch - palette->w * 3;

		for(int i = 0; i < 4; i++)
		{
			for(int j = 0; j < NUM_SCHEMES; j++)
			{
				for(int m = 0; m < numcolors; m++)
				{
					colorschemes[i][j][iRedIndex][m] = pixels[counter++];
					colorschemes[i][j][iGreenIndex][m] = pixels[counter++];
					colorschemes[i][j][iBlueIndex][m] = pixels[counter++];
				}

				counter += palette->pitch - palette->w * 3;
			}
		}
		
		return true;
	}*/
}
