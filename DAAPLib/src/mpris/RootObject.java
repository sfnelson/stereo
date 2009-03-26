package mpris;

import interfaces.Constants;
import interfaces.DJInterface;
import interfaces.Player;
import interfaces.Track;
import interfaces.collection.Collection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mpris.dbustypes.FullVersion;
import mpris.dbustypes.StatusCode;

import org.freedesktop.MediaPlayer;
import org.freedesktop.dbus.Variant;

import util.response.PlaylistSongs;

public class RootObject implements MediaPlayer {
	private DJInterface dj;
	private static final Map<Constants, String> metadataNames = new HashMap<Constants, String>();;
	
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

	public RootObject(DJInterface dj) {
		this.dj = dj;
	}

	public boolean isRemote() {
		return false;
	}

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
		return dj.playbackStatus().position();
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
		dj.playbackControl().prev();
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
		dj.playbackControl().play(); //TODO: Check that this meets specification, when already playing
	}
	
	public void Repeat(boolean repeat) {
		//Ignore, as not supported
	}
	
	public StatusCode GetStatus() {
		byte state = dj.playbackStatus().state();
		return new StatusCode(state == Player.PLAYING ? 0 : (state == Player.PAUSED ? 1 : 2), false, false, true);
	}
	
	public Map<String, Variant<?>> GetMetadata() {
		return metadataFor(dj.playbackStatus().current());
	}
	
	public int GetCaps() {
		byte state = dj.playbackStatus().state();
		int position = dj.playbackStatus().position();
		int playlistSize = dj.playbackStatus().playlist().size();
		
		int capabilities = MediaPlayer.CAN_HAS_TRACKLIST;
		if (playlistSize > 0 && playlistSize >= position) {
			capabilities += MediaPlayer.CAN_GO_NEXT;
		}
		if (position > 0) {
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
		//TODO
		return 0;
	}
}
