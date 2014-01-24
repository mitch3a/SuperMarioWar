package smw.ui.screen;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import smw.entity.Player;
import smw.gfx.Font;
import smw.gfx.Scoreboard;
import smw.level.Level;

public class GameFrame extends JFrame{

  private static final long serialVersionUID = 1L;

  //TODO this should be in a common area
	public static final String Title = "Super Mario War";
	
	// TODO - RPG - should figure out how to setup resolution options w/ scaling...
	public static int res_width = 640;
	public static int res_height = 480;
	
	//TODO this is REALLY hacky but i just wanted to get the stupid thing to work
	Player[] players;
	Scoreboard sB;
	Level level;
	
	public GameFrame(Player[] players, Level level){
		add(new GamePanel());
	    setTitle(Title);
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    setSize(res_width, res_height);
	    setLocationRelativeTo(null);
	    setVisible(true);
	    setResizable(false);

	    createBufferStrategy(3);
	    
	    this.players = players;
	    this.level = level;
	    sB = new Scoreboard(this.players, 50);
	}

    @Override
    public void paint(Graphics g) {
    // Get buffer strategy and loop to protect against lost frames.
    BufferStrategy bs = getBufferStrategy();
    do {
      try {
        g = bs.getDrawGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        Graphics2D g2d = (Graphics2D)g;
        level.draw(g2d, this);
        if (players != null && players.length > 0) {
          for(Player p : players){
            p.draw(g2d, this);
          }
        }
        sB.draw(g2d, this);
      } finally {
        // Free up graphics.
        g.dispose();
      }
      // Display contents of buffer.
      bs.show();
    } while (bs.contentsLost());   
  }
}
