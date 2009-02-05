package interfaces;

import notification.EventGenerator;
import notification.PlayerListener;

public interface Player extends EventGenerator<PlayerListener> {
	
	public static final byte STOPPED = 2;
	public static final byte PAUSED = 3;
	public static final byte PLAYING = 4;
	
	public void setTrack(Track t);
	public void start();
	public void stop();
	public void pause();
	public byte status();
	public int elapsed();
	public byte[] getAlbumArt();
}