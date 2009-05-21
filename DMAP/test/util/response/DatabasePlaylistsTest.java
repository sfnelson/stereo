package util.response;

import static org.junit.Assert.assertEquals;
import interfaces.Track;
import interfaces.collection.Collection;

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
import dmap.node.PlaylistNode;
import dmap.response.databases.Playlists;

public class DatabasePlaylistsTest {

	private Playlists input;
	private Playlists output;

	@Before
	public void setUp() throws Exception {

		Collection<? extends Track> c = new test.Playlist(1, 2, Collection.EDITABLE, "test", 3, true, 15);
		Collection<? extends Track> d = new test.Playlist(4, 5, Collection.GENERATED, "test2", 6, true, 10);
		
		List<PlaylistNode> list = new ArrayList<PlaylistNode>();
		list.add(new PlaylistNode(c));
		list.add(new PlaylistNode(d));
		
		input = new Playlists(list);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Writer w = new DACPWriter(out);
		w.appendNode(input);

		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		Reader r = new DACPReader(in, in.available());

		output = Playlists.read(r.nextComposite(r.iterator().next()), new test.Playlist.PlaylistFactory());
	}

	@Test
	public void testPlaylists() {
		
		Iterator<PlaylistNode> a = input.playlists().iterator();
		Iterator<PlaylistNode> b = output.playlists().iterator();
		
		while (a.hasNext() && b.hasNext()) {
			Collection<? extends Track> pa = a.next().collection();
			Collection<? extends Track> pb = b.next().collection();
			
			assertEquals(pa.name(), pb.name());
			assertEquals(pa.parent().id(), pb.parent().id());
			assertEquals(pa.isRoot(), pb.isRoot());
			assertEquals(pa.size(), pb.size());
			assertEquals(pa.editStatus(), pb.editStatus());
			
		}
		
		assertEquals(a.hasNext(), b.hasNext());
	}

}
