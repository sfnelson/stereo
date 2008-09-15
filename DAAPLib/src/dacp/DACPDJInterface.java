package dacp;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.List;

import reader.DACPResponseParser;
import util.DACPConstants;
import util.node.ByteNode;
import util.node.Composite;
import util.node.LongLongNode;
import util.node.Node;
import util.node.StringNode;

import interfaces.DJInterface;
import interfaces.LibraryListener;
import interfaces.PlaylistStatusListener;
import interfaces.Track;

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

	public void next() {
		request("/ctrl-int/1/nextitem");
	}

	public void pause() {
		request("/ctrl-int/1/pause");
	}

	public void play() {
		request("/ctrl-int/1/playpause");
	}

	public void setVolume(int newVolume) {
		// TODO Auto-generated method stub
		
	}

	public void stop() {
		// TODO Auto-generated method stub
		
	}

	public Track currentTrack() {
		Composite c = request("/ctrl-int/1/playstatusupdate");

		if (c == null) return null;

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

	public byte[] getAlbumArt() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Track> getPlaylist() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getVolume() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int playbackElapsedTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int playbackRevision() {
		// TODO Auto-generated method stub
		return 0;
	}

	public byte playbackStatus() {
		Composite c = request("/ctrl-int/1/playstatusupdate");

		if (c == null) return -1;
		
		for (Node n : c.nodes) {
			if (n.code == DACPConstants.caps) {
				return ((ByteNode)n).getValue();
			}
		}
		
		return -1;
	}

	public void registerPlaybackStatusListener(PlaylistStatusListener l) {
		// TODO Auto-generated method stub
		
	}

	public void removePlaybackStatusListener(PlaylistStatusListener l) {
		// TODO Auto-generated method stub
		
	}

	public List<Track> getLibrary() {
		// TODO Auto-generated method stub
		return null;
	}

	public int libraryVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void registerLibraryListener(LibraryListener l) {
		// TODO Auto-generated method stub
		
	}

}
