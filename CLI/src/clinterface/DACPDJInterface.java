package clinterface;

import interfaces.Constants;
import interfaces.PlaybackControl;
import interfaces.Track;
import interfaces.VolumeControl;
import interfaces.Track.TrackFactory;
import interfaces.collection.Collection;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;

import notification.PlaybackListener;
import reader.DACPResponseParser;
import util.response.PlaylistSongs;
import util.response.ctrlint.GetProperty;
import util.response.ctrlint.PlayStatusUpdate;
import util.response.databases.Playlists;
import api.Response;
import api.nodes.AlbumNode.AlbumFactory;
import api.nodes.PlaylistNode.PlaylistFactory;

public class DACPDJInterface {
	
	private final DACPResponseParser parser;
	private final String host;
	private final int port;
	
	public DACPDJInterface(String host, int port) {
		this.parser = new CLIResponseParser();
		this.host = host;
		this.port = port;
	}
	
	private Response request(String request) {
		try {
			return parser.request(host, port, request);
		} catch (UnknownHostException e) {
			System.err.println("Unable to connect to " + host);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Problem contacting " + host);
			e.printStackTrace();
		}
		return null;
	}

	public PlaybackControl playbackControl() {
		return this.control;
	}

	public VolumeControl volume() {
		return this.volume;
	}
	
	public PlaylistSongs playlist() {
		
		return (PlaylistSongs)request("/ctrl-int/1/playlist");

	}
	
	public PlayStatusUpdate playStatusUpdate() {
		
		return (PlayStatusUpdate)request("/ctrl-int/1/playstatusupdate");

	}
	
	private VolumeControl volume = new VolumeControl() {

		public int getVolume() {
			GetProperty response = (GetProperty)request("/ctrl-int/1/getproperty?properties=dmcp.volume");

			if (response.getProperty() == Constants.dmcp_volume) {
				return (Integer)response.getValue();
			}
			
			throw new RuntimeException("volume not found");
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
				query += "'dmap.itemid:"+it.next().get(Constants.dmap_itemid)+"'";
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
			return playStatusUpdate().revision;
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

	public CLILibrary browse() {
		return library;
	}
	
	public CLILibrary library = new CLILibrary();
	
	public class CLILibrary {
		
		public Playlists collections() {
			return (Playlists)request("/databases/1/containers");
		}
		
		public Collection<Track> getCollectionByName(String collection) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	private class CLIResponseParser extends DACPResponseParser {

		@Override
		public AlbumFactory albumFactory() {
			return CLIAlbum.factory();
		}

		@Override
		public PlaylistFactory playlistFactory() {
			return CLIPlaylist.factory();
		}

		@Override
		public TrackFactory trackFactory() {
			return CLITrack.factory;
		}
		
	}
}
