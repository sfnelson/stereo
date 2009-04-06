package util.response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import util.response.Databases.Database;
import api.Reader;
import api.Writer;
import dacp.DACPReader;
import dacp.DACPWriter;

public class DatabasesTest {

	private Databases input;
	private Databases output;

	@Before
	public void setUp() throws Exception {

		input = new Databases(1, 1000, "Test Database", 100, 10);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Writer w = new DACPWriter(out);
		w.appendNode(input);

		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		Reader r = new DACPReader(in, in.available());

		output = Databases.read(r.nextComposite(r.iterator().next()));
	}

	@Test
	public void testDatabases() {
		Iterator<Database> in = input.databases();
		Iterator<Database> out = output.databases();
		while (in.hasNext() && out.hasNext()) {
			Database a = in.next();
			Database b = out.next();
			assertEquals(a.id(), b.id());
			assertEquals(a.persistentId(), b.persistentId());
			assertEquals(a.name(), b.name());
			assertEquals(a.containers(), b.containers());
			assertEquals(a.tracks(), b.tracks());
			assertEquals(a.editStatus(), b.editStatus());
			assertEquals(a, b);
		}
		
		assertTrue(in.hasNext() == out.hasNext());
	}

	@Test
	public void testSize() {
		assertEquals(input.size(), output.size());
	}

}
