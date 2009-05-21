package util.response;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;

import api.Reader;
import api.Writer;
import dmap.DACPReader;
import dmap.DACPWriter;
import dmap.response.Update;

public class UpdateTest {

	private Update input;
	private Update output;

	@Before
	public void setUp() throws Exception {

		input = new Update(15);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Writer w = new DACPWriter(out);
		w.appendNode(input);

		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		Reader r = new DACPReader(in, in.available());

		output = Update.read(r.nextComposite(r.iterator().next()));
	}

	@Test
	public void testRevision() {
		assertEquals(input.revision(), output.revision());
	}

}
