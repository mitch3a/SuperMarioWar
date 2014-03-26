package smw.ui.screen;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import smw.Game;
import smw.entity.Player;
import smw.gfx.Scoreboard;
import smw.gfx.Sprite;
import smw.settings.Debug;
import smw.settings.GraphicsSettings.ScreenSize;
import smw.settings.Settings;
import smw.world.World;

public class GameFrame extends Canvas{

  private static final long serialVersionUID = 1L;

	// TODO - RPG - should figure out how to setup resolution options w/ scaling...
	public static int res_width = 640;
	public static int res_height = 480;
	public static double scaleFactorWidth = 1.5f;
	public static double scaleFactorHeight = 1.5f;
	public static double scaleFactorScoreboardWidth = 1.5f;
	public static double scaleFactorScoreboardHeight = 1.5f;
	public static int bumpFactor = 0;
	private boolean bumpUp = false;
	private boolean bumpDown = false;
	
	//For doing zoom/clipping
  private static final int CLIP_WINDOW_HEIGHT = 160; //if keeping aspect ratio, pick something doesn't leave a fractional width
  private static final float HEIGHT_TO_WIDTH_FACTOR = Debug.CLIP_SHAPE_KEEP_ASPECT_RATIO ? ((float)(res_width)/res_height) : 1.0f;
  private static final int CLIP_WINDOW_WIDTH = (int)(CLIP_WINDOW_HEIGHT*HEIGHT_TO_WIDTH_FACTOR);
  private static final int CLIP_WINDOW_SHIFT_X = CLIP_WINDOW_WIDTH/2 - Sprite.IMAGE_WIDTH/2;
  private static final int CLIP_WINDOW_SHIFT_Y = CLIP_WINDOW_HEIGHT/2 - Sprite.IMAGE_HEIGHT/2;
  private static final int CLIP_MAX_X = res_width - CLIP_WINDOW_WIDTH;
  private static final int CLIP_MAX_Y = res_height - CLIP_WINDOW_HEIGHT;
  
  private static RectangularShape clipShape = Debug.CLIP_SHAPE_RECTANGLE ? new Rectangle(0, 0, CLIP_WINDOW_WIDTH, CLIP_WINDOW_HEIGHT) :
                                                                           new Ellipse2D.Float(0, 0, CLIP_WINDOW_WIDTH, CLIP_WINDOW_HEIGHT);
	
	private JFrame frame;
	private BufferStrategy bs;
	private final Area area = new Area();
	
	private Player[] players;
	private Scoreboard scoreboard;
	private World world;
	private Game game;

	private int tempX, tempY;
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
    this.scoreboard = new Scoreboard(this.players);
    
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
    
    Settings settings = Settings.getInstance();
    if(settings.getGraphics().getScreenSize() == ScreenSize.fullScreen){
      frame.setExtendedState(Frame.MAXIMIZED_BOTH);
      frame.addComponentListener(new ComponentAdapter() {
        public void componentResized(ComponentEvent e) {
        
        }
       
        public void componentMoved(ComponentEvent e) {
          frame.setLocation(0,0);
        }
      });
	  }
    else{
      frame.addComponentListener(new ComponentAdapter() {
        public void componentResized(ComponentEvent e) {
          resetScalingFactors();
        }
      });
    }
    
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
    if (Game.menu != null) {
      Game.menu.draw(g2d, this);
    } else {
      
      if(Debug.CLIP_MODE && (Scoreboard.winningPlayer == null || Debug.CLIP_ZOOM_STRETCH)){
        if(!Debug.CLIP_ZOOM_STRETCH){
          area.reset();
          
          for(Player p : players){
            if(!p.isOut()){
              tempX = (int) Math.min(Math.max(p.x - CLIP_WINDOW_SHIFT_X,  0), CLIP_MAX_X);
              tempY = (int) Math.min(Math.max(p.y - CLIP_WINDOW_SHIFT_Y,  0), CLIP_MAX_Y);
              
              clipShape.setFrame(tempX, tempY, CLIP_WINDOW_WIDTH, CLIP_WINDOW_HEIGHT);
              area.add(new Area(clipShape));
            }
          }
          
          g2d.setClip(area);
        }
        else{
          tempX = (int) Math.min(Math.max(players[0].x - CLIP_WINDOW_SHIFT_X,  0), CLIP_MAX_X);
          tempY = (int) Math.min(Math.max(players[0].y - CLIP_WINDOW_SHIFT_Y,  0), CLIP_MAX_Y);
          g2d.clip(clipShape);
          g2d.translate(-tempX, -tempY);
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
      
      Graphics2D g2d2 = (Graphics2D)bs.getDrawGraphics();
      g2d2.scale(scaleFactorScoreboardWidth, scaleFactorScoreboardHeight);
      scoreboard.draw(g2d2, this);
      g2d2.dispose();
    }
    g2d.dispose();
    // Display contents of buffer.
    bs.show();
    Toolkit.getDefaultToolkit().sync();
  }
  
  synchronized void resetScalingFactors(){
    double width = frame.getWidth();
    double height = frame.getHeight();
    
    Settings settings = Settings.getInstance();
    if(false){//TODO!settings.isStretchMode()){
      double desiredRatio = (((double)res_width)/res_height);
      double actualRatio = (width/height);
      
      if(actualRatio == desiredRatio){
        return;
      }
      
      if( actualRatio > desiredRatio){
        //Too wide so tone back width
        height -= (height % 3);
        width = height*desiredRatio;
      }
      else{
        //Too tall so tone back height
        
        width -= (width % 4);
        height = width/desiredRatio;
      }
      
      //TODO not sure wtf is wrong with this mk
      //frame.setSize((int)width, (int)height);
    }
    
    if(Debug.CLIP_MODE && Debug.CLIP_ZOOM_STRETCH){
      scaleFactorWidth =  width/CLIP_WINDOW_WIDTH;
      scaleFactorHeight = height/CLIP_WINDOW_HEIGHT;
      scaleFactorScoreboardWidth = width/res_width;
      scaleFactorScoreboardHeight = height/res_height;
    }
    else{
      scaleFactorWidth =  width/res_width;
      scaleFactorHeight = height/res_height;
      scaleFactorScoreboardWidth = scaleFactorWidth;
      scaleFactorScoreboardHeight = scaleFactorHeight;
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
