package music;

import interfaces.collection.AbstractCollection;
import interfaces.collection.Collection;
import interfaces.collection.Source;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import notification.AbstractEventGenerator;
import notification.QueueListener;

class PlaybackQueue extends AbstractEventGenerator<QueueListener>
		implements interfaces.PlaybackQueue, Source.Listener {
	
	public static final int RECENT = 5;
	public static final int QUEUED = 10;
	
	private Source<? extends Track> source;
	
	private volatile Track current;
	private volatile int position;
	private LinkedList<Track> queued;
	private LinkedList<Track> recent;
	
	public PlaybackQueue(Source<? extends Track> source) {
		queued = new LinkedList<Track>();
		recent = new LinkedList<Track>();
		
		this.source = source;
		source.registerListener(this);
	}
	
	public synchronized void next() {
		
		System.out.println("queue: next");
		
		if (current != null) {
			recent.addLast(current);
			while (recent.size() > RECENT) {
				recent.removeFirst();
			}
		}
		
		while (queued.size() < QUEUED && source.hasNext()) {
			queued.addLast(source.next());
		}
		
		if (!queued.isEmpty()) {
			current = queued.removeFirst();
			position++;
			notifyQueueChanged();
		}
		else {
			notifyQueueEmpty();
		}
	}
	
	public synchronized void prev() {
		
		System.out.println("queue: prev");
		
		if (!recent.isEmpty()) {
			
			if (current != null) {
				queued.addFirst(current);
				position--;
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
	
	public synchronized void added(Iterable<? extends Track> tracks) {
		
		System.out.println("songs added: updating queue");
		
		boolean empty = queued.isEmpty() && current == null;
		
		while (queued.size() < QUEUED && source.hasNext()) {
			queued.addLast(source.next());
			System.out.println("added " + queued.getLast());
		}
		
		if (!queued.isEmpty() && empty) {
			if (empty) notifyTracksAvailable();
			else notifyQueueChanged();
		}
	}
	
	public synchronized void removed(Set<? extends Track> tracks) {
		
		boolean changed = false;
		
		for (Iterator<Track> it = recent.iterator(); it.hasNext();) {
			if (tracks.contains(it.next())) {
				it.remove();
				changed = true;
			}
		}
		if (current != null && tracks.contains(current)) {
			current = null;
			changed = true;
		}
		for (Iterator<Track> it = queued.iterator(); it.hasNext();) {
			if (tracks.contains(it.next())) {
				it.remove();
				changed = true;
			}
		}
		
		if (!changed) return;
		
		if (current == null) {
			next();
		}
		
		if (current == null) {
			notifyQueueEmpty();
		}
		else {
			notifyQueueChanged();
		}
	}
	
	protected synchronized void remove(Track t) {
		recent.remove(t);
		if (current == t) current = null;
		queued.remove(t);
	}
	
	public synchronized Track current() {
		return current;
	}
	
	public synchronized int position() {
		return position;
	}

	public synchronized Collection<? extends Track> playlist() {
		
		//TODO create collection
		List<Track> tracks = new ArrayList<Track>();
		tracks.addAll(recent);
		if (current != null) tracks.add(current);
		tracks.addAll(queued);
		
		return new PlaybackQueueCollection(tracks);
	}
	
	public synchronized void setSource(Source<? extends Track> source) {
		
		this.source.removeListener(this);
		
		Set<Track> removed = new HashSet<Track>();
		for (Track t: this.source.tracks()) {
			removed.add(t);
		}
		
		this.source.registerListener(this);
		this.source = source;
		
		for (Track t: this.source.tracks()) {
			removed.remove(t);
		}
		
		removed(removed);
	}
	
	protected void notifyQueueEmpty() {
		System.out.println("queue empty");
		for (QueueListener l: super.listeners()) {
			l.queueEmpty();
		}
	}
	
	protected void notifyTracksAvailable() {
		System.out.println("tracks available");
		for (QueueListener l: super.listeners()) {
			l.tracksAvailable();
		}
	}
	
	protected void notifyQueueChanged() {
		System.out.println("queue changed");
		for (QueueListener l: super.listeners()) {
			l.queueChanged();
		}
	}
	
	private class PlaybackQueueCollection extends AbstractCollection<Track> {

		private final List<Track> tracks;
		private final Iterator<Track> trackIt;
		
		public PlaybackQueueCollection(List<Track> tracks) {
			super(Collection.QUEUE_ID, Collection.QUEUE_PERSISTENT_ID);
			this.tracks = tracks;
			this.trackIt = tracks.iterator();
		}
		
		public boolean hasNext() {
			return trackIt.hasNext();
		}

		public Track next() {
			return trackIt.next();
		}
		
		public int editStatus() {
			return GENERATED;
		}

		public Iterable<? extends Track> tracks() {
			return tracks;
		}

		public Iterator<Track> iterator() {
			return tracks.iterator();
		}
		
		public int id() {
			return source.id();
		}

		public long persistentId() {
			return source.persistentId();
		}

		public boolean isRoot() {
			// TODO Auto-generated method stub
			return false;
		}

		public String name() {
			return source.name();
		}

		public Collection<Track> parent() {
			return null;
		}

		public int size() {
			return tracks.size();
		}
		
	}

}
