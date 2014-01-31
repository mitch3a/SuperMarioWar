package smw;

import smw.level.Level;

public class Main {
	public static void main(String[] args) {
		//Level.printAllMapsAndVersions();
	  int numPlayers = 1;
		new Game(numPlayers).start();
	}
}