package music;

import interfaces.Playlist;
import interfaces.Track;
import interfaces.TrackSource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import notification.AbstractEventGenerator;
import notification.QueueListener;
import notification.TrackSourceListener;

//TODO handle library going away: remove inaccessible tracks

class PlaybackQueue extends AbstractEventGenerator<QueueListener>
		implements interfaces.PlaybackQueue, TrackSourceListener {
	
	public static final int RECENT = 5;
	public static final int QUEUED = 10;
	
	private final TrackSource source;
	
	private volatile Track current;
	private LinkedList<Track> queued;
	private LinkedList<Track> recent;
	
	public PlaybackQueue(TrackSource source) {
		queued = new LinkedList<Track>();
		recent = new LinkedList<Track>();
		
		this.source = source;
		source.registerListener(this);
	}
	
	public synchronized void next() {
		
		if (current != null) {
			recent.addLast(current);
			while (recent.size() > RECENT) {
				recent.removeFirst();
			}
		}
		
		while (queued.size() < QUEUED && source.hasNextTrack()) {
			queued.addLast(source.nextTrack());
		}
		
		if (!queued.isEmpty()) {
			current = queued.removeFirst();
			notifyQueueChanged();
		}
		else {
			notifyQueueEmpty();
		}
	}
	
	public synchronized void prev() {
		if (!recent.isEmpty()) {
			
			if (current != null) {
				queued.addFirst(current);
			}
			
			current = recent.removeLast();
		
			notifyQueueChanged();
		}
	}
	
	public synchronized void clear() {
		queued.clear();
		
		System.out.println("queue cleared");
		
		notifyQueueChanged();
	}
	
	public synchronized void enqueue(List<? extends Track> tracks) {
		queued.addAll(tracks);
		
		notifyQueueChanged();
	}
	
	public synchronized boolean hasSongs() {
		return !queued.isEmpty();
	}
	
	public synchronized void tracksAvailable() {
		
		boolean empty = queued.isEmpty() && current == null;
		
		while (queued.size() < QUEUED && source.hasNextTrack()) {
			queued.addLast(source.nextTrack());
		}
		
		if (empty && !queued.isEmpty()) {
			notifyTracksAvailable();
		}
	}
	
	public synchronized void tracksUnavailable(Set<? extends Track> available) {
		for (Iterator<Track> it = queued.iterator(); it.hasNext();) {
			Track t = it.next();
			if (!available.contains(t)) {
				it.remove();
			}
		}
		for (Iterator<Track> it = recent.iterator(); it.hasNext();) {
			Track t = it.next();
			if (!available.contains(t)) {
				it.remove();
			}
		}
		
		if (!available.contains(current)) {
			current = null;
			next();
		}
	}
	
	public synchronized Track current() {
		return current;
	}

	public synchronized Playlist<? extends Track> playlist() {
		return new PlaybackPlaylist(this);
	}
	
	protected void notifyQueueEmpty() {
		for (QueueListener l: super.listeners()) {
			l.queueEmpty();
		}
	}
	
	protected void notifyTracksAvailable() {
		for (QueueListener l: super.listeners()) {
			System.out.println("Tracks Available");
			l.tracksAvailable();
		}
	}
	
	protected void notifyQueueChanged() {
		for (QueueListener l: super.listeners()) {
			l.queueChanged();
			
			//System.out.println(recent);
			//System.out.println(current);
			//System.out.println(queued);
		}
	}
	
	private static class PlaybackPlaylist extends ArrayList<Track> implements Playlist<Track> {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 621466966772934136L;

		public PlaybackPlaylist(PlaybackQueue queue) {
			this.addAll(queue.recent);
			if (queue.current != null) this.add(queue.current);
			this.addAll(queue.queued);
		}

		public int id() {
			return 2;
		}

		public boolean isRoot() {
			return false;
		}

		public String name() {
			return "Shuffle Playlist";
		}

		public Playlist<Track> parent() {
			return null;
		}

		public long persistantId() {
			return 621466966772934136L;
		}
		
	}
}
