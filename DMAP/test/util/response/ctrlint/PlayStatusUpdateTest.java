package util.response.ctrlint;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;

import api.Reader;
import api.Writer;
import dmap.DACPReader;
import dmap.DACPWriter;
import dmap.response.ctrlint.PlayStatusUpdate;

public class PlayStatusUpdateTest {

	private PlayStatusUpdate input;
	private PlayStatusUpdate output;

	@Before
	public void setUp() throws Exception {

		input = new PlayStatusUpdate.Active(15, PlayStatusUpdate.Status.PAUSED, true, 0, 1, 3, 9, 21, "Title", "Artist", "Album", "Genre", 123456789l, 0, 90, 180);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Writer w = new DACPWriter(out);
		w.appendNode(input);

		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		Reader r = new DACPReader(in, out.size());

		output = PlayStatusUpdate.read(r.nextComposite(r.iterator().next()));
	}

	@Test
	public void testPlayStatusUpdate() {
		assertEquals(15, output.revision);
		assertEquals(PlayStatusUpdate.Status.PAUSED, output.state);
		assertEquals(true, output.shuffle);
		assertEquals(0, output.repeat);
	}
	
	@Test
	public void testPlayStatusUpdateActive() {
		PlayStatusUpdate.Active output = this.output.active();
		
		assertEquals(15, output.revision);
		assertEquals(PlayStatusUpdate.Status.PAUSED, output.state);
		assertEquals(true, output.shuffle);
		assertEquals(0, output.repeat);
		
		assertEquals(1, output.currentDatabase);
		assertEquals(3, output.currentPlaylist);
		assertEquals(9, output.currentPosition);
		assertEquals(21, output.currentTrackId);
		
		assertEquals("Title", output.trackTitle);
		assertEquals("Artist", output.trackArtist);
		assertEquals("Album", output.trackAlbum);
		assertEquals("Genre", output.trackGenre);
		
		assertEquals(123456789l, output.currentAlbumId);
		assertEquals(0, output.mediaKind);
		assertEquals(90, output.remainingTime);
		assertEquals(180, output.totalTime);
		
	}

}
