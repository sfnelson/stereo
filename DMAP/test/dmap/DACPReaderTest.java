package dmap;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import api.Constants;
import api.Reader;

public class DACPReaderTest {
	
	private DACPReader reader;
	private byte[] buffer = new byte[1024];

	@Before
	public void setUp() throws Exception {
		InputStream in = new ByteArrayInputStream(buffer);
		reader = new DACPReader(in, 1024);
	}

	@Test
	public void testHasNextBoolean() {
		byte[] test = new byte[] {
				'm', 's', 'b', 'r', 0, 0, 0, 1, 1,
				'm', 's', 'b', 'r', 0, 0, 0, 4, 0, 0, 0, 1};
		System.arraycopy(test, 0, buffer, 0, test.length);
		reader.iterator().next();
		assertTrue(reader.hasNextBoolean(Constants.dmap_supportsbrowse));
		reader.iterator().next();
		assertFalse(reader.hasNextBoolean(Constants.dmap_supportsbrowse));
	}

	@Test
	public void testHasNextByte() {
		byte[] test = new byte[] {
				'm', 'i', 'k', 'd', 0, 0, 0, 1, 1,
				'm', 'i', 'k', 'd', 0, 0, 0, 4, 0, 0, 0, 1};
		System.arraycopy(test, 0, buffer, 0, test.length);
		reader.iterator().next();
		assertTrue(reader.hasNextByte(Constants.dmap_itemkind));
		reader.iterator().next();
		assertFalse(reader.hasNextByte(Constants.dmap_itemkind));
	}

	@Test
	public void testHasNextComposite() {
		byte[] test = new byte[] {
				'm', 'l', 'c', 'l', 0, 0, 0, 4, 0, 0, 0, 1,
				'm', 'l', 'c', 'l', 0, 0, 0, 12,
					'm', 's', 'd', 'c', 0, 0, 0, 4, 0, 0, 0, 1};
		System.arraycopy(test, 0, buffer, 0, test.length);
		reader.iterator().next();
		assertFalse(reader.hasNextComposite(Constants.dmap_listing));
		reader.iterator().next();
		assertTrue(reader.hasNextComposite(Constants.dmap_listing));
	}
	
	@Test
	public void testHasNextDate() {
		byte[] test = new byte[] {
				'm', 's', 't', 'c', 0, 0, 0, 4, 0, 0, 0, 1,
				'm', 's', 't', 'c', 0, 0, 0, 1, 1};
		System.arraycopy(test, 0, buffer, 0, test.length);
		reader.iterator().next();
		assertTrue(reader.hasNextDate(Constants.dmap_utctime));
		reader.iterator().next();
		assertFalse(reader.hasNextDate(Constants.dmap_utctime));
	}

	@Test
	public void testHasNextInteger() {
		byte[] test = new byte[] {
				'm', 's', 't', 't', 0, 0, 0, 1, 1,
				'm', 's', 't', 't', 0, 0, 0, 4, 0, 0, 0, 1};
		System.arraycopy(test, 0, buffer, 0, test.length);
		reader.iterator().next();
		assertFalse(reader.hasNextInteger(Constants.dmap_status));
		reader.iterator().next();
		assertTrue(reader.hasNextInteger(Constants.dmap_status));
	}

	@Test
	public void testHasNextLong() {
		byte[] test = new byte[] {
				'm', 'p', 'e', 'r', 0, 0, 0, 4, 0, 0, 0, 1,
				'm', 'p', 'e', 'r', 0, 0, 0, 8, 0, 0, 0, 1, 2, 3, 4, 5};
		System.arraycopy(test, 0, buffer, 0, test.length);
		reader.iterator().next();
		assertFalse(reader.hasNextLong(Constants.dmap_persistentid));
		reader.iterator().next();
		assertTrue(reader.hasNextLong(Constants.dmap_persistentid));
	}

	@Test
	public void testHasNextLongLong() {
		byte[] test = new byte[] {
				'c', 'a', 'n', 'p', 0, 0, 0, 1, 1,
				'c', 'a', 'n', 'p', 0, 0, 0, 16, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 3, 0, 0, 0, 4};
		System.arraycopy(test, 0, buffer, 0, test.length);
		reader.iterator().next();
		assertFalse(reader.hasNextLongLong(Constants.dacp_nowplaying));
		reader.iterator().next();
		assertTrue(reader.hasNextLongLong(Constants.dacp_nowplaying));
	}

	@Test
	public void testHasNextShort() {
		byte[] test = new byte[] {
				'a', 's', 'y', 'r', 0, 0, 0, 1, 1,
				'a', 's', 'y', 'r', 0, 0, 0, 2, 0, 9};
		System.arraycopy(test, 0, buffer, 0, test.length);
		reader.iterator().next();
		assertFalse(reader.hasNextShort(Constants.daap_songyear));
		reader.iterator().next();
		assertTrue(reader.hasNextShort(Constants.daap_songyear));
	}

	@Test
	public void testHasNextString() {
		byte[] test = new byte[] {
				'm', 'i', 'n', 'm', 0, 0, 0, 5, 'h', 'e', 'l', 'l', 'o'};
		System.arraycopy(test, 0, buffer, 0, test.length);
		reader.iterator().next();
		assertTrue(reader.hasNextString(Constants.dmap_itemname));
	}

	@Test
	public void testHasNextVersion() {
		byte[] test = new byte[] {
				'm', 'p', 'r', 'o', 0, 0, 0, 5, 'h', 'e', 'l', 'l', 'o',
				'm', 'p', 'r', 'o', 0, 0, 0, 4, 0, 1, 2, 3};
		System.arraycopy(test, 0, buffer, 0, test.length);
		reader.iterator().next();
		assertFalse(reader.hasNextVersion(Constants.dmap_protocolversion));
		reader.iterator().next();
		assertTrue(reader.hasNextVersion(Constants.dmap_protocolversion));
	}

	@Test(expected=NoSuchElementException.class)
	public void testNextBoolean() {
		byte[] test = new byte[] {
				'm', 's', 'b', 'r', 0, 0, 0, 1, 1,
				'm', 's', 'b', 'r', 0, 0, 0, 4, 0, 0, 0, 1};
		System.arraycopy(test, 0, buffer, 0, test.length);
		reader.iterator().next();
		assertEquals(true, reader.nextBoolean(Constants.dmap_supportsbrowse));
		reader.iterator().next();
		reader.nextBoolean(Constants.dmap_supportsbrowse); //throws exception
	}

	@Test(expected=NoSuchElementException.class)
	public void testNextByte() {
		byte[] test = new byte[] {
				'm', 'i', 'k', 'd', 0, 0, 0, 1, 1,
				'm', 'i', 'k', 'd', 0, 0, 0, 4, 0, 0, 0, 1};
		System.arraycopy(test, 0, buffer, 0, test.length);
		reader.iterator().next();
		assertEquals(0x1, reader.nextByte(Constants.dmap_itemkind));
		reader.iterator().next();
		reader.nextByte(Constants.dmap_itemkind);
	}

	@Test(expected=NoSuchElementException.class)
	public void testNextComposite() {
		byte[] test = new byte[] {
				'm', 'l', 'c', 'l', 0, 0, 0, 12,
					'm', 's', 'd', 'c', 0, 0, 0, 4, 0, 0, 0, 1,
				'm', 'l', 'c', 'l', 0, 0, 0, 4, 0, 0, 0, 1,};
		System.arraycopy(test, 0, buffer, 0, test.length);
		reader.iterator().next();
		assertTrue(reader.nextComposite(Constants.dmap_listing) instanceof Reader);
		reader.iterator().next();
		reader.nextComposite(Constants.dmap_listing);
	}
	
	@Test(expected=NoSuchElementException.class)
	public void testNextDate() {
		byte[] test = new byte[] {
				'm', 's', 't', 'c', 0, 0, 0, 4, 0x1d, (byte)0xab, (byte)0xba, 0x40,
				'm', 's', 't', 'c', 0, 0, 0, 1, 1};
		System.arraycopy(test, 0, buffer, 0, test.length);
		reader.iterator().next();
		Calendar d = Calendar.getInstance();
		d.set(1985, 9, 11, 0, 0, 0);
		assertEquals(d, reader.nextDate(Constants.dmap_utctime));
		reader.iterator().next();
		reader.nextDate(Constants.dmap_utctime);
	}

	@Test(expected=NoSuchElementException.class)
	public void testNextInteger() {
		byte[] test = new byte[] {
				'm', 's', 't', 't', 0, 0, 0, 4, 0, 0, 0, 1,
				'm', 's', 't', 't', 0, 0, 0, 1, 1};
		System.arraycopy(test, 0, buffer, 0, test.length);
		reader.iterator().next();
		assertEquals(1, reader.nextInteger(Constants.dmap_status));
		reader.iterator().next();
		reader.nextInteger(Constants.dmap_status);
	}

	@Test(expected=NoSuchElementException.class)
	public void testNextLong() {
		byte[] test = new byte[] {
				'm', 'p', 'e', 'r', 0, 0, 0, 8, 0, 0, 0, 1, 2, 3, 4, 5,
				'm', 'p', 'e', 'r', 0, 0, 0, 4, 0, 0, 0, 1};
		System.arraycopy(test, 0, buffer, 0, test.length);
		reader.iterator().next();
		assertEquals(0x102030405l, reader.nextLong(Constants.dmap_persistentid));
		reader.iterator().next();
		reader.nextLong(Constants.dmap_persistentid);
	}

	@Test(expected=NoSuchElementException.class)
	public void testNextLongLong() {
		byte[] test = new byte[] {
				'c', 'a', 'n', 'p', 0, 0, 0, 16, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 3, 0, 0, 0, 4,
				'c', 'a', 'n', 'p', 0, 0, 0, 1, 1};
		System.arraycopy(test, 0, buffer, 0, test.length);
		reader.iterator().next();
		assertArrayEquals(new int[] {1,2,3,4}, reader.nextLongLong(Constants.dacp_nowplaying));
		reader.iterator().next();
		reader.nextLongLong(Constants.dacp_nowplaying);
	}

	@Test(expected=NoSuchElementException.class)
	public void testNextShort() {
		byte[] test = new byte[] {
				'a', 's', 'y', 'r', 0, 0, 0, 2, 0, 9,
				'a', 's', 'y', 'r', 0, 0, 0, 1, 1};
		System.arraycopy(test, 0, buffer, 0, test.length);
		reader.iterator().next();
		assertEquals(9, reader.nextShort(Constants.daap_songyear));
		reader.iterator().next();
		reader.nextShort(Constants.daap_songyear);
	}

	@Test
	public void testNextString() {
		byte[] test = new byte[] {
				'm', 'i', 'n', 'm', 0, 0, 0, 5, 'h', 'e', 'l', 'l', 'o'};
		System.arraycopy(test, 0, buffer, 0, test.length);
		reader.iterator().next();
		assertEquals("hello", reader.nextString(Constants.dmap_itemname));
	}

	@Test(expected=NoSuchElementException.class)
	public void testNextVersion() {
		byte[] test = new byte[] {
				'm', 'p', 'r', 'o', 0, 0, 0, 4, 0, 1, 2, 3,
				'm', 'p', 'r', 'o', 0, 0, 0, 5, 'h', 'e', 'l', 'l', 'o'};
		System.arraycopy(test, 0, buffer, 0, test.length);
		reader.iterator().next();
		assertArrayEquals(new byte[] {0,1,2,3}, reader.nextVersion(Constants.dmap_protocolversion));
		reader.iterator().next();
		reader.nextVersion(Constants.dmap_protocolversion);
	}

	@Test(expected=NoSuchElementException.class)
	public void testIterator() {
		byte[] test = new byte[] {
				'm', 'i', 'n', 'm', 0, 0, 0, 5, 'h', 'e', 'l', 'l', 'o',
				'f', 'a', 'i', 'l', 0, 0, 0};
		System.arraycopy(test, 0, buffer, 0, test.length);
		InputStream in = new ByteArrayInputStream(buffer);
		reader = new DACPReader(in, test.length);
		assertTrue(reader.iterator().hasNext());
		assertEquals("hello", reader.nextString(reader.iterator().next()));
		assertFalse(reader.iterator().hasNext());
		reader.iterator().next();
	}

}
