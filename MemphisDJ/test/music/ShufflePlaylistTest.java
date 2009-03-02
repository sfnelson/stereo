package music;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import interfaces.AbstractTrack;
import interfaces.Constants;
import interfaces.Track;

import org.junit.Before;
import org.junit.Test;

public class ShufflePlaylistTest {

	private ShufflePlaylist list;
	private Track a = new TestTrack("A");
	private Track b = new TestTrack("B");
	private Track c = new TestTrack("C");
	private Track d = new TestTrack("D");
	private Track e = new TestTrack("A");
	
	@Before
	public void setUp() throws Exception {
		list = new ShufflePlaylist(1, 1, "test");
		list.append(a);
		list.append(b);
		list.append(c);
	}
	
	@Test
	public void testHasNext() {
		assertTrue(list.hasNext());
		list = new ShufflePlaylist(1, 1, "test");
		assertFalse(list.hasNext());
	}

	@Test
	public void testAppend() {
		list.append(d);
		assertEquals(a, list.next());
		assertEquals(b, list.next());
		assertEquals(c, list.next());
		assertEquals(d, list.next());
		assertFalse(list.hasNext());
	}

	@Test
	public void testInsertFirst() {
		list.insertFirst(d);
		assertEquals(d, list.next());
		assertEquals(a, list.next());
		assertEquals(b, list.next());
		assertEquals(c, list.next());
		assertFalse(list.hasNext());
	}

	@Test
	public void testMove() {
		list.move(a, c);
		assertEquals(b, list.next());
		assertEquals(a, list.next());
		assertEquals(c, list.next());
		assertFalse(list.hasNext());
	}
	
	@Test
	public void testMoveFirst() {
		list.move(d, a);
		assertEquals(d, list.next());
		assertEquals(a, list.next());
		assertEquals(b, list.next());
		assertEquals(c, list.next());
		assertFalse(list.hasNext());
	}
	
	@Test
	public void testMoveLast() {
		list.move(d, null);
		assertEquals(a, list.next());
		assertEquals(b, list.next());
		assertEquals(c, list.next());
		assertEquals(d, list.next());
		assertFalse(list.hasNext());
	}
	
	@Test
	public void testMoveSelf() {
		list.move(e, a);
		print();
		assertEquals(a, list.next());
		assertEquals(b, list.next());
		assertEquals(c, list.next());
		assertFalse(list.hasNext());
	}

	@Test
	public void testRemove() {
		list.remove(b);
		assertEquals(a, list.next());
		assertEquals(c, list.next());
		assertFalse(list.hasNext());
	}
	
	private void print() {
		for (Track t: list.tracks()) {
			System.out.print(t.get(Constants.dmap_itemname)+", ");
		}
		System.out.println();
	}

	private static class TestTrack extends AbstractTrack {

		public TestTrack(String name) {
			super(name.hashCode(), name.hashCode());
			this.put(Constants.dmap_itemname, name);
		}

		@Override
		public InputStream getStream() throws IOException {
			return null;
		}
		
	}
}
