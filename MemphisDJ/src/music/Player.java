package music;

import java.io.InputStream;

import player.PlaybackListener;

public interface Player {
	public void setInputStream(InputStream i);
	public void start();
	public void stop();
	public void pause();
	public byte status();
	public void addPlaybackListener(PlaybackListener l);
	public void removePlaybackListener(PlaybackListener l);
	public int elapsed();
	public byte[] getAlbumArt();
}