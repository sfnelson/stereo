package dmap.response;

import static org.junit.Assert.assertEquals;
import interfaces.Constants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import api.Reader;
import api.Writer;
import dmap.DACPReader;
import dmap.DACPWriter;
import dmap.response.ContentCodes;

public class ContentCodesTest {
	
	private ContentCodes input;
	private ContentCodes output;

	@Before
	public void setUp() throws Exception {
		
		input = new ContentCodes();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Writer w = new DACPWriter(out);
		w.appendNode(input);

		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		Reader r = new DACPReader(in, in.available());

		output = ContentCodes.read(r.nextComposite(r.iterator().next()));
	}

	@Test
	public void testSize() {
		assertEquals(input.size(), output.size());
	}

	@Test
	public void testIterator() {
		Iterator<Constants> a = input.iterator();
		Iterator<Constants> b = output.iterator();
		
		while (a.hasNext() && b.hasNext()) {
			assertEquals(a.next(), b.next());
			
		}
		
		assertEquals(a.hasNext(), b.hasNext());
	}

}
