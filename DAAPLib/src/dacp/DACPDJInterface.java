package dacp;

import interfaces.Album;
import interfaces.DJInterface;
import interfaces.Lackey;
import interfaces.PlaybackControl;
import interfaces.PlaybackStatus;
import interfaces.Playlist;
import interfaces.Track;
import interfaces.TrackSource;
import interfaces.VolumeControl;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
		CLITrack t = new CLITrack();
		for (Node n : c.nodes) {
			switch (n.code) {
			case DACPConstants.canp:
				LongLongNode lln = (LongLongNode)n;
				t.setId((int)lln.getValue2()); //drops top types - last int field is id
				break;
			case DACPConstants.cann:
				t.putTag(DACPConstants.NAME, ((StringNode)n).getValue());
				break;
			case DACPConstants.cana:
				t.putTag(DACPConstants.ARTIST, ((StringNode)n).getValue());
				break;
			case DACPConstants.canl:
				t.putTag(DACPConstants.ALBUM, ((StringNode)n).getValue());
				break;
			case DACPConstants.cang:
				t.putTag(DACPConstants.GENRE, ((StringNode)n).getValue());
				break;
			}
		}
		return t;
	}
	
	private CLITrack parsePlaylistTrack(Composite c) {
		CLITrack t = new CLITrack();
		for (Node n : c.nodes) {
			switch (n.code) {
			case DAAPConstants.miid:
				t.setId(((IntegerNode)n).getValue());
				break;
			case DAAPConstants.NAME:
				t.putTag(DACPConstants.NAME, ((StringNode)n).getValue());
				break;
			case DAAPConstants.ARTIST:
				t.putTag(DACPConstants.ARTIST, ((StringNode)n).getValue());
				break;
			case DAAPConstants.ALBUM:
				t.putTag(DACPConstants.ALBUM, ((StringNode)n).getValue());
				break;
			case DAAPConstants.GENRE:
				t.putTag(DACPConstants.GENRE, ((StringNode)n).getValue());
				break;
			}
		}
		return t;
	}
	
	private CLIPlaylist parsePlaylist(Composite c) {
		CLIPlaylist pl = new CLIPlaylist();
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
		CLIPlaylist pl = new CLIPlaylist();
		for (Node n : c.nodes) {
			switch (n.code) {
			case DAAPConstants.miid: pl.setId(((IntegerNode)n).getValue()); break;
			case DAAPConstants.mper: pl.setPersistantId(((LongNode)n).getValue()); break;
			case DAAPConstants.minm: pl.setName(((StringNode)n).getValue()); break;
			case DAAPConstants.abpl: pl.setRoot(((BooleanNode)n).getValue()); break;
			case DAAPConstants.mpco: pl.setParentId(((IntegerNode)n).getValue()); break;
			case DAAPConstants.mimc: pl.setSpecifiedSize(((IntegerNode)n).getValue()); break;
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
	
	public Lackey library() {
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
	
	private Lackey library = new Lackey() {

		public List<? extends Album> getAlbums() {
			Composite c = request("/databases/1/browse/artists?filter=('daap.songartist!:')");

			if (c == null) return null;
			
			return parseAlbums(c);
		}

		public List<? extends Track> getLibrary() {			
			Composite c = request("/databases/1/containers/1/items");

			if (c == null) return null;
			
			return parsePlaylist(c);
		}

		public List<? extends Playlist<? extends Track>> getPlaylists() {
			Composite c = request("/databases/1/containers");

			if (c == null) return null;
			
			return parsePlaylists(c);
		}

		public TrackSource trackSource() { return null; }

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
		
	};
	
	private PlaybackStatus status = new PlaybackStatus() {

		public Track currentTrack() {
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

		public Playlist<? extends Track> getPlaylist() {
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
				query += "'daap.itemid:"+it.next().getTag(DAAPConstants.TRACK_ID)+"'";
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
		
	};
}
