package ui.screen;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;

import javax.swing.JFrame;

import model.Player;

public class GameFrame extends JFrame{
	//TODO this should be in a common area
	public static final String Title = "Super Mario War";
	
	//TODO this is REALLY hacky but i just wanted to get the stupid thing to work
	Player[] players;
	
	public GameFrame(Player[] players){
		add(new GamePanel());
	    setTitle(Title);
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    setSize(800, 600); //TODO this should probably be pulled out
	    setLocationRelativeTo(null);
	    setVisible(true);
	    setResizable(false);
	    
	    this.players = players;
	}

	public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2d = (Graphics2D)g;
        if(players != null && players.length > 0){
        	for(Player p : players){
        		g2d.drawImage(p.getImage(), p.getTransform(), this);
        	}
        }

        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }
}
