package mpris;

import interfaces.Constants;
import interfaces.DJInterface;
import interfaces.PlaybackQueue;
import interfaces.Player;
import interfaces.Track;
import interfaces.collection.Collection;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mpris.dbustypes.FullVersion;
import mpris.dbustypes.StatusCode;
import notification.PlaybackListener;

import org.freedesktop.MediaPlayer;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;

import spi.StereoServer;

public class MPRISServer implements MediaPlayer, PlaybackListener, StereoServer {
	private DJInterface dj;
	private static Map<Constants, String> metadataNames = new HashMap<Constants, String>();;
	private DBusConnection conn;
	
	{
		metadataNames.put(Constants.daap_songdataurl, "location");
		metadataNames.put(Constants.dmap_itemname, "title");
		metadataNames.put(Constants.daap_songartist, "artist");
		metadataNames.put(Constants.daap_songalbum, "album");
		metadataNames.put(Constants.daap_songyear, "year");
		metadataNames.put(Constants.daap_songtracknumber, "tracknumber");
		metadataNames.put(Constants.daap_songbitrate, "audio-bitrate");
		metadataNames.put(Constants.daap_songgenre, "genre");
		metadataNames.put(Constants.daap_songtime, "mtime");
	}
	
	public void start(DJInterface dj, String[] args) {
		this.dj = dj;
		
		try {
			conn = DBusConnection.getConnection(DBusConnection.SESSION);
			conn.requestBusName("org.mpris.stereo");
			conn.exportObject("/", this);
			conn.exportObject("/TrackList", this);
			conn.exportObject("/Player", this);
		} catch (DBusException e) {
			e.printStackTrace();
		}
		
		//Listen for updates from the DJ, when the state, track or queue changes
		dj.playbackControl().registerListener(this);
		
		System.out.println("MPRIS control interface started.");
	}

	public boolean isRemote() {
		return false;
	}

	//Root object methods
	public String Identity() {
		return "Stereo";
	}

	public FullVersion MprisVersion() {
		return new FullVersion(1, 0);
	}

	public void Quit() {
		//TODO
	}

	//TrackList methods
	public Map<String, Variant<?>> GetMetadata(int position) {
		Collection<? extends Track> coll = dj.playbackStatus().playlist();
		List<? extends Track> tracks = coll.source().tracks();
		return metadataFor(tracks.get(position));
	}
	
	private Map<String, Variant<?>> metadataFor(Track track) {
		Map<String, Variant<?>> metadata = new HashMap<String, Variant<?>>();
		
		if (track != null) {
			for (Constants tag: track.getAllTags()) {
				metadata.put(metadataNames.containsKey(tag) ? metadataNames.get(tag) : tag.toString(), new Variant<Object>(track.get(tag)));
			}
		}
		
		return metadata;
	}

	public int GetCurrentTrack() {
		return dj.playbackStatus().position() - 1; //Position in dj is counted from 1, but MPRIS counts from 0
	}
	
	public int GetLength() {
		return dj.playbackStatus().playlist().size();
	}
	
	public int AddTrack(String uri, boolean playImmediately) {
		//Not supported
		return 1;
	}
	
	public void DelTrack(int position) {
		//TODO
	}
	
	public void SetLoop(boolean loop) {
		//Ignore, as not supported
	}
	
	public void SetRandom(boolean random) {
		//Ignore, as not supported
	}
	
	//Methods for /Player
	public void Next() {
		dj.playbackControl().next();
	}
	
	public void Prev() {
		dj.playbackControl().jump(-1); //TODO: This does not work
	}
	
	public void Pause() {
		if (dj.playbackStatus().state() == Player.PLAYING) {
			dj.playbackControl().pause();
		}
		else if (dj.playbackStatus().state() == Player.PAUSED) {
			dj.playbackControl().play();
		}
		//Else must be stopped, so just ignore
	}
	
	public void Stop() {
		dj.playbackControl().stop();
	}
	
	public void Play() {
		if (dj.playbackStatus().state() == Player.PLAYING) {
			dj.playbackControl().prev(); //Contrary to the method name, this actually restarts the current track
		}
		else {
			dj.playbackControl().play();
		}
	}
	
	public void Repeat(boolean repeat) {
		//Ignore, as not supported
	}
	
	public StatusCode GetStatus() {
		return statusCodeForState(dj.playbackStatus().state());
	}
	
	private StatusCode statusCodeForState(byte state) {
		return new StatusCode(state == Player.PLAYING ? 0 : (state == Player.PAUSED ? 1 : 2), false, false, true);
	}
	
	public Map<String, Variant<?>> GetMetadata() {
		return metadataFor(dj.playbackStatus().current());
	}
	
	public int GetCaps() {
		byte state = dj.playbackStatus().state();
		int position = dj.playbackStatus().position(); //Note that this is counted from 1 not 0 (0 means no current track)
		int playlistSize = dj.playbackStatus().playlist().size();
		
		int capabilities = MediaPlayer.CAN_HAS_TRACKLIST;
		if (playlistSize > position) {
			capabilities += MediaPlayer.CAN_GO_NEXT;
		}
		if (position > 1) {
			capabilities += MediaPlayer.CAN_GO_PREV;
		}
		if (state == Player.PLAYING || state == Player.PAUSED) {
			capabilities += MediaPlayer.CAN_PAUSE;
		}
		if (dj.playbackStatus().current() != null || playlistSize > 0) {
			capabilities += MediaPlayer.CAN_PLAY;
		}
		if (dj.playbackStatus().current() != null) {
			capabilities += MediaPlayer.CAN_PROVIDE_METADATA;
		}
		
		return capabilities;
	}
	
	public void VolumeSet(int volume) {
		dj.volume().setVolume(volume);
	}
	
	public int VolumeGet() {
		return dj.volume().getVolume();
	}
	
	public void PositionSet(int position) {
		//Ignore, as not supported
	}
	
	public int PositionGet() {
		return dj.playbackStatus().elapsedTime();
	}

	//Methods for PlaybackListener
	public void queueChanged(PlaybackQueue queue) {
		try {
			conn.sendSignal(new TrackListChange("/TrackList", queue.playlist().size()));
			conn.sendSignal(new CapsChange("/Player", GetCaps()));
			//TODO: is queue.playlist() the same as dj.playbackStatus().playlist()? 
		} catch (DBusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stateChanged(byte state) {
		try {
			conn.sendSignal(new StatusChange("/Player", statusCodeForState(state)));
			conn.sendSignal(new CapsChange("/Player", GetCaps()));
		} catch (DBusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void trackChanged(Track track) {
		try {
			conn.sendSignal(new TrackChange("/Player", metadataFor(track)));
			conn.sendSignal(new CapsChange("/Player", GetCaps()));
		} catch (DBusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
