package dacp;

import java.io.IOException;

import daap.DAAPLackey;
import music.DJ;

public class Main {

	public static void main(String [] args) throws IOException {
		DJ dj = new DJ("Memphis Stereo");
		new DAAPLackey(dj.library());
		new DACPServer(args[0], args.length>1?Integer.parseInt(args[1]):3689, dj);
	}
}
