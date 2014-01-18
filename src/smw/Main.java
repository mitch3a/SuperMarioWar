package smw;

import smw.gfx.Palette;

public class Main {
//here is a comment
	public static void main(String[] args) {
		new Palette().loadPalette();
		int numPlayers = 2;
		new Game(numPlayers).start();
	}
}