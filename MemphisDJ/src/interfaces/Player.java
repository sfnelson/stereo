package interfaces;

import api.notification.EventGenerator;
import api.tracks.Track;
import notification.PlayerListener;

public interface Player extends EventGenerator<PlayerListener> {
	
	public static final byte STOPPED = 2;
	public static final byte PAUSED = 3;
	public static final byte PLAYING = 4;
	
	/**
	 * Set a new track, and start playing it immediately.
	 * @param t
	 */
	public void setTrack(Track t);
	
	/**
	 * Set a new track, but do not start playing it yet.
	 * Note that this will stop whatever track is currently playing.
	 * After this method is called, the status (as returned by the status() method) will always be STOPPED
	 * @param t
	 * @return Play status before the action of this method.
	 */
	public byte setTrackWithoutStarting(Track t);
	
	/**
	 * Set a new track, and start playing it iff we were currently playing the previous track.
	 * If we were stopped or paused before, this method will leave the player stopped.
	 * @param t
	 * @return false iff was playing and so still playing now
	 */
	public boolean setTrackKeepStatus(Track t);
	
	public void start();
	public void stop();
	public void pause();
	public byte status();
	public int elapsed();
	public byte[] getAlbumArt();
	public byte[] getCurrentSong();
}