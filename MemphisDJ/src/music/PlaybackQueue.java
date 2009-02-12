package music;

import interfaces.Track;
import interfaces.collection.AbstractCollection;
import interfaces.collection.Collection;
import interfaces.collection.Source;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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
	private ShufflePlaylist<Track> queued;
	private ShufflePlaylist<Track> recent;
	
	public PlaybackQueue(Source<? extends Track> source) {
		queued = new ShufflePlaylist<Track>();
		recent = new ShufflePlaylist<Track>();
		
		this.source = source;
		source.registerListener(this);
	}
	
	public synchronized void next() {
		
		System.out.println("queue: next");
		
		if (current != null) {
			recent.insertFirst(current);
			recent.trim(RECENT);
		}
		
		while (queued.size() < QUEUED && source.hasNext()) {
			queued.append(source.next());
		}
		
		if (queued.hasNext()) {
			current = queued.next();
			position++;
			notifyQueueChanged();
		}
		else {
			notifyQueueEmpty();
		}
	}
	
	public synchronized void prev() {
		
		System.out.println("queue: prev");
		
		if (!recent.hasNext()) {
			
			if (current != null) {
				queued.insertFirst(current);
				position--;
			}
			
			current = recent.next();
		
			notifyQueueChanged();
		}
	}
	
	public synchronized void clear() {
		queued.clear();
		
		System.out.println("queue cleared");
		
		notifyQueueChanged();
	}
	
	public synchronized void enqueue(List<? extends Track> tracks) {
		queued.appendAll(tracks);
		
		notifyQueueChanged();
	}
	
	public synchronized boolean hasSongs() {
		return queued.hasNext();
	}
	
	public synchronized void added(Iterable<? extends Track> tracks) {
		
		System.out.println("songs added: updating queue");
		
		boolean empty = !queued.hasNext() && current == null;
		
		boolean changed = false;
		while (queued.size() < QUEUED && source.hasNext()) {
			Track t = source.next();
			queued.append(t);
			changed = true;
			System.out.println("added " + t);
		}
		
		if (empty && queued.hasNext()) {
			notifyTracksAvailable();
		}
		else if (changed) {
			notifyQueueChanged();
		}
	}
	
	public synchronized void removed(Set<? extends Track> tracks) {
		
		boolean changed = false;
		
		for (Track t: recent.tracks()) {
			if (tracks.contains(t)) {
				recent.remove(t);
				changed = true;
			}
		}
		if (current != null && tracks.contains(current)) {
			current = null;
			changed = true;
		}
		for (Track t: queued.tracks()) {
			if (tracks.contains(t)) {
				queued.remove(t);
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
	
	public Collection<? extends Track> queue() {
		return queued.collection();
	}

	public synchronized Collection<? extends Track> playlist() {
		
		return new QueueSource(recent.tracks(), current, queued.tracks()).collection();
		
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

	private class QueueSource extends AbstractCollection<Track> implements Source<Track> {
		
		private ArrayList<Track> tracks;
		private Iterator<Track> it;
		
		public QueueSource(Iterable<Track> recent, Track current, Iterable<Track> queued) {
			super(0, 0);
			tracks = new ArrayList<Track>();
			for (Track t: recent) {
				tracks.add(0, t);
			}
			tracks.add(current);
			for (Track t: queued) {
				tracks.add(t);
			}
			it = tracks.iterator();
		}

		public Collection<Track> collection() {
			return this;
		}

		public boolean hasNext() {
			return it.hasNext();
		}

		public Track next() {
			return it.next();
		}

		public int size() {
			return tracks.size();
		}

		public Iterable<Track> tracks() {
			return tracks;
		}

		public void registerListener(Listener listener) {}
		public void removeListener(Listener listener) {}

		public int editStatus() {
			return Collection.GENERATED;
		}

		public boolean isRoot() {
			return false;
		}

		public String name() {
			return null;
		}

		public Collection<? extends Track> parent() {
			return null;
		}

		public Source<Track> source() {
			return this;
		}
		
	}
}
