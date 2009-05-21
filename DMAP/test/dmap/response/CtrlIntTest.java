package dmap.response;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import api.Reader;
import api.Writer;
import dmap.DACPReader;
import dmap.DACPWriter;
import dmap.response.CtrlInt;

public class CtrlIntTest {

	public CtrlInt input;
	public CtrlInt output;

	@Before
	public void setUp() throws Exception {

		input = new CtrlInt(1);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Writer w = new DACPWriter(out);
		w.appendNode(input);

		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		Reader r = new DACPReader(in, in.available());

		output = CtrlInt.read(r.nextComposite(r.iterator().next()));
	}

	@Test
	public void testIds() {
		Iterator<Integer> a = input.ids().iterator();
		Iterator<Integer> b = output.ids().iterator();
		while (a.hasNext() && b.hasNext()) {
			assertEquals(a.next(), b.next());
		}
		assertEquals(a.hasNext(), b.hasNext());
	}
}
