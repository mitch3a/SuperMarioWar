package smw.ui.screen;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import smw.entity.Player;
import smw.gfx.Scoreboard;
import smw.world.World;

public class GameFrame extends JFrame{

  private static final long serialVersionUID = 1L;

  //TODO this should be in a common area
	public static final String Title = "Super Mario War";
	
	// TODO - RPG - should figure out how to setup resolution options w/ scaling...
	public static int res_width = 640;
	public static int res_height = 480;
	public static double scaleFactorWidth = 1.5f;
	public static double scaleFactorHeight = 1.5f;
	public static int bumpFactor = 0;
	boolean bumpUp = false;
	boolean bumpDown = false;
	
	//TODO this is REALLY hacky but i just wanted to get the stupid thing to work
	Player[] players;
	Scoreboard sB;
	World world;
	
	public GameFrame(Player[] players, World world){
	  add(new GamePanel());
	  
		//This will get rid of all borders but locks up the keyboard/mouse so you are hosed setUndecorated(true);
		setTitle(Title);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize((int)(res_width*scaleFactorWidth), (int)(res_height*scaleFactorHeight));
    setLocationRelativeTo(null);
    setVisible(true);
    setResizable(true);
    this.setContentPane(new GamePanel());
    addComponentListener(new ComponentAdapter() {
        public void componentResized(ComponentEvent e) {
            resetScalingFactors();
        }
    });
    
    this.players = players;
    this.world = world;
    sB = new Scoreboard(this.players);
	}

  @Override
  public void paint(Graphics g) {
    BufferStrategy bs = getBufferStrategy();
    if (bs == null) {
      createBufferStrategy(3);
      requestFocus();
      return;
    }

    updateBumpFactor();

    try {
      g = bs.getDrawGraphics();
      g.fillRect(0, 0, getWidth(), getHeight());
      
      Graphics2D g2d = (Graphics2D)g;
      Insets insets = getInsets();
      g2d.translate(insets.left, bumpFactor + insets.top);
      g2d.scale(scaleFactorWidth, scaleFactorHeight);
      
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
    } finally {
      // Free up graphics.
      g.dispose();
    }
    // Display contents of buffer.
    bs.show();   
  }
  
  void resetScalingFactors(){
    //TODO mk this is temporary. there has to be a better way of doing this
    Insets insets = getInsets();
    scaleFactorWidth =  ((double)(getWidth() - insets.left - insets.right))/res_width;
    scaleFactorHeight = ((double)(getHeight()- insets.top  - insets.bottom))/res_height;
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
}
