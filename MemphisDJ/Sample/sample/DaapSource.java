package sample;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import memphis.stereo.backend.SonglistChangeListener;
import memphis.stereo.backend.impl.DatabaseSonglist;
import memphis.stereo.backend.impl.DatabaseTableInterface;
import memphis.stereo.backend.impl.SonglistImpl;
import memphis.stereo.backend.sources.Source;
import memphis.stereo.song.Song;
import memphis.stereo.songs.Song;
import memphis.stereo.sound.AudioPlayer;
import memphis.stereo.sources.Source;
import nz.ac.vuw.mcs.memphis.stereo.server.StereoLog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.SimpleLog;

import testing.DaapEntry;
import testing.DaapUtilities;

public class DaapSource extends SonglistImpl implements Source {
	
	public DaapSource(String host) throws IOException {
		
		this.hostname = host;
		this.name = host;
		
		this.log = new SimpleLog();
		this.helper = new DaapUtilities(host, this.log);
		this.songlist = new DatabaseSonglist(host) {
			public int version() {
				return version();
			}
		};
		
		this.doLogin();
		
		this.serverrevision = this.doUpdate();
		
		// TODO: remove these unnecessary steps - should get called by init
		this.retrieveDatabase();
		this.retrieveSongs();
		
		final DaapSource client = this;
		
		// Register a thread to close connection
	    Runtime.getRuntime().addShutdownHook(new Thread() {
	        public void run() {
	            client.close();
	        }
	    });
	    
	    this.isalive = true;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int version() {
		return this.serverrevision;
	}
	
	public Iterator<Song> iterator() {
		return this.songlist.iterator();
	}
	
	public void flush() {
		this.songlist.flush();
	}
	
	public void init() throws IOException {
		this.retrieveDatabase();
		this.retrieveSongs();
	}
	
	public void refresh() {
		
		try {
			int version = doUpdate();
			
			if (version == this.serverrevision) {
				this.log.debug("server revision is the same as this one, not updating");
			}
			else {
				
				this.log.debug("server revision has changed, updating");
				
				this.flush();
				this.retrieveSongs();
				
				this.serverrevision = version;
				
				this.changed();
			}
		}
		catch(IOException ex) {
			this.log.error("error retrieving version number, closing", ex);
		}
	}
	
	public void close() {
		
		if (!this.isalive) return;
		
		this.flush();
		
		this.log.debug("closing connection");
		
		try {
			doLogout();
		}
		catch (Exception ex) {
			this.log.error("Error closing connection", ex);
		}
		
		this.isalive = false;
	}
	
	public boolean isAvailable() {
		return this.isalive;
	}
	
	public synchronized void play(Song song, AudioPlayer player) {
		
		if (!this.isAvailable()) return;
		
		if (this.currentSong != null) {
			this.flushStreams();
		}
		
		DaapSong _song;
		
		if (!(song instanceof DaapSong)) {
			_song = this.songs.get(song);
		}
		else {
			_song = (DaapSong)song;
		}
		
		this.currentStream = this.helper.request(this.getHostname(), "databases/" + this.databaseId + "/items/" + _song.reference() + ".mp3?session-id=" + this.sessionid, this.log);
		this.currentSong = AudioSystem.getAudioInputStream(new BufferedInputStream(this.currentStream, 1024));
		
		player.play(this.currentSong);

	}
	
	public String getHostname() {
		return this.hostname;
	}
	
	public synchronized void flushStreams() {
		
		this.log.debug("daap client: stopping songs");
		
		if (this.currentSong != null) {
			try {
				this.currentSong.close();
				this.log.debug("daap client: stream closed");
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
			this.helper.release(this.currentStream);
			this.currentSong = null;
			this.currentStream = null;
		}
	}
	
	DatabaseTableInterface database() {
		return this.songlist;
	}

	private void doLogin() throws IOException {
		
		InputStream stream = this.helper.request(this.getHostname(), "login", this.log);

		DaapEntry entry = DaapEntry.parseStream(stream, this.helper.types); 
		this.log.debug("entry: " + this.helper.names.get(entry.getName()));
		
		this.sessionid = (Integer)entry.getValueMap().get(DaapUtilities.stringToInt("mlid"));

		this.log.debug("session id: " + this.sessionid);

		this.helper.release(stream);
	}
	
	private int doUpdate() throws IOException {
		
		InputStream stream = this.helper.request(this.getHostname(), "update?session-id="+this.sessionid, this.log);

		DaapEntry entry = DaapEntry.parseStream(stream, this.helper.types);

		int version = (Integer)entry.getValueMap().get(DaapUtilities.stringToInt("musr"));

		this.log.debug("server revision: " + version);

		this.helper.release(stream);
		
		return version;
	}
	
	private void doLogout() throws IOException {	
		
		this.helper.release(this.helper.request(this.getHostname(), "logout?session-id="+this.sessionid, this.log));

		this.log.info("daap client: logged out");
	}
	
	/*
	 * Methods for creating databases
	 */
	
	private void retrieveDatabase() throws IOException {
		
		this.log.debug("daap-client: retrieving database information");
		
		InputStream stream = this.helper.request(this.getHostname(), "databases?session-id="+this.sessionid+"&revision-id="+this.serverrevision, this.log);

		DaapEntry entry = DaapEntry.parseStream(stream, this.helper.types);

		if ((entry == null) || (entry.getName() != DaapUtilities.stringToInt("avdb"))) {
			this.log.error(DaapUtilities.intToString(entry.getName()));
			throw new IOException("'" + DaapUtilities.intToString(entry.getName()) + "'");
		}

		for (DaapEntry e: entry) {
			if (e.getName() == DaapUtilities.stringToInt("mlcl")) {
				entry = e;
				break;
			}
		}

		DaapEntry e = entry.iterator().next();

		Map<Integer, Object> entries = e.getValueMap();

		int id = (Integer)entries.get(DaapUtilities.stringToInt("miid"));
		String name = (String)entries.get(DaapUtilities.stringToInt("minm"));
		int items = (Integer)entries.get(DaapUtilities.stringToInt("mimc"));
		int playlists = (Integer)entries.get(DaapUtilities.stringToInt("mctc"));

		this.log.debug("database " + name + " has " + items + " songs and " + playlists + " playlists.");

		this.databaseId = id;
		this.name = name;
		
		this.helper.release(stream);
	}
	
	private void retrieveSongs() throws IOException {

		this.log.debug("daap-client: updating songs for " + this.getName());
		
		InputStream stream = this.helper.request(this.getHostname(), "databases/" + this.databaseId + "/items?type=music&meta=dmap.itemkind,dmap.itemid,dmap.itemname,daap.songalbum,daap.songartist,daap.songgenre,daap.songcomposer,daap.songbitrate,daap.songsamplerate,daap.songtime&session-id="+this.sessionid+"&revision-number=" + this.serverrevision, this.log);

		DaapEntry entry = DaapEntry.parseStream(stream, this.helper.types);

		this.log.debug("daap-client: retrieved data.");

		if ((entry == null) || (entry.getName() != DaapUtilities.stringToInt("adbs"))) {
			this.log.error(DaapUtilities.intToString(entry.getName()));
			throw new IOException("'" + entry.getName() + "'");
		}

		for (DaapEntry e: entry) {
			if (e.getName() == DaapUtilities.stringToInt("mlcl")) {
				entry = e;
				break;
			}
		}

		SongFactory factory = new SongFactory(this);

		for (DaapEntry e: entry) {
			if ((entry == null) || !entry.hasChildren()) {
				continue;
			}
			DaapSong song = factory.constructSong(e);
			if (song != null && !this.songs.containsKey(song)) {
				this.songs.put(song, song);
			}
		}
		
		List<Song> songlist = new Vector<Song>();
		songlist.addAll(this.songs.keySet());
		
		this.songlist.flush();
		this.songlist.add(songlist);
		
		this.helper.release(stream);
	}

	private final Log log;
	
	private final DatabaseSonglist songlist;
	private final Map<Song, DaapSong> songs = new HashMap<Song, DaapSong>();
	
	private final String hostname;
	private String name;
	
	private DaapUtilities helper;

	private int sessionid;
	private int serverrevision;
	private int databaseId;
	
	private boolean isalive;
	
	private InputStream currentStream;
	private AudioInputStream currentSong;
}
