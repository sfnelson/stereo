package music;

import interfaces.DJInterface;
import interfaces.PlaybackControl;
import interfaces.PlaybackStatus;
import interfaces.VolumeControl;
import interfaces.collection.Collection;

import java.util.HashSet;
import java.util.Set;


public class DJ implements DJInterface, PlaybackStatus {

	private final Library library;
	private final interfaces.Player player;
	private final interfaces.PlaybackQueue queue;
	private final interfaces.PlaybackControl control;
	private final interfaces.VolumeControl volume;
	
	private final Set<Collection<? extends Track>> collections;

	public DJ() {
		collections = new HashSet<Collection<? extends Track>>();
		
		library = new Library("All Songs");
		
		queue = new PlaybackQueue(library);
		
		player = new Player();
		control = new PlaybackController(player, queue);
		volume = new music.VolumeControl();
	}

	public Track current() {
		return queue.current();
	}
	
	public int position() {
		return queue.position();
	}

	public int elapsedTime() {
		return player.elapsed();
	}

	public byte[] getAlbumArt() {
		return player.getAlbumArt();
	}

	public Collection<? extends Track> playlist() {
		return queue.playlist();
	}

	public byte state() {
		return player.status();
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

	public interfaces.Library<? extends Track> library() {
		return library;
	}

	public Iterable<Collection<? extends Track>> collections() {
		return collections;
	}

	public boolean addCollection(Collection<? extends Track> collection) {
		return collections.add(collection);
	}

	public boolean removeCollection(Collection<? extends Track> collection) {
		return collections.remove(collection);
	}

}
