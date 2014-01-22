package smw.level;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;

import javax.imageio.ImageIO;

import smw.ui.screen.GameFrame;

public class Level {
  /*
   * TODO - a level will be made up of tiles In the simplest sense this could
   * be a 2D array of tiles There could be "layers" like in SNES for graphics
   * This could be accomplished by having an array of 2D tile arrays
   * 
   * Need to provide a way to get tile at x,y coord. - During the game logic,
   * the game would check what type of tile the player is touching
   */
  
  public static final int TILE_SIZE = 32;
  public final int WIDTH = GameFrame.res_width / TILE_SIZE;
  public final int HEIGHT = GameFrame.res_height / TILE_SIZE;
  
  private Tile[][] tiles = new Tile[WIDTH][HEIGHT];
  
  // TODO - this will eventually be init by a map file
  public void init() {    
    BufferedImage testTileImg = null;
    try {
      testTileImg = ImageIO.read(this.getClass().getClassLoader().getResource("test_tile.png"));
    } catch (IOException e) {
      e.printStackTrace();
    } 
    
    // My cheese ball statically populated "map"
    // It has a floor and a block placed near the middle, oh boy!
    for (int i = 0; i < WIDTH; i++) {
      for (int j = 0; j < HEIGHT; j++) {
          tiles[i][j] = new Tile(i * WIDTH, i * HEIGHT);
          if (j == HEIGHT - 1)
          {
            tiles[i][j].setImg(testTileImg);
            tiles[i][j].setTileType(1);
          }
      }
    }
    tiles[WIDTH/2][HEIGHT - 2].setImg(testTileImg);
    tiles[WIDTH/2][HEIGHT - 2].setTileType(1);    
  }
  
  /** Gets tile type at provided pixel coordinates. */
  public int getTileTypeAtPx(int x, int y) {
    return tiles[x / TILE_SIZE][y / TILE_SIZE].getTileType();
  }
  
  /** Gets tile type at provided tile coordinates. */
  public int getTileTypeAtTile(int x, int y) {
    return tiles[x][y].getTileType();
  }
  
  public void update() {
    // TODO - not sure what would get updated here, interactive junk in the level I guess?
  }
  
  public void draw(Graphics2D g, ImageObserver io) {
    
    for (int i = 0; i < WIDTH; i++) {
      for (int j = 0; j < HEIGHT; j++) {
        BufferedImage img = tiles[i][j].getImg();
        if (img != null) {
          g.drawImage(img, i * TILE_SIZE, j * TILE_SIZE, io);
        }
      }
    }
  }
  
}
