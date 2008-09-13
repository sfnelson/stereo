package music;

import interfaces.AbstractDJ;
import interfaces.ControlServerCreator;
import interfaces.Lackey;
import interfaces.LackeyClient;
import interfaces.LackeyCreator;
import interfaces.Track;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import player.PlaybackListener;

public class DJ extends AbstractDJ implements PlaybackListener, LackeyClient {

	private Lackey lackey;

	private List<Track> playlist;

	private int playlistSize = 10;

	private Player player;

	private Track current;

	private int currentVolume;
	private List<Track> recentlyPlayedTracks=new ArrayList<Track>();
	private int recentlyPlayedTracksSize=30;
	private boolean paused;

	public static void registerServerCreator(ControlServerCreator creator) {
		serverCreators.add(creator);
	}
	public static void removeServerCreator(ControlServerCreator creator) {
		serverCreators.remove(creator);
	}
	private static Set<ControlServerCreator> serverCreators = new HashSet<ControlServerCreator>();
	
	public static void setLackeyCreator(LackeyCreator creator) {
		lackeyCreator = creator;
	}
	private static LackeyCreator lackeyCreator;

	public DJ() {
		playlist = new ArrayList<Track>();
		player = new player.Player();
		player.addPlaybackListener(this);
		
		if (lackeyCreator != null) {
			lackey = lackeyCreator.create(this);
		}
		else {
			throw new NullPointerException("No LackeyCreator registered");
		}
		
		for (ControlServerCreator s: DJ.serverCreators) {
			s.create(this);
		}
	}

	private void fillPlaylist() {
		System.out.println("Attempting to fill playlist of size " + playlist.size());
		List<Track> lib = lackey.getAllTracks();
		if (lib != null && !lib.isEmpty()) {
			Collections.shuffle(lib);
			for (int i = 0; playlist.size() < playlistSize && i < lib.size(); i++) {
				Track t = lib.get(i);
				// unnecessary?
				if (t != null) {
					playlist.add(t);
				}
			}
			System.out.println("Playlist size: " + playlist.size());
		} else {
			stop();
			System.out.println("Library empty: stopped playback.");
		}
	}

	/**
	 * 
	 * @param c
	 *            A map of filter criterias. The key will be the search category
	 *            such as 'artist' or 'album, the value associated with the key
	 *            is the word the filter will try to match with.
	 * @return
	 */
	public List<Track> getPlaylistWithFilter(Map<Integer, String> c){
		List<Track> returned = new ArrayList<Track>();
		List<Track> allTracks = lackey.getAllTracks();

		//System.out.println("DAAPConstants.ALBUM="+DAAPConstants.ARTIST+" "+c);


		//Search through every criteria for potential matches
		for(int i=0; i<allTracks.size(); i++){

			Track currentTrack=allTracks.get(i);
			boolean fitCrit=true;

			for(int s : c.keySet()){

				if (!c.get(s).equalsIgnoreCase((String)currentTrack.getTag(s)))	{
					fitCrit=false;
					break;
				}
			}

			if (fitCrit)returned.add(currentTrack);

		}
		return returned;
	}

	/**
	 * Takes a set of Track identifier (e.g Track.ALBUM) - value pairs
	 * which will select from all songs the songs which meet
	 * the criteria and then fill the playlist.
	 * @param c
	 */
	public void setPlaylistWithFilter(Map<Integer, String> c){
		stop();
		playlist=getPlaylistWithFilter(c);
		start();
	}

	public void appendTracks(Map<Integer, String> c){
		List<Track> toAppend = getPlaylistWithFilter(c);

		playlist.addAll(toAppend);
	}

	public void setVolume(int volume) {
		currentVolume = volume;
		URL url = DJ.class.getResource("setvolume.sh");

		try {
			System.out.println("setting volume to " + volume);
			Process p = Runtime.getRuntime().exec(
					"bash " + url.getFile() + " " + volume);
			for (Scanner sc = new Scanner(p.getErrorStream()); sc.hasNextLine();) {
				System.out.println(sc.nextLine());
			}
		} catch (IOException e) {
			System.err.println("set volume failed");
			e.printStackTrace();
		}
	}


	/*
	 * Lackey calls when 
	 */
	public void tracksAdded(){
		System.out.println("tracks added");

		if(playlist.isEmpty()){

			fillPlaylist();
			start();
		}

		if (playlist.size() < playlistSize){
			fillPlaylist();	
		}

	}

	/*
	 * Lackey calls this when the library has changed (i.e DAAP server dropped).
	 * Validates the current playlist.
	 */
	public void libraryChanged(){
		lackey.checkPlaylist(playlist);
		if (playlist.size() < playlistSize){
			fillPlaylist();	
		}
		System.out.println("Library changed: playlist updated");
		this.notifyLibraryVersionChanged();
	}


	/*
	 * Modify DJ state
	 */
	public void play() {
		playbackEvent();
		if(paused){
			player.start();
			paused=false;
		}
		else start();
	}

	public void pause(){
		playbackEvent();
		player.pause();
		paused=true;
	}


	public void stop(){
		playbackEvent();
		player.stop();
	}

	public void next() {
		playbackEvent();
		player.stop();
		playbackFinished();
	}

	public void start(){
		if (playlist.isEmpty()) return;

		current = playlist.remove(0);
		recentlyPlayedTracks.add(current);
		if(recentlyPlayedTracks.size()>recentlyPlayedTracksSize){
			recentlyPlayedTracks.remove(0);
		}
		System.out.println("Polled playlist.");
		try {
			player.setInputStream(current.getStream());
		} catch (IOException e) {
			System.out.println("Failed to send stream to player.");
			//e.printStackTrace();
		}
	}

	public void setPlaylist(List<Track> playlist) {
		this.playlist = playlist;
	}


	/*
	 * Info about DJ state
	 */
	public int getVolume(){
		return currentVolume;
	}

	public boolean isPaused(){
		return paused;
	}

	public List<Track> getPlaylist() {
		ArrayList<Track> list = new ArrayList<Track>(playlist);
		if (current != null) list.add(0, current);
		return list;
	}

	public Track currentTrack(){
		return current;
	}

	public List<Track> getRecentlyPlayedTracks() {
		return recentlyPlayedTracks;
	}

	public List<Track> getLibrary(){
		return Collections.unmodifiableList(lackey.getAllTracks());
	}
	
	public int libraryVersion() {
		return lackey.version();
	}


	/*
	 * Methods from PlaybackListener
	 * 
	 */
	public void playbackFinished() {
		InputStream stream = null;
		System.out.println("Playback finished.");
		System.out.println("Size of Playlist: " + playlist.size());
		while (stream == null) {
			try {
				if (playlist.isEmpty()) fillPlaylist();
				if (playlist.isEmpty()) return;

				current = playlist.remove(0); 
				recentlyPlayedTracks.add(current);
				if(recentlyPlayedTracks.size() > recentlyPlayedTracksSize)
					recentlyPlayedTracks.remove(0);


				if (((String) current.getTag(Constants.NAME)).endsWith(".ogg")
						|| ((String) current.getTag(Constants.NAME))
						.endsWith(".wav")) {
					continue;
				}
				System.out.println("Polled playlist.");
				stream = current.getStream();
				fillPlaylist();

			}
			catch (IOException ex) {
				System.err.println("DJ: Failed to find a playable track.");
				//ex.printStackTrace();
			}
		}

		player.setInputStream(stream);
	}

	public void playbackStarted() {
		System.out.println("Playback started");
		playbackEvent();
	}

	public static void main(String[] args){
		new DJ();
	}

	/**
	 * Do a filter on the playlist and replace the current playlist with the new filtered one.
	 */
	public void createPlaylistWithFilter(String type, String criteria){
		Map<Integer,String> filter=new HashMap<Integer, String>();
		try {
			int code = getCode(type);
			filter.put(code,criteria);
			setPlaylistWithFilter(filter);
		}
		catch (Exception ex) {
			System.err.println("unable to find type: " + type);
			ex.printStackTrace();
		}
	}

	/**
	 * Do a filter on the playlist and display it without replacing the current playlist with the new LIST of tracks.
	 */
	public List<Track> queryLibrary(String type, String criteria){
		Map<Integer,String> filter=new HashMap<Integer, String>();
		try {
			int code = getCode(type);
			filter.put(code,criteria);
			return getPlaylistWithFilter(filter);
		}
		catch (Exception ex) {
			System.err.println("unable to find type: " + type);
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Append a new filtered playlist on the bottom of the old playlist.
	 * @param type 
	 * @param criteria
	 * @param oldPL
	 * @return
	 */
	public void append(String type, String criteria){
		Map<Integer, String> filter = new HashMap<Integer, String>();
		
		try {
			int code = getCode(type);
			filter.put(code,criteria);
			getPlaylist().addAll(getPlaylistWithFilter(filter));
		}
		catch (Exception ex) {
			System.err.println("unable to find type: " + type);
			ex.printStackTrace();
		}
	}

	public List<Track> queryRecentlyPlayed(){
		return getRecentlyPlayedTracks();
	}
	
	private static int getCode(String name) throws Exception {
		if (name.equals("name")) {
			return Constants.NAME;
		}
		if (name.equals("artist")) {
			return Constants.ARTIST;
		}
		throw new Exception("Invalid for query: " + name);
	}
	
	private int playbackRevision = 1;
	private void playbackEvent() {
		playbackRevision++;
		this.notifyCurrentTrackChanged();
	}
	
	public int playbackRevision() {
		return playbackRevision;
	}
	public byte playbackStatus() {
		return player.status();
	}
	public int playbackElapsedTime() {
		return player.elapsed();
	}
	public byte[] getAlbumArt() {
		return player.getAlbumArt();
	}
}
