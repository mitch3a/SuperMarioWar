package smw.ui.screen;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import smw.Game;
import smw.entity.Player;
import smw.gfx.Scoreboard;
import smw.gfx.Sprite;
import smw.settings.Debug;
import smw.world.World;

public class GameFrame extends Canvas{

  private static final long serialVersionUID = 1L;

	// TODO - RPG - should figure out how to setup resolution options w/ scaling...
	public static int res_width = 640;
	public static int res_height = 480;
	public static double scaleFactorWidth = 1.5f;
	public static double scaleFactorHeight = 1.5f;
	public static int bumpFactor = 0;
	private boolean bumpUp = false;
	private boolean bumpDown = false;
	
	//For doing zoom/clipping
  private static final int CLIP_WINDOW_HEIGHT = 320; //if keeping aspect ratio, pick something doesn't leave a fractional width
  private static final float HEIGHT_TO_WIDTH_FACTOR = Debug.CLIP_SHAPE_KEEP_ASPECT_RATIO ? (res_width/res_height) : 1.0f;
  private static final int CLIP_WINDOW_WIDTH = (int)(CLIP_WINDOW_HEIGHT*HEIGHT_TO_WIDTH_FACTOR);
  private static final int CLIP_WINDOW_SHIFT_X = CLIP_WINDOW_WIDTH/2 - Sprite.IMAGE_WIDTH/2;
  private static final int CLIP_WINDOW_SHIFT_Y = CLIP_WINDOW_HEIGHT/2 - Sprite.IMAGE_HEIGHT/2;
  private static final int CLIP_MAX_X = res_width - CLIP_WINDOW_WIDTH;
  private static final int CLIP_MAX_Y = res_height - CLIP_WINDOW_HEIGHT;
  
  private static RectangularShape clipShape = Debug.CLIP_SHAPE_RECTANGLE ? new Rectangle(0, 0, CLIP_WINDOW_WIDTH, CLIP_WINDOW_HEIGHT) :
                                                                           new Ellipse2D.Float(0, 0, CLIP_WINDOW_HEIGHT, CLIP_WINDOW_HEIGHT);
	
	private JFrame frame;
	private BufferStrategy bs;
	
	
	private Player[] players;
	private Scoreboard sB;
	private World world;
	private Game game;
	
	/**
	 * Creates the frame to display the game.
	 * @param players
	 * @param world
	 * @param game
	 */
	public GameFrame(Player[] players, World world, Game game){	  
	  this.game = game;
	  this.players = players;
    this.world = world;
    this.sB = new Scoreboard(this.players);
    
    this.setSize(new Dimension((int)(res_width*scaleFactorWidth), (int)(res_height*scaleFactorHeight)));
    
    // Setup the window.
    frame = new JFrame("Super Mario War");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize((int)(res_width*scaleFactorWidth), (int)(res_height*scaleFactorHeight));
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    frame.setResizable(true);
    frame.add(this, BorderLayout.CENTER);
    frame.pack();
    
    // If the window is closed perform any needed shutdown cleanup.
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent we) { 
        Game.shutdown();
        System.exit(0);
      }
    });
    
    // TODO - this is broken, I think we don't want to be able to resize the window anyway.
    frame.addComponentListener(new ComponentAdapter() {
        public void componentResized(ComponentEvent e) {
            resetScalingFactors();
        }
    });
    
    resetScalingFactors();
    
    // Now that frame is setup, create buffer strategy.
    this.createBufferStrategy(3);
    bs = getBufferStrategy();
	}
  
	/** Main drawing method. */
  public void render() {    
    updateBumpFactor();
    
    Graphics2D g2d = (Graphics2D)bs.getDrawGraphics();
    g2d.fillRect(0, 0, getWidth(), getHeight());

    // TODO - put this in an if statement if bump is needed?
    g2d.translate(0, bumpFactor);  
    g2d.scale(scaleFactorWidth, scaleFactorHeight);
    
    // TODO - RPG - If we have a menu then only draw that! (Although this won't work for pause menu!)
    if (game.menu != null) {
      game.menu.draw(g2d, this);
    } else {
      
      if(Debug.CLIP_MODE){
        int x = (int) Math.min(Math.max(players[0].x - CLIP_WINDOW_SHIFT_X,  0), CLIP_MAX_X);
        int y = (int) Math.min(Math.max(players[0].y - CLIP_WINDOW_SHIFT_Y,  0), CLIP_MAX_Y);
        
        if(!Debug.CLIP_STRETCH){
          clipShape.setFrame(x, y, CLIP_WINDOW_WIDTH, CLIP_WINDOW_HEIGHT);
          g2d.clip(clipShape);
        }
        else{
          g2d.clip(clipShape);
          g2d.translate(-x, -y);
        }
      }
      
      world.drawBackground(g2d, this);

      if (players != null && players.length > 0) {
        for(Player p : players){
          if (p != null) {
            p.draw(g2d, this);
          }
        }
      }
      
      world.drawLayer2(g2d, this);
      world.drawLayer3(g2d, this);      
      sB.draw(g2d, this);
    }
    g2d.dispose();
    // Display contents of buffer.
    bs.show();
    Toolkit.getDefaultToolkit().sync();
  }
  
    
  // TODO - probably only want to change screen size in settings anyway...
  // mk - I disagree. I kind of hate how the regular game will only let you 
  //      go full screen or tiny screen. I think it needs work like maintaining
  //      aspect ratio, but at the very least should be in there for now because
  //      it can help for debuggin
  void resetScalingFactors(){
    if(Debug.CLIP_MODE && Debug.CLIP_STRETCH){
      scaleFactorWidth =  (double)(getWidth())/CLIP_WINDOW_WIDTH;
      scaleFactorHeight = (double)(getHeight())/CLIP_WINDOW_HEIGHT;
    }
    else{
      scaleFactorWidth =  (double)(getWidth())/res_width;
      scaleFactorHeight = (double)(getHeight())/res_height;
    }
  }
  
  /********************************************
   * This bump factor stuff is to make the screen
   * bump when a player gets stomped.
   ********************************************/
  public void bump(){
    if(!bumpUp && !bumpDown){
      bumpUp = true;
    }
  }
  
  void updateBumpFactor(){
    if(bumpDown){
      bumpFactor--;
      if(bumpFactor == 0){
        bumpDown = false;
      }
    }
    
    if(bumpUp){
      bumpFactor++;
      if(bumpFactor == 5){
        bumpDown = true;
        bumpUp = false;
      }
    }
  }
  
  /**
   * Returns the game JFrame.
   * @return The main JFrame.
   */
  public JFrame getGameFrame() {
    return this.frame;
  }
}
