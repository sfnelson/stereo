package music;

import static org.junit.Assert.*;

import org.junit.Test;

public class DJTest {
	
	DJ a = new DJ("Test DJ");
	
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
		for (int i = 0; i <= 100; i+=50) {
			a.volume().setVolume(i);
			assertEquals(i, a.volume().getVolume());
		}
	}

	@Test
	public void testGetVolume() {
		int i = a.volume().getVolume();
		assertTrue(i>=0 && i<=100);
	}

	@Test
	public void testTracksAdded() {
		assertTrue(a.playlist().hasNext());
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
