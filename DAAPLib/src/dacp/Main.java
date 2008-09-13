package dacp;

import daap.DAAPLackey;
import music.DJ;

public class Main {

	public static void main(String [] args) {
		DAAPLackey.register();
		DACPServer.register();
		new DJ();
	}
}
