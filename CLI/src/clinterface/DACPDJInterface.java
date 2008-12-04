package clinterface;

import interfaces.Album;
import interfaces.DJInterface;
import interfaces.Library;
import interfaces.PlaybackControl;
import interfaces.PlaybackStatus;
import interfaces.VolumeControl;
import interfaces.collection.Collection;
import interfaces.collection.Source;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import music.Track;
import notification.LibraryListener;
import notification.PlaybackListener;
import reader.DACPResponseParser;
import util.DACPConstants;
import util.node.BooleanNode;
import util.node.ByteNode;
import util.node.Composite;
import util.node.IntegerNode;
import util.node.LongLongNode;
import util.node.LongNode;
import util.node.Node;
import util.node.StringNode;
import daap.DAAPConstants;

public class DACPDJInterface implements DJInterface {
	
	private final DACPResponseParser parser;
	private final String host;
	private final int port;
	
	public DACPDJInterface(String host, int port) {
		this.parser = new DACPResponseParser();
		this.host = host;
		this.port = port;
	}
	
	private Composite request(String request) {
		try {
			InputStream is = DACPResponseParser.request(host, port, request);
			return parser.parse(is);
		} catch (UnknownHostException e) {
			System.err.println("Unable to connect to " + host);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Problem contacting " + host);
			e.printStackTrace();
		}
		return null;
	}
	
	private CLITrack parseStatusTrack(Composite c) {
		
		int id = 0;
		long persistent = 0;
		
		for (Node n : c.nodes) {
			switch (n.code) {
			case DACPConstants.canp:
				LongLongNode lln = (LongLongNode)n;
				id = (int)lln.getValues()[3]; //drops top types - last int field is id
				break;
			}
		}
		
		CLITrack t = new CLITrack(id, persistent);
		
		for (Node n : c.nodes) {
			switch (n.code) {
			case DACPConstants.cann:
				t.put(DACPConstants.NAME, ((StringNode)n).getValue());
				break;
			case DACPConstants.cana:
				t.put(DACPConstants.ARTIST, ((StringNode)n).getValue());
				break;
			case DACPConstants.canl:
				t.put(DACPConstants.ALBUM, ((StringNode)n).getValue());
				break;
			case DACPConstants.cang:
				t.put(DACPConstants.GENRE, ((StringNode)n).getValue());
				break;
			}
		}
		
		return t;
	}
	
	private CLITrack parsePlaylistTrack(Composite c) {
		
		int id = 0;
		long persistent = 0;
		
		for (Node n : c.nodes) {
			switch (n.code) {
			case DAAPConstants.miid:
				id = (int)(((IntegerNode)n).getValue());
				break;
			case DAAPConstants.mper:
				persistent = (long)(((LongNode)n).getValue());
				break;
			}
		}
		
		CLITrack t = new CLITrack(id, persistent);
		
		for (Node n : c.nodes) {
			switch (n.code) {
			case DAAPConstants.NAME:
				t.put(DACPConstants.NAME, ((StringNode)n).getValue());
				break;
			case DAAPConstants.ARTIST:
				t.put(DACPConstants.ARTIST, ((StringNode)n).getValue());
				break;
			case DAAPConstants.ALBUM:
				t.put(DACPConstants.ALBUM, ((StringNode)n).getValue());
				break;
			case DAAPConstants.GENRE:
				t.put(DACPConstants.GENRE, ((StringNode)n).getValue());
				break;
			}
		}
		return t;
	}
	
	private CLIPlaylist parsePlaylist(Composite c) {
		CLIPlaylist pl = new CLIPlaylist(0, 0, "current");
		for (Node n : c.nodes) {
			if (n.code == DACPConstants.mlcl) {
				Composite list = (Composite)n;
				for (Node i : list.nodes) {
					pl.add(parsePlaylistTrack((Composite)i));
				}
			}
		}
		return pl;
	}
	
	private List<CLIPlaylist> parsePlaylists(Composite c) {
		List<CLIPlaylist> plist = new ArrayList<CLIPlaylist>();
		for (Node n : c.nodes) {
			if (n.code == DACPConstants.mlcl) {
				Composite list = (Composite)n;
				for (Node i : list.nodes) {
					plist.add(parsePlaylistDetails((Composite)i));
				}
			}
		}
		return plist;
	}
	
	private CLIPlaylist parsePlaylistDetails(Composite c) {
		
		int id = 0;
		long persistent = 0;
		String name = null;
		
		for (Node n : c.nodes) {
			switch (n.code) {
			case DAAPConstants.miid: id = ((IntegerNode)n).getValue(); break;
			case DAAPConstants.mper: persistent = ((LongNode)n).getValue(); break;
			case DAAPConstants.minm: name = ((StringNode)n).getValue(); break;
			}
		}
		
		CLIPlaylist pl = new CLIPlaylist(id, persistent, name);
		
		for (Node n : c.nodes) {
			switch (n.code) {
			case DAAPConstants.abpl: pl.setRoot(((BooleanNode)n).getValue()); break;
			case DAAPConstants.mpco: pl.setParent(((IntegerNode)n).getValue()); break;
			case DAAPConstants.mimc: pl.specifySize(((IntegerNode)n).getValue()); break;
			}
		}
		return pl;
	}
	
	private List<CLIAlbum> parseAlbums(Composite c) {
		List<CLIAlbum> albums = new ArrayList<CLIAlbum>();
		for (Node n : c.nodes) {
			if (n.code == DACPConstants.mlcl) {
				Composite list = (Composite)n;
				for (Node i : list.nodes) {
					albums.add(parseAlbum((Composite)i));
				}
			}
		}
		return albums;
	}
	
	private CLIAlbum parseAlbum(Composite c) {
		CLIAlbum al = new CLIAlbum();
		for (Node n : c.nodes) {
			switch (n.code) {
			case DAAPConstants.miid: al.put(n.code, ((IntegerNode)n).getValue()); break;
			case DAAPConstants.mper: al.put(n.code, ((LongNode)n).getValue()); break;
			case DAAPConstants.minm: al.put(DACPConstants.ALBUM, ((StringNode)n).getValue()); break;
			case DAAPConstants.asaa: al.put(DACPConstants.ARTIST, ((StringNode)n).getValue()); break;	
			}
		}
		return al;
	}
	
	public Library<CLITrack> library() {
		return this.library;
	}

	public PlaybackControl playbackControl() {
		return this.control;
	}

	public VolumeControl volume() {
		return this.volume;
	}

	public PlaybackStatus playbackStatus() {
		return this.status;
	}
	
	private Library<CLITrack> library = new Library<CLITrack>() {

		public boolean addCollection(Collection<? extends Track> collection) {
			throw new RuntimeException("should not be called on cli");
		}

		public boolean addSource(Source<? extends Track> source) {
			throw new RuntimeException("should not be called on cli");
		}

		public Iterable<? extends Album> albums() {
			// TODO Auto-generated method stub
			return null;
		}

		public Iterable<Collection<? extends Track>> collections() {
			// TODO Auto-generated method stub
			return null;
		}

		public int numAlbums() {
			// TODO Auto-generated method stub
			return 0;
		}

		public int numCollections() {
			// TODO Auto-generated method stub
			return 0;
		}

		public void registerLibraryListener(LibraryListener listener) {
			// TODO Auto-generated method stub
			
		}

		public boolean removeCollection(Collection<? extends Track> collection) {
			// TODO Auto-generated method stub
			return false;
		}

		public void removeLibraryListener(LibraryListener listener) {
			// TODO Auto-generated method stub
			
		}

		public boolean removeSource(Source<? extends Track> source) {
			// TODO Auto-generated method stub
			return false;
		}

		public int version() {
			// TODO Auto-generated method stub
			return 0;
		}

		public boolean isRoot() {
			// TODO Auto-generated method stub
			return false;
		}

		public String name() {
			// TODO Auto-generated method stub
			return null;
		}

		public Collection<CLITrack> parent() {
			// TODO Auto-generated method stub
			return null;
		}

		public int size() {
			// TODO Auto-generated method stub
			return 0;
		}

		public Object get(int tagID) {
			// TODO Auto-generated method stub
			return null;
		}

		public Iterable<Integer> getAllTags() {
			// TODO Auto-generated method stub
			return null;
		}

		public int id() {
			// TODO Auto-generated method stub
			return 0;
		}

		public long persistentId() {
			// TODO Auto-generated method stub
			return 0;
		}

		public boolean hasNext() {
			// TODO Auto-generated method stub
			return false;
		}

		public CLITrack next() {
			// TODO Auto-generated method stub
			return null;
		}

		public Iterable<? extends Track> tracks() {
			// TODO Auto-generated method stub
			return null;
		}

		public void registerListener(
				interfaces.collection.Source.Listener listener) {
			// TODO Auto-generated method stub
			
		}

		public void removeListener(
				interfaces.collection.Source.Listener listener) {
			// TODO Auto-generated method stub
			
		}

		public Iterator<CLITrack> iterator() {
			// TODO Auto-generated method stub
			return null;
		}
		/*
		public List<? extends Album> getAlbums() {
			Composite c = request("/databases/1/browse/artists?filter=('daap.songartist!:')");

			if (c == null) return null;
			
			return parseAlbums(c);
		}

		public CLIPlaylist getLibrary() {			
			Composite c = request("/databases/1/containers/1/items");

			if (c == null) return null;
			
			return parsePlaylist(c);
		}

		public List<? extends Collection<? extends Track>> getPlaylists() {
			Composite c = request("/databases/1/containers");

			if (c == null) return null;
			
			return parsePlaylists(c);
		}

		public int version() {
			Composite c = request("/update");

			if (c == null) return 0;
			
			for (Node n : c.nodes) {
				if (n.code == DACPConstants.musr) {
					return ((IntegerNode)n).getValue();
				}
			}
			
			return 0;
		}

		public void registerListener(LibraryListener listener) {}
		public void removeListener(LibraryListener listener) {}
		*/

		public int nextCollectionId() {
			// TODO Auto-generated method stub
			return 0;
		}

		public int editStatus() {
			// TODO Auto-generated method stub
			return 0;
		}
	};
	
	private PlaybackStatus status = new PlaybackStatus() {

		public Track current() {
			Composite c = request("/ctrl-int/1/playstatusupdate");

			if (c == null) return null;

			CLITrack t = parseStatusTrack(c);
			
			return t;
		}

		public int elapsedTime() {
			Composite c = request("/ctrl-int/1/playstatusupdate");

			if (c == null) return -1;
			
			for (Node n : c.nodes) {
				if (n.code == DACPConstants.cant) {
					return ((IntegerNode)n).getValue();
				}
			}
			
			return -1;
		}

		public byte[] getAlbumArt() {
			// TODO Auto-generated method stub
			return null;
		}

		public Collection<? extends Track> playlist() {
			Composite c = request("/ctrl-int/1/playlist");

			if (c == null) return null;
			
			return parsePlaylist(c);
		}

		public byte state() {
			Composite c = request("/ctrl-int/1/playstatusupdate");

			if (c == null) return -1;
			
			for (Node n : c.nodes) {
				if (n.code == DACPConstants.caps) {
					return ((ByteNode)n).getValue();
				}
			}
			
			return -1;
		}

		public int position() {
			Composite c = request("/ctrl-int/1/playstatusupdate");

			if (c == null) return -1;
			
			for (Node n : c.nodes) {
				if (n.code == DACPConstants.canp) {
					return ((LongLongNode)n).getValues()[2];
				}
			}
			
			return -1;
		}
		
	};
	
	private VolumeControl volume = new VolumeControl() {

		public int getVolume() {
			Composite c = request("/ctrl-int/1/getproperty?properties=dmcp.volume");

			if (c == null) return -1;
			
			for (Node n : c.nodes) {
				if (n.code == DACPConstants.cmvo) {
					return ((IntegerNode)n).getValue();
				}
			}
			
			return -1;
		}

		public void setVolume(int volume) {
			request("/ctrl-int/1/setproperty?dmcp.volume="+volume);
		}
		
	};
	
	private PlaybackControl control = new PlaybackControl() {

		public void clear() {
			request("/ctrl-int/1/cue?command=clear");
		}

		public void enqueue(List<? extends Track> tracks) {
			String query = "/ctrl-int/1/cue?command=play&query=(";
			for (Iterator<? extends Track> it = tracks.iterator(); it.hasNext();) {
				query += "'daap.itemid:"+it.next().get(DAAPConstants.TRACK_ID)+"'";
				if (it.hasNext()) query += ',';
			}
			query += ")";
			request(query);
		}

		public void next() {
			request("/ctrl-int/1/nextitem");
		}

		public void pause() {
			request("/ctrl-int/1/pause");
		}

		public void play() {
			request("/ctrl-int/1/playpause");
		}

		public void prev() {
			request("/ctrl-int/1/previtem");
		}
		
		public void jump(int index) {
			request("/ctrl-int/1/cue?command=play&index="+index);
		}

		public int revision() {
			Composite c = request("/ctrl-int/1/playstatusupdate");

			if (c == null) return 0;

			for (Node n : c.nodes) {
				switch (n.code) {
				case DACPConstants.cmsr:
					return ((IntegerNode)n).getValue();
				}
			}
			
			return 0;
		}

		public void stop() {
			request("/ctrl-int/1/stop");
		}

		public void registerListener(PlaybackListener listener) {}
		public void removeListener(PlaybackListener listener) {}

		public void setCollection(Collection<? extends Track> collection) {
			request("/ctrl-int/1/playspec?playlist-spec='dmap.persistentid:"+collection.persistentId()+"'");
		}
		
	};

	public boolean addCollection(Collection<? extends Track> collection) {
		// TODO Auto-generated method stub
		return false;
	}

	public Iterable<Collection<? extends Track>> collections() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean removeCollection(Collection<? extends Track> collection) {
		// TODO Auto-generated method stub
		return false;
	}
}
