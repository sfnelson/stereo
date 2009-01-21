package util.response;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;

import api.Reader;
import api.Writer;
import dacp.DACPReader;
import dacp.DACPWriter;

public class LoginTest {

	private Login input;
	private Login output;

	@Before
	public void setUp() throws Exception {

		input = new Login(1234);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Writer w = new DACPWriter(out);
		w.appendNode(input);

		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		Reader r = new DACPReader(in, in.available());

		output = Login.read(r.nextComposite(r.iterator().next()));
	}

	@Test
	public void testGetSessionId() {
		assertEquals(input.getSessionId(), output.getSessionId());
	}

}
