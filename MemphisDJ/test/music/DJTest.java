package music;

import static org.junit.Assert.*;

import org.junit.Test;

public class DJTest {
	
	DJ a = new DJ();
	
	@Test
	public void testFillPlaylist() {
		fail("Not yet implemented");
	}

	@Test
	public void testFillPlaylistEvenly() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTracksFiltered() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetVolume() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetVolume() {

		a.setVolume(20);
		assertTrue(a.getVolume()==20);
	}

	@Test
	public void testTracksAdded() {
		a.getPlaylist().size();
	}

	@Test
	public void testLibraryChanged() {
		fail("Not yet implemented");
	}

	@Test
	public void testPlaybackFinished() {
		fail("Not yet implemented");
	}

	@Test
	public void testPlaybackStarted() {
		fail("Not yet implemented");
	}

}
