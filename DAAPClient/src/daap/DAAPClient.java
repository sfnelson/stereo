package daap;

import interfaces.Track;
import interfaces.collection.AbstractSetSource;
import interfaces.collection.Collection;
import interfaces.collection.ConcreteCollection;
import interfaces.collection.Source;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DAAPClient extends AbstractSetSource<DAAPTrack>
		implements Source<DAAPTrack> {
	
	public static List<DAAPClient> create(String path, int id) throws IOException {
		
		DAAPUtilities helper = new DAAPUtilities(path);
		
		try {
			final String name = helper.connect();
			System.out.println("connected to " + name + " (" + path + ")");
		}
		catch (IOException ex) {
			System.err.println("error connecting to " + path + ": " + ex.getMessage());
			throw ex;
		}
		
		final int revision = helper.update(0);
		
		List<DAAPEntry> dbs = helper.databases(revision);
		
		List<DAAPClient> collections = new ArrayList<DAAPClient>(dbs.size());
		
		for (DAAPEntry e: dbs) {
			String dbname = null;
			int dbid = 0;
			
			for (DAAPEntry a: e.children()) {
				switch (a.code()) {
				case dmap_itemid: dbid = (Integer)a.value(); break;
				case dmap_itemname: dbname = (String)a.value(); break;
				}
			}
			
			System.out.println(dbname);
			
			int na = dbname.hashCode();
			int hs = path.hashCode();
			
			byte[] persistant = new byte[8];
			persistant[0] = (byte)(na>>24 & 0xFF);
			persistant[1] = (byte)(na>>16 & 0xFF);
			persistant[2] = (byte)(na>>8  & 0xFF);
			persistant[3] = (byte)(na	  & 0xFF);
			persistant[4] = (byte)(hs>>24 & 0xFF);
			persistant[5] = (byte)(hs>>16 & 0xFF);
			persistant[6] = (byte)(hs>>8  & 0xFF);
			persistant[7] = (byte)(hs	  & 0xFF);

			long per = new BigInteger(persistant).longValue();
			
			if (dbname == null) throw new IOException("Database name not found (" + path + ")");
			
			collections.add(new DAAPClient(id++, per, revision, dbid, dbname, helper));
		}
		
		return collections;
	}
	
	private final DAAPUtilities connection;
	private final int dbid;
	private final Collection<DAAPTrack> collection;
	
	private int revision;

	public DAAPClient(int id, long per, int rev, int dbid, String name, DAAPUtilities connection) {

		this.revision = rev;
		this.dbid = dbid;
		this.connection = connection;
		
		collection = new ConcreteCollection<DAAPTrack>(id, per, name, Collection.GENERATED, false, null, 0, this) {
			public int size() {
				return _size();
			}
		};
		
		updateTracks();
	}
	
	private int _size() {
		return size();
	}
	
	public Collection<DAAPTrack> collection() {
		return collection;
	}
	
	public void update() throws IOException {

		int rev = connection.update(revision);

		if (rev > revision) {
			updateTracks();
		}
	}
	
	public void close() {
		
		List<DAAPTrack> tracks = this.store.tracks();
		Set<DAAPTrack> trackSet = new HashSet<DAAPTrack>(tracks);
		
		for (Source.Listener l: this.listeners()) {
			l.removed(trackSet);
		}
	}

	private void updateTracks() {
		
		List<DAAPEntry> tracks;
		
		try {
			tracks = connection.tracks(dbid, revision);
		}
		catch (IOException ex) {
			System.err.println("Unable to get track list");
			ex.printStackTrace();
			return;
		}
		
		List<DAAPTrack> added = new ArrayList<DAAPTrack>();
		Set<DAAPTrack> update = new HashSet<DAAPTrack>();
		for (DAAPEntry e : tracks) {
			DAAPTrack track = DAAPTrack.create(e, this);
			if (track != null) {
				update.add(track);
				if (!this.store.contains(track)) {
					added.add(track);
				}
			}
		}
		
		Set<DAAPTrack> removed = new HashSet<DAAPTrack>();
		for (DAAPTrack t: this.store.tracks()) {
			if (!update.contains(t)) {
				removed.add(t);
			}
		}
		
		for (DAAPTrack t: removed) {
			this.store.remove(t);
		}
		
		for (DAAPTrack t: added) {
			this.store.add(t);
		}
		
		for (Source.Listener l: this.listeners()) {
			l.added(added);
			l.removed(removed);
		}
		
	}

	public void readStream(DAAPTrack track, Track.StreamReader reader) throws IOException {
		int song = track.id();
		connection.readSong(dbid, song, reader);
	}
}
