package music;

import interfaces.AbstractDJ;
import interfaces.ControlServerCreator;
import interfaces.Lackey;
import interfaces.PlaybackControl;
import interfaces.PlaybackStatus;
import interfaces.Playlist;
import interfaces.Track;
import interfaces.VolumeControl;


public class DJ extends AbstractDJ implements PlaybackStatus {

	private final interfaces.Lackey lackey;
	private final interfaces.Player player;
	private final interfaces.PlaybackQueue queue;
	private final interfaces.PlaybackControl control;
	private final interfaces.VolumeControl volume;

	public DJ() {
		if (lackeyCreator != null) {
			lackey = lackeyCreator.create();
		}
		else {
			throw new NullPointerException("No LackeyCreator registered");
		}
		
		queue = new PlaybackQueue(lackey.trackSource());
		player = new Player();
		control = new PlaybackController(player, queue);
		volume = new music.VolumeControl();
		
		for (ControlServerCreator s: DJ.serverCreators) {
			s.create(this);
		}
	}

	public Track currentTrack() {
		return queue.current();
	}

	public int elapsedTime() {
		return player.elapsed();
	}

	public byte[] getAlbumArt() {
		return player.getAlbumArt();
	}

	public Playlist<? extends Track> getPlaylist() {
		return queue.playlist();
	}

	public byte state() {
		return player.status();
	}

	public Lackey library() {
		return lackey;
	}

	public PlaybackControl playbackControl() {
		return control;
	}

	public PlaybackStatus playbackStatus() {
		return this;
	}
	
	public VolumeControl volume() {
		return volume;
	}

}
