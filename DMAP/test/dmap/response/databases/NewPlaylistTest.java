package dmap.response.databases;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;

import api.Reader;
import api.Writer;
import dmap.DACPReader;
import dmap.DACPWriter;
import dmap.response.databases.NewPlaylist;

public class NewPlaylistTest {

	private NewPlaylist input;
	private NewPlaylist output;

	@Before
	public void setUp() throws Exception {
		input = new NewPlaylist(15);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Writer w = new DACPWriter(out);
		w.appendNode(input);

		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		Reader r = new DACPReader(in, in.available());

		output = NewPlaylist.read(r.nextComposite(r.iterator().next()));
	}

	@Test
	public void testId() {
		assertEquals(input.id(), output.id());
	}

}
