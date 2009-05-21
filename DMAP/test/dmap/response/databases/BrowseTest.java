package dmap.response.databases;

import static org.junit.Assert.assertEquals;
import interfaces.Constants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import api.Reader;
import api.Writer;
import dmap.DACPReader;
import dmap.DACPWriter;
import dmap.response.databases.Browse;

public class BrowseTest {
	
	private List<String> list;
	private Browse input;
	private Browse output;

	@Before
	public void setUp() throws Exception {
		
		list = new ArrayList<String>();
		list.add("Test1");
		list.add("Test2");
		list.add("Test3");
		
		input = new Browse(Constants.daap_browsealbumlisting, list);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Writer w = new DACPWriter(out);
		w.appendNode(input);
		
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		Reader r = new DACPReader(in, in.available());
		
		output = Browse.read(r.nextComposite(r.iterator().next()));
	}

	@Test
	public void testContent() {
		assertEquals(input.content(), output.content());
	}

	@Test
	public void testIterator() {
		Iterator<String> a = input.iterator();
		Iterator<String> b = output.iterator();
		
		while (a.hasNext() && b.hasNext()) {
			assertEquals(a.next(), b.next());
		}
		
		assertEquals(a.hasNext(), b.hasNext());
	}

	@Test
	public void testSize() {
		assertEquals(input.size(), output.size());
	}

}
