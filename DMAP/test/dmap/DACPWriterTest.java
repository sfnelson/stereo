package dmap;

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import api.Constants;
import api.Node;
import api.Writer;

public class DACPWriterTest {
	
	private ByteArrayOutputStream stream;
	private DACPWriter writer;

	@Before
	public void setUp() throws Exception {
		stream = new ByteArrayOutputStream();
		writer = new DACPWriter(stream);
	}

	@Test
	public void testAppendBoolean() {
		writer.appendBoolean(Constants.dmap_supportsbrowse, true);
		assertArrayEquals(new byte[] { 'm', 's', 'b', 'r', 0, 0, 0, 1, 1 }, stream.toByteArray());
	}

	@Test
	public void testAppendByte() {
		writer.appendByte(Constants.dmap_itemkind, (byte)1);
		assertArrayEquals(new byte[] { 'm', 'i', 'k', 'd', 0, 0, 0, 1, 1 }, stream.toByteArray());
	}
	
	@Test
	public void testAppendShort() {
		writer.appendShort(Constants.daap_songyear, (short)2009);
		assertArrayEquals(new byte[] { 'a', 's', 'y', 'r', 0, 0, 0, 2, 7, -39 }, stream.toByteArray());
	}

	@Test
	public void testAppendInteger() {
		writer.appendInteger(Constants.dmap_status, 1);
		assertArrayEquals(new byte[] { 'm', 's', 't', 't', 0, 0, 0, 4, 0, 0, 0, 1 }, stream.toByteArray());
	}

	@Test
	public void testAppendLong() {
		writer.appendLong(Constants.dmap_persistentid, 0x102030405l);
		assertArrayEquals(new byte[] { 'm', 'p', 'e', 'r', 0, 0, 0, 8, 0, 0, 0, 1, 2, 3, 4, 5 }, stream.toByteArray());
	}

	@Test
	public void testAppendLongLong() {
		writer.appendLongLong(Constants.dacp_nowplaying, new int[] {1,2,3,4});
		assertArrayEquals(new byte[] { 'c', 'a', 'n', 'p', 0, 0, 0, 16, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 3, 0, 0, 0, 4 }, stream.toByteArray());
	}

	@Test
	public void testAppendNode() {
		writer.appendNode(new Node() {
			public Constants type() {
				return Constants.dmap_listing;
			}
			public void write(Writer writer) {
				return;
			}
		});
		assertArrayEquals(new byte[] { 'm', 'l', 'c', 'l', 0, 0, 0, 0 }, stream.toByteArray());
	}

	@Test
	public void testAppendString() {
		writer.appendString(Constants.dmap_itemname, "hello");
		assertArrayEquals(new byte[] { 'm', 'i', 'n', 'm', 0, 0, 0, 5, 'h', 'e', 'l', 'l', 'o' }, stream.toByteArray());
	}

	@Test
	public void testAppendVersion() {
		writer.appendVersion(Constants.dmap_protocolversion, new byte[] {0, 1, 2, 3});
		assertArrayEquals(new byte[] { 'm', 'p', 'r', 'o', 0, 0, 0, 4, 0, 1, 2, 3 }, stream.toByteArray());
	}

	@Test
	public void testAppendDate() {
		Calendar c = Calendar.getInstance();
		c.set(1985, 9, 11, 0, 0, 0);
		writer.appendDate(Constants.dmap_utctime, c);
		assertArrayEquals(new byte[] { 'm', 's', 't', 'c', 0, 0, 0, 4, 0x1d, (byte)0xab, (byte)0xba, 0x40 }, stream.toByteArray());
	}

	@Test
	public void testAppendList() {
		List<Node> list = new ArrayList<Node>();
		list.add(new Node() {
			public Constants type() {
				return Constants.dmap_listingitem;
			}
			public void write(Writer writer) {
				writer.appendString(Constants.dmap_itemname, "hello");
			}
		});
		writer.appendList(Constants.daap_browseartistlisting, (byte)0, list);
		assertArrayEquals(new byte[] {
				'm', 'u', 't', 'y', 0, 0, 0, 1, 0,
				'm', 't', 'c', 'o', 0, 0, 0, 4, 0, 0, 0, 1,
				'm', 'r', 'c', 'o', 0, 0, 0, 4, 0, 0, 0, 1,
				'a', 'b', 'a', 'r', 0, 0, 0, 21,
				'm', 'l', 'i', 't', 0, 0, 0, 13,
				'm', 'i', 'n', 'm', 0, 0, 0, 5, 'h', 'e', 'l', 'l', 'o'
		}, stream.toByteArray());
	}
	
}
