package smw;

import smw.level.Level;

public class Main {
	public static void main(String[] args) {
		//Level.printAllMapsAndVersions();
	  int numPlayers = 2;
		new Game(numPlayers).start();
	}
}