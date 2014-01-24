package smw;

import smw.gfx.Font;

public class Main {
//here is a comment
	public static void main(String[] args) {
		Font font = Font.getInstance();
		int numPlayers = 2;
		new Game(numPlayers).start();
	}
}