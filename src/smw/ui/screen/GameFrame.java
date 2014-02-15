package smw.ui.screen;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import smw.Game;
import smw.entity.Player;
import smw.gfx.Scoreboard;
import smw.world.World;

public class GameFrame extends Canvas{

  private static final long serialVersionUID = 1L;
	
	// TODO - RPG - should figure out how to setup resolution options w/ scaling...
	public static int res_width = 640;
	public static int res_height = 480;
	public static double scaleFactorWidth = 1.0f;
	public static double scaleFactorHeight = 1.0f;
	public static int bumpFactor = 0;
	private boolean bumpUp = false;
	private boolean bumpDown = false;
	
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
    frame.setResizable(false);
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
    //g2d.scale(scaleFactorWidth, scaleFactorHeight);
    
    // TODO - RPG - If we have a menu then only draw that! (Although this won't work for pause menu!)
    if (game.menu != null) {
      game.menu.draw(g2d, this);
    } else {
      world.drawBackground(g2d, this);
      boolean drewToBack = false;
      
      //Draw the warping guys behind everything
      if (players != null && players.length > 0) {
        for(Player p : players){
          if (p != null) {
            drewToBack |= p.drawToBack(g2d, this);
          }
        }
      }

      if(drewToBack){
        //Only need to bother if there are players
        //behind anything, else they are already on
        //the background
        world.drawLayer0(g2d, this);
        world.drawLayer1(g2d, this);
      }
      
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
  
    
  // TODO - this is currently broken, probably only want to change screen size in settings anyway...
  void resetScalingFactors(){
    //TODO mk this is temporary. there has to be a better way of doing this
    //Insets insets = frame.getInsets();
    //scaleFactorWidth =  ((double)(getWidth() - insets.left - insets.right))/res_width;
    //scaleFactorHeight = ((double)(getHeight()- insets.top  - insets.bottom))/res_height;
    scaleFactorWidth =  (double)(getWidth())/res_width;
    scaleFactorHeight = (double)(getHeight())/res_height;
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
