package music;

import static org.junit.Assert.assertTrue;
import interfaces.Track;

import java.util.List;

import org.junit.Before;
import org.junit.Test;


public class PlaylistTest {
	
	List<Track> p;
	Track track;
	
	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void addTest(){
		assertTrue(p.add(track));
	}
	
	@Test
	public void removeTest(){
		p.add(track);
		assertTrue(p.remove(track));
	}

}
