package ui.screen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import ui.PlayerControl;

import model.Player;

public class Game implements ActionListener{
	
	GameFrame game_frame;
	Player[] players;
	PlayerControl[] player_controls;
	
	public Game(final int numPlayers){
		players = new Player[numPlayers];
		player_controls = new PlayerControl[numPlayers];
		for(int i = 0 ; i < numPlayers ; ++i){
			players[i] = new Player();
			players[i].init(50, 50, "4matsy_BubbleBobble.bmp");
			player_controls[i] = new PlayerControl(players[i]);			
		}
	
	    this.game_frame = new GameFrame(players);
		
	    //TODO I think the player/control should be packaged together... but this is good enough for now
		game_frame.addKeyListener(player_controls[0]);
	}
	
	public void start(){
		//TODO this is a temporary way to refresh the screen
		Timer timer = new Timer(17, this);
        timer.start();
	}

	//TODO this is probably going to be replaced with actual controls
	@Override
	public void actionPerformed(ActionEvent e) {
		//TODO again this is just for testing 1 player
		players[0].move();
		game_frame.repaint();
	}
} 
