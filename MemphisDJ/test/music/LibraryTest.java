package music;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class LibraryTest {

	private List<Track> trackList;
	private Set<Track> trackSet;
	
	@Before
	public void setUp() throws Exception {
		trackList = new ArrayList<Track>();
		trackSet = new HashSet<Track>();
		
		for (int i = 0; i < 10; i++) {
			Track t = new TestTrack(i);
			trackList.add(t);
			trackSet.add(t);
		}
	}

	@Test
	public void testAdded() {
		Library l = new Library("test");
		l.added(trackList);
		assertTrue(l.size() == 10);
	}

	@Test
	public void testRemoved() {
		Library l = new Library("test");
		l.added(trackList);
		l.removed(trackSet);
		assertTrue(l.size() == 0);
	}
	
	private class TestTrack extends Track {
		
		public TestTrack(int id) {
			super(id, id);
		}

		@Override
		public InputStream getStream() throws IOException {
			// TODO Auto-generated method stub
			return null;
		}
	}

}
