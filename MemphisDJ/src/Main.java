import java.io.IOException;
import java.util.Iterator;

import javax.imageio.spi.ServiceRegistry;

import spi.StereoServer;

import music.DJ;


public class Main {
	
	public static void main(String [] args) throws IOException {
		DJ dj = new DJ("Memphis Stereo");
		
		for (Iterator<StereoServer> it = ServiceRegistry.lookupProviders(StereoServer.class); it.hasNext();) {
			StereoServer server = it.next();
			System.out.println("loading server: " + server.getClass());
			server.start(dj, args);
		}
	}
	
}
