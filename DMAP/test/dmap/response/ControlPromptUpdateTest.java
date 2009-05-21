package dmap.response;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;

import api.Reader;
import api.Writer;
import dmap.DACPReader;
import dmap.DACPWriter;
import dmap.response.ControlPromptUpdate;

public class ControlPromptUpdateTest {

	private ControlPromptUpdate input;
	private ControlPromptUpdate output;

	@Before
	public void setUp() throws Exception {

		input = new ControlPromptUpdate(15);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Writer w = new DACPWriter(out);
		w.appendNode(input);

		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		Reader r = new DACPReader(in, in.available());

		output = ControlPromptUpdate.read(r.nextComposite(r.iterator().next()));
	}

	@Test
	public void testPromptId() {
		assertEquals(input.promptId(), output.promptId());
	}

}
