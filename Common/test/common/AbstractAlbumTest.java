package common;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import api.Constants;

public class AbstractAlbumTest {

	private AbstractAlbum album;
	
	@Before
	public void setUp() throws Exception {
		album = new AbstractAlbum(123, 4567, "Test Album", "Artist", 5);
	}

	@Test
	public void testGet() {
		assertEquals(new Integer(123), album.get(Constants.dmap_itemid));
		assertEquals(new Long(4567), album.get(Constants.dmap_persistentid));
		assertEquals("Test Album", album.get(Constants.dmap_itemname));
		assertEquals("Artist", album.get(Constants.daap_songalbumartist));
		assertEquals(5, album.get(Constants.dmap_itemcount));
	}

	@Test
	public void testId() {
		assertEquals(123, album.id());
	}

	@Test
	public void testPersistentId() {
		assertEquals(4567l, album.persistentId());
	}

	@Test
	public void testName() {
		assertEquals("Test Album", album.name());
	}

	@Test
	public void testArtist() {
		assertEquals("Artist", album.artist());
	}

	@Test
	public void testTracks() {
		assertEquals(5, album.tracks());
	}

	@Test
	public void testSetTracks() {
		album.setTracks(4);
		assertEquals(4, album.tracks());
	}

}
