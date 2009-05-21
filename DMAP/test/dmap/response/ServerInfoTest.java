package dmap.response;


import static org.junit.Assert.assertEquals;
import interfaces.Constants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;

import api.Reader;
import api.Writer;
import dmap.DACPReader;
import dmap.DACPWriter;
import dmap.response.ServerInfo;

public class ServerInfoTest {

	private ServerInfo input;
	private ServerInfo output;

	@Before
	public void setUp() throws Exception {

		input = new ServerInfo("Test Server", 15);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Writer w = new DACPWriter(out);
		w.appendNode(input);

		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		Reader r = new DACPReader(in, in.available());

		output = ServerInfo.read(r.nextComposite(r.iterator().next()));
	}

	@Test
	public void testServerInfo() {
		for (Constants c: input.getAllTags()) {
			switch (c.type) {
			case Constants.VERSION:
				byte[] a = (byte[])input.get(c);
				byte[] b = (byte[])output.get(c);
				for (int i = 0; i < 4; i++) {
					assertEquals(a[i], b[i]);
				}
				break;
			default:
				assertEquals(input.get(c), output.get(c));	
			}
		}
	}

}
