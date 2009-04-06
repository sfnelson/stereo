import java.io.IOException;
import java.util.ServiceLoader;

import spi.StereoServer;

import music.DJ;


public class Main {
	
	public static void main(String [] args) throws IOException {
		DJ dj = new DJ("Memphis Stereo");
		
		ServiceLoader<StereoServer> loader = ServiceLoader.load(StereoServer.class);
		for (StereoServer server: loader) {
			System.out.println(server.getClass());
			server.start(dj, args);
		}
	}
	
}
