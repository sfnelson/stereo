package player;

import interfaces.AbstractDJ;
import interfaces.Track;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import music.Constants;


/**
 * 
 * @author coxdyla This class interfaces the User interfaces with the servers
 *         playlist TODO make it recieve and interpret DACP requests
 */

public class Controller extends AbstractDJ {

	private final static boolean DEBUG = false;

	private DACPHeckler dacp;

	private String location;
	private int port;

	public Controller(String _location, int _port) {
		this.location = _location;
		this.port = _port;
		boolean success = this.connect();
		if (!success) {
			throw new RuntimeException("Controller failed to connect");
		}
	}


	private boolean connect() {
		// TODO
		// Assumes once connected, it is always connected.

		if (this.dacp == null) {
			try {
				this.dacp = new DACPHeckler(this.location, this.port);
				return true;
			} catch (UnknownHostException e) {
				if (DEBUG)
					e.printStackTrace();
			} catch (IOException e) {
				if (DEBUG)
					e.printStackTrace();
			}
			return false;
		}
		return true;
	}

	public boolean isValidController() {
		return this.dacp != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see player.ControllerInterface#pauseTrack()
	 */
	public void pause() {
		if (this.connect())
			this.dacp.pause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see player.ControllerInterface#playTrack()
	 */
	public void play() {
		if (this.connect())
			this.dacp.play();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see player.ControllerInterface#getPlaylist()
	 */
	public List<Track> getPlaylist() {
		if (this.connect())
			return this.dacp.getTracks();
		return new ArrayList<Track>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see player.ControllerInterface#changeVolume(int)
	 */
	public void setVolume(int newVolume) {
		if (newVolume < 0 || newVolume > 255)
			throw new IllegalArgumentException("volume must be between 0-255");
		if (this.connect())
			this.dacp.setVolume(newVolume);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see player.ControllerInterface#skipTrack()
	 */
	public void next() {
		this.dacp.skip();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see player.ControllerInterface#getVolume()
	 */
	public int getVolume() {
		return this.dacp.getVolume();
	}

	/*
	 * 
	 */
	public void setPlaylist(List<Track> p) {
		ArrayList<Track> tracks = new ArrayList<Track>();
		for (Track t : tracks)
			tracks.add(t);
		if (this.connect())
			this.dacp.setTracks(tracks);
	}

	public void stop() {
		if (this.connect())
			this.dacp.stop();
	}

	public String status() {
			Track t = this.dacp.queryCurrentTrack();
			if (t != null) // TODO print this better? What about paused/unpause?
				return t.toString();
			else
				return "";
	}

	public void createPlaylistWithFilter(String type, String criteria) {
		Map<Integer, String> filter = new HashMap<Integer, String>();
		filter = fillFilter(type, criteria, filter);
		this.dacp.setPlaylistWithFilter(filter);
	}

	public List<Track> queryLibrary(String type, String crit) {
		Map<Integer, String> filter = new HashMap<Integer, String>();
		filter = fillFilter(type, crit, filter);
		return this.dacp.getPlaylistWithFilter(filter);
	}

	// helper method
	/**
	 * Returns a filtered playlist without replacing the current one.
	 * 
	 * @param type
	 * @param criteria
	 */
	private Map<Integer, String> fillFilter(String type, String criteria,
			Map<Integer, String> playList) {
		if (type.equalsIgnoreCase("artist"))
			playList.put(Constants.ARTIST, criteria);
		else if (type.equalsIgnoreCase("album"))
			playList.put(Constants.ALBUM, criteria);

		return playList;
	}

	public List<Track> queryRecentlyPlayed() {
		List<Track> recent = new ArrayList<Track>();
		for (Track t: this.dacp.getRecentlyPlayedTracks()) {
			recent.add(0, t);
		}
		return recent;
	}

	public void append(String type, String crit) {
		// TODO
	}

	public List<Track> getLibrary() {
		return this.dacp.getLibrary();
	}


	public int libraryVersion() {
		// TODO Auto-generated method stub
		return 0;
	}


	public Track currentTrack() {
		// TODO Auto-generated method stub
		return null;
	}


	public int playbackRevision() {
		// TODO Auto-generated method stub
		return 0;
	}


	public byte playbackStatus() {
		// TODO Auto-generated method stub
		return 0;
	}


	public int playbackElapsedTime() {
		// TODO Auto-generated method stub
		return 0;
	}


	public byte[] getAlbumArt() {
		// TODO Auto-generated method stub
		return null;
	}

}
