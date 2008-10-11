package daap;

import interfaces.collection.AbstractSetCollection;
import interfaces.collection.Collection;
import interfaces.collection.Source;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DAAPClient extends AbstractSetCollection<DAAPTrack> {
	
	public static List<DAAPClient> create(String hostname, int port, int id) throws IOException {
		
		DAAPUtilities helper = new DAAPUtilities(hostname, port);
		
		final String name = helper.connect();
		System.out.println("connected to " + name + " (" + hostname + ")");
		
		final int revision = helper.update(0);
		
		List<DAAPEntry> dbs = helper.databases(revision);
		
		List<DAAPClient> collections = new ArrayList<DAAPClient>(dbs.size());
		
		for (DAAPEntry e: dbs) {
			String dbname = null;
			int dbid = 0;
			
			for (DAAPEntry a: e.children()) {
				switch (a.code()) {
				case DAAPConstants.miid: dbid = (Integer)a.value(); break;
				case DAAPConstants.minm: dbname = (String)a.value(); break;
				}
			}
			
			System.out.println(dbname);
			
			int na = dbname.hashCode();
			int hs = hostname.hashCode();
			
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
			
			if (dbname == null) throw new IOException("Database name not found (" + hostname + ")");
			
			collections.add(new DAAPClient(id++, per, revision, dbid, dbname, helper));
		}
		
		return collections;
	}
	
	private final DAAPUtilities connection;
	
	private final int dbid;
	private final String name;
	
	private int revision;

	public DAAPClient(int id, long per, int rev, int dbid, String name, DAAPUtilities connection) {
		super(id, per);
		
		this.revision = rev;
		this.dbid = dbid;
		this.name = name;
		this.connection = connection;
		
		updateTracks();
	}
	
	public String name() {
		return name;
	}
	
	public int editStatus() {
		return GENERATED;
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

	public InputStream getStream(DAAPTrack track) throws IOException {
		int song = track.id();
		return connection.song(dbid, song);
	}

	public boolean isRoot() {
		return false;
	}

	public Collection<DAAPTrack> parent() {
		return null;
	}
}
