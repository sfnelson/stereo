package interfaces;

public interface DJInterface {
	
	public PlaybackControl playbackControl();
	public PlaybackStatus playbackStatus();
	public Lackey library();
	public VolumeControl volume();

}
