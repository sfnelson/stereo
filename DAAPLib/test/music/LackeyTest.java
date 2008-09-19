package music;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import interfaces.Track;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import daap.DAAPClient;
import daap.DAAPLackey;
import daap.DAAPTrack;


public class LackeyTest {
	
	@Test
	public void getTracksTest(){
		try {
			DAAPClient client = new DAAPClient("majoribanks.mcs.vuw.ac.nz", 3689, 0);
			DAAPLackey lackey = new DAAPLackey();
			lackey.newConnection(client);
			List<Track> tracks = lackey.getLibrary();
			assertTrue(tracks.size() > 1600);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	@Ignore("infinite loops ftw!!!")
	@Test
	public void handshakeTest(){
		new DAAPLackey();
		while(true);
	}
	
	@Test
	public void checkListTest(){
		DAAPLackey l = new DAAPLackey();
		List<Track> p = new ArrayList<Track>();
		try {
			p.add(new DAAPTrack(new HashMap<Integer, Object>(), new DAAPClient("majoribanks.mcs.vuw.ac.nz", 3689, 0)));
			l.trackSource().checkPlaylist(p);
			assertEquals(0, p.size());
		} catch (IOException e) {
			fail();
		}
	}
	
}
