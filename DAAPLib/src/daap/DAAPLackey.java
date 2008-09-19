package daap;

import interfaces.Album;
import interfaces.Lackey;
import interfaces.LackeyCreator;
import interfaces.Playlist;
import interfaces.Track;
import interfaces.TrackSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import music.DJ;
import notification.AbstractEventGenerator;
import notification.LibraryListener;
import notification.TrackSourceListener;
import daap.DAAPClient.ClientExpiredException;


public class DAAPLackey extends AbstractEventGenerator<LibraryListener> implements interfaces.Lackey {
	
	private final Map<DAAPClient, DAAPPlaylist> library;
	
	private int playlists = 2; //number of playlists created
	//1 is 'all' playlist
	//2 is 'shuffle' playlist
	
	private Set<DAAPTrack> tracks = new HashSet<DAAPTrack>();
	private List<Track> tracklist = new ArrayList<Track>();
	
	private Map<String, Map<String, DAAPAlbum>> allAlbums = new HashMap<String, Map<String, DAAPAlbum>>();
	
	//private Set<DAAPAlbum> albums = new HashSet<DAAPAlbum>();
	private List<DAAPAlbum> albumlist = new ArrayList<DAAPAlbum>();
	
	private int version = 1;
	
	private LackeyPlaylist playlist = new LackeyPlaylist(this);
	
	public DAAPLackey() {
		
		library = new HashMap<DAAPClient, DAAPPlaylist>();
		
		Handshake hs;
		try {
			hs = new Handshake(this);
			hs.start();
		} catch (IOException e) {
			System.out.println("server socket failed to initialise");
			//e.printStackTrace();
			System.exit(1);
		}
		
		new WorkerThread(this).start();
	}
	
	public List<Track> getLibrary() {
		return tracklist;
	}
	
	public List<? extends Album> getAlbums() {
		return albumlist;
	}
	
	public List<Playlist<? extends Track>> getPlaylists() {
		List<Playlist<? extends Track>> playlists = new ArrayList<Playlist<? extends Track>>();
		playlists.add(this.playlist);
		for (DAAPPlaylist p: library.values()) {
			playlists.add(p);
		}
		return playlists;
	}
	
	public void newConnection(DAAPClient client) {
		worklist.add(new NewConnectionJob(this, client));
	}
	
	public int version() {
		return version;
	}
	
	//internal stuff
	private Queue<Job> worklist = new LinkedList<Job>();
	
	private static class WorkerThread extends Thread {
		private final DAAPLackey lackey;
		public WorkerThread(DAAPLackey lackey) {
			super("Lackey.WorkerThread");
			this.lackey = lackey;
		}
		
		public void run() {
			while (true) {
				
				lackey.worklist.offer(new ServerPollJob(lackey));
				
				while (!lackey.worklist.isEmpty()) {
					Job job = lackey.worklist.poll();
					//System.out.println(job.getClass());
					job.run();
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					//swallow
				}
			}
		}
		
	}
	
	private interface Job {
		public void run();
	}
	
	private static class NewConnectionJob implements Job {
		private final DAAPClient client;
		private final DAAPLackey lackey;
		public NewConnectionJob(DAAPLackey lackey, DAAPClient client) {
			this.client = client;
			this.lackey = lackey;
		}
		public void run() {
			if (client == null) 	return;
			DAAPPlaylist tracks = new DAAPPlaylist(client.getName(), client.id(), client.getPersistantId(), null);
			client.getTracks(tracks);
			System.out.println("Tracks retrieved: " + tracks.size());
			lackey.library.put(client, tracks);
			lackey.worklist.offer(new RebuildTracklistJob(lackey));
			System.out.println("Tracks and client added to library");
			lackey.worklist.offer(new TracksAddedJob(lackey));
		}
	}

	private static class TracksAddedJob implements Job {
		private final DAAPLackey lackey;
		public TracksAddedJob(DAAPLackey lackey) {
			this.lackey = lackey;
		}
		public void run() {
			lackey.trackSource.notifyTracksAdded();
			System.out.println("DJ Informed");
		}
	}
	
	private static class LibraryChangedJob implements Job {
		private final DAAPLackey lackey;
		public LibraryChangedJob(DAAPLackey lackey) {
			this.lackey = lackey;
		}
		public void run() {
			lackey.trackSource.notifyTracksRemoved(lackey.tracks);
			for (LibraryListener dj: lackey.listeners()) {
				dj.libraryVersionChanged(lackey.version);
			}
			System.out.println("DJ Informed");
		}
	}
	
	private static class RebuildTracklistJob implements Job {
		
		private final DAAPLackey lackey;
		
		public RebuildTracklistJob(DAAPLackey lackey) {
			this.lackey = lackey;
		}
		
		public void run() {
			Set<DAAPTrack> tracks = new HashSet<DAAPTrack>();
			List<Track> tracklist = new ArrayList<Track>();
			Set<DAAPAlbum> albums = new HashSet<DAAPAlbum>();
			List<DAAPAlbum> albumList = new ArrayList<DAAPAlbum>();
			
			for(List<DAAPTrack> tks : lackey.library.values()){
				for (DAAPTrack track: tks) {
					String artist = (String)track.getTag(DAAPConstants.ARTIST);
					String album = (String)track.getTag(DAAPConstants.ALBUM);
					
					if (artist == null) artist = "";
					if (album == null) album = "";
					
					if (!lackey.allAlbums.containsKey(artist)) {
						lackey.allAlbums.put(artist, new HashMap<String, DAAPAlbum>());
					}
					
					DAAPAlbum al = lackey.allAlbums.get(artist).get(album);
					if (al == null) {
						al = DAAPAlbum.createAlbum(album, artist);
						lackey.allAlbums.get(artist).put(album, al);
					}
					
					if (albums.contains(al)) {
						al.setItems(al.getItems()+1);
					}
					else {
						al.setItems(1);
						albums.add(al);
					}
					
					if (track.getAlbum() != al) {
						track.setAlbum(al);
					}
					
					tracks.add(track);
				}
			}
			
			tracklist.addAll(tracks);
			albumList.addAll(albums);
			
			synchronized (lackey) {
				lackey.tracks = tracks;
				lackey.tracklist = tracklist;
				//lackey.albums = albums;
				lackey.albumlist = albumList;
				lackey.version++;
			}
			
			lackey.worklist.offer(new LibraryChangedJob(lackey));
		}
	}

	private static class Handshake extends Thread {

		private ServerSocket connection;

		private final int DAAPPORT = 3689;

		private final int PORT = 8080;

		private BufferedReader netInput = null;

		private DAAPLackey lackey;

		public Handshake(DAAPLackey l) throws IOException {
			super("Lackey.Handshake");
			connection = new ServerSocket(PORT);
			this.lackey = l;
		}

		
		private DAAPClient createConnection() throws IOException {
			Socket client = null;
			try {
				client = connection.accept();
				netInput = new BufferedReader(new InputStreamReader(client
						.getInputStream()));

				String line = netInput.readLine();
				if (line.startsWith("GET /")) {
					// TODO consider allowing the client to specify the address
					// TODO allow the client to specify a port
					String DAAPServer = client.getInetAddress().getHostAddress();
					return new DAAPClient(DAAPServer, DAAPPORT, ++lackey.playlists);
				} else {
					return null;
				}
			} finally {
				if (client != null) {
					client.close();
				}
			}
		}

		public void run() {
			while (true) {
				DAAPClient client = null;
				try {
					client = createConnection();
					System.out.println("Lackey accepted connection.");
					lackey.newConnection(client);
					System.out.println("Lackey created connection.");
				} catch (Exception e) {
					System.err.println("Error creating connection");
					//e.printStackTrace();
				}
			}
		}
	}
	
	private static class ServerPollJob implements Job {
		private final DAAPLackey lackey;
		public ServerPollJob(DAAPLackey lackey) {
			this.lackey = lackey;
		}
		public void run(){
			List<DAAPClient> expired = new ArrayList<DAAPClient>();

			for(DAAPClient client:lackey.library.keySet()){
				try {
					if(client.isUpdated()){
						System.out.println("client changed");
						DAAPPlaylist list = lackey.library.get(client);
						client.getTracks(list);
						lackey.worklist.offer(new RebuildTracklistJob(lackey));
					}
				} catch (ClientExpiredException e) {
					expired.add(client);
				}
			}

			if(expired.size() > 0){
				for(DAAPClient c:expired){
					lackey.library.remove(c);
				}
				lackey.worklist.offer(new RebuildTracklistJob(lackey));
			}
		}
	}
	

	public static void register() {
		DJ.setLackeyCreator(new DAAPLackeyCreator());
	}
	
	private static class DAAPLackeyCreator implements LackeyCreator {
		public Lackey create() {
			DAAPLackey lackey = new DAAPLackey();
			return lackey;
		}
	}

	private static class LackeyPlaylist extends AbstractList<DAAPTrack> implements Playlist<DAAPTrack> {

		private final DAAPLackey lackey;

		public LackeyPlaylist(DAAPLackey lackey) {
			this.lackey = lackey;
		}

		public int id() {
			return 1;
		}

		public String name() {
			return "All Songs";
		}

		public Playlist<Track> parent() {
			return null;
		}

		public long persistantId() {
			return 1;
		}

		public boolean add(DAAPTrack o) {
			throw new RuntimeException("unimplemented for this type");
		}

		public void clear() {
			throw new RuntimeException("unimplemented for this type");
		}

		public boolean contains(Object o) {
			return lackey.tracks.contains(o);
		}

		public DAAPTrack get(int index) {
			int i = 0;
			for (DAAPTrack t: this) {
				if (i == index) return t;
			}
			return null;
		}

		public Iterator<DAAPTrack> iterator() {
			return lackey.tracks.iterator();
		}

		public boolean remove(Object o) {
			throw new RuntimeException("unimplemented for this type");
		}

		public DAAPTrack set(int index, Track element) {
			throw new RuntimeException("unimplemented for this type");
		}

		public int size() {
			return lackey.tracks.size();
		}

		public boolean isRoot() {
			return true;
		}
	}


	public TrackSource trackSource() {
		return trackSource;
	}
	
	private LackeyTrackSource trackSource = new LackeyTrackSource(this);
	
	private static class LackeyTrackSource extends AbstractEventGenerator<TrackSourceListener> implements TrackSource {

		private final DAAPLackey lackey;
		
		public LackeyTrackSource(DAAPLackey lackey) {
			this.lackey = lackey;
		}
		
		public void notifyTracksAdded() {
			for (TrackSourceListener l: listeners()) {
				l.tracksAvailable();
			}
		}
		
		public void notifyTracksRemoved(Set<? extends Track> available) {
			for (TrackSourceListener l: listeners()) {
				l.tracksUnavailable(available);
			}
		}
		
		public void checkPlaylist(List<Track> check) {
			for (Iterator<Track> iter = check.iterator(); iter.hasNext();) {
				Track element = iter.next();
				if(!lackey.tracks.contains(element)){
					iter.remove();
				}
			}
		}

		public boolean hasNextTrack() {
			return !lackey.tracks.isEmpty();
		}

		public Track nextTrack() {
			int size = lackey.tracklist.size();
			if (size == 0) return null;
			
			int pos = ((int)(Math.random()*size))%size;
			return lackey.tracklist.get(pos);
		}
		
	}

}
