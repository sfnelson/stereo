package util.response.ctrlint;

import static org.junit.Assert.assertEquals;
import interfaces.Constants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;

import api.Reader;
import api.Writer;
import dacp.DACPReader;
import dacp.DACPWriter;

public class GetPropertyTest {

	private GetProperty input;
	private GetProperty output;

	@Before
	public void setUp() throws Exception {

		input = new GetProperty(Constants.dmcp_volume, 100);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Writer w = new DACPWriter(out);
		w.appendNode(input);

		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		Reader r = new DACPReader(in, in.available());

		output = GetProperty.read(r.nextComposite(r.iterator().next()));
	}

	@Test
	public void testGetProperty() {
		assertEquals(input.getProperty(), output.getProperty());
	}

	@Test
	public void testGetValue() {
		assertEquals(input.getValue(), output.getValue());
	}

}
