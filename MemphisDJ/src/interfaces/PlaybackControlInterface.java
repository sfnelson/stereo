package interfaces;

public interface PlaybackControlInterface {

	public void pause();
	public void play();
	public void next();
	public void stop();
	
	public void setVolume(int newVolume);
}