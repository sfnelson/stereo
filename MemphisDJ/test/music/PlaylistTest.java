package music;

import static org.junit.Assert.*;

import interfaces.Album;
import interfaces.Track;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


public class PlaylistTest {
	
	List<Track> p;
	Track track;
	
	@Before
	public void setUp() throws Exception {
		p = new ArrayList<Track>();
		track = new Track() {
			public int getId() { return 1; }
			public Object getTag(int i) { return "value"; }
			public Map<Integer, Object> getAllTags() { return null; }
			public InputStream getStream() { return null; }
			public Album getAlbum() { return null; }
			public void setAlbum(Album album) {}
		};
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
