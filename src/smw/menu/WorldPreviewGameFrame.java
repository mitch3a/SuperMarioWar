package smw.menu;

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
import java.util.Set;

import javax.swing.JFrame;

public class WorldPreviewGameFrame extends Canvas{

  private static final long serialVersionUID = 1L;

  // TODO - RPG - should figure out how to setup resolution options w/ scaling...
  public static int res_width = 640;
  public static int res_height = 480;
  public static double scaleFactorWidth = 1.5f;
  public static double scaleFactorHeight = 1.5f;
  public static double scaleMiniFactorWidth = .5f;
  public static double scaleMiniFactorHeight = .5f;
  
  
  private JFrame frame;
  private BufferStrategy bs;
  
  private Set<WorldPreview> worlds;

  /**
   * Creates the frame to display the game.
   * @param players
   * @param world
   * @param game
   */
  public WorldPreviewGameFrame(Set<WorldPreview> worlds){   
    this.worlds = worlds;
    
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
        WorldPreviewTest.shutdown();
        System.exit(0);
      }
    });
    
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
    
    Graphics2D g2d = (Graphics2D)bs.getDrawGraphics();
    g2d.fillRect(0, 0, getWidth(), getHeight());
        
    //Draw all the worlds where they belong
    for(WorldPreview w : worlds){
      Graphics2D g2d2 = (Graphics2D)bs.getDrawGraphics();
      g2d2.translate(w.x*scaleFactorWidth, w.y*scaleFactorHeight);
      g2d2.scale(w.scaleWidth*scaleFactorWidth, w.scaleHeight*scaleFactorHeight);
      w.drawBackground(g2d2, this);
      w.drawLayer2(g2d2, this);
      w.drawLayer3(g2d2, this);
      g2d2.dispose();
    }
    
    g2d.scale(scaleFactorWidth, scaleFactorHeight);
  
    g2d.dispose();
    // Display contents of buffer.
    bs.show();
    Toolkit.getDefaultToolkit().sync();
  }
  
  synchronized void resetScalingFactors(){
    double width = frame.getWidth();
    double height = frame.getHeight();

    scaleFactorWidth =  width/res_width;
    scaleFactorHeight = height/res_height;
  }
}
