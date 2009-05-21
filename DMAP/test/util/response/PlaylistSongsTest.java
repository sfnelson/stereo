package util.response;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import interfaces.Track;

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
import dmap.node.TrackNode;
import dmap.response.PlaylistSongs;

public class PlaylistSongsTest {
	
	private PlaylistSongs input;
	private PlaylistSongs output;

	@Before
	public void setUp() throws Exception {
		
		List<? extends Track> tracks = new ArrayList<Track>();
		input = new PlaylistSongs(tracks);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Writer w = new DACPWriter(out);
		w.appendNode(input);

		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		//StreamPrinter.print(in);
		Reader r = new DACPReader(in, in.available());

		output = PlaylistSongs.read(r.nextComposite(r.iterator().next()), new test.Track.TrackFactory());
	}
	
	@Test
	public void testDatabases() {
		Iterator<TrackNode> in = input.tracks().iterator();
		Iterator<TrackNode> out = output.tracks().iterator();
		while (in.hasNext() && out.hasNext()) {
			TrackNode a = in.next();
			TrackNode b = out.next();
			assertEquals(a.track, b.track);
		}
		
		assertTrue(in.hasNext() == out.hasNext());
	}

	@Test
	public void testSize() {
		assertEquals(input.size(), output.size());
	}

}
