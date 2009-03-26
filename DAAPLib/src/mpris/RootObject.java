package mpris;

import mpris.dbustypes.FullVersion;

import org.freedesktop.MediaPlayer;

public class RootObject implements MediaPlayer {
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
}
