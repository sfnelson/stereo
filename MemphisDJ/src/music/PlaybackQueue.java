package music;

import interfaces.Track;
import interfaces.collection.AbstractCollection;
import interfaces.collection.Collection;
import interfaces.collection.Source;

import java.util.ArrayList;
import java.util.Collections;
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
	private ShufflePlaylist shuffle;
	private ShufflePlaylist queued;
	private ShufflePlaylist recent;
	
	public PlaybackQueue(Source<? extends Track> source) {
		shuffle = new ShufflePlaylist(this, Collection.QUEUE_ID, Collection.QUEUE_PERSISTENT_ID, "Shuffle");
		queued = new ShufflePlaylist(this, Collection.QUEUE_ID+3, Collection.QUEUE_PERSISTENT_ID+3, "Queued");
		recent = new ShufflePlaylist(this, Collection.QUEUE_ID+6, Collection.QUEUE_PERSISTENT_ID+6, "Recent");
		
		this.source = source;
		source.registerListener(this);
	}
	
	private volatile boolean expectingChange = false;
	private volatile boolean changed = false;
	private void expectingChange(boolean expectingChange) {
		this.expectingChange = expectingChange;
		if (expectingChange == false && changed) {
			changed = false;
			notifyQueueChanged();
		}
	}
	
	public synchronized void next() {
		
		System.out.println("queue: next");
		
		expectingChange(true);
		
		if (current != null) {
			recent.insertFirst(current);
			recent.trim(RECENT);
		}
		
		while (shuffle.size() < QUEUED && shuffle.hasNext()) {
			shuffle.append(source.next());
		}
		
		if (queued.hasNext()) {
			current = queued.next();
			position++;
		}
		else if (shuffle.hasNext()) {
			current = shuffle.next();
			position++;
		}
		else {
			notifyQueueEmpty();
		}
		
		expectingChange(false);
	}
	
	public synchronized void prev() {
		
		System.out.println("queue: prev");
		
		expectingChange(true);
		
		if (!recent.hasNext()) {
			
			if (current != null) {
				queued.insertFirst(current);
				position--;
			}
			
			current = recent.next();
		}
		
		expectingChange(false);
	}
	
	public synchronized void clear() {
		
		expectingChange(true);
		
		queued.clear();
		shuffle.clear();
		
		expectingChange(false);
		
		System.out.println("queue cleared");
	}
	
	public synchronized void enqueue(List<? extends Track> tracks) {
		
		expectingChange(true);
		
		queued.appendAll(tracks);
		
		expectingChange(false);
		
		System.out.println("enqueued songs");
	}
	
	public synchronized boolean hasSongs() {
		return queued.hasNext() || shuffle.hasNext();
	}
	
	public synchronized void added(Iterable<? extends Track> tracks) {
		
		System.out.println("songs added: updating queue");
		
		expectingChange(true);
		
		boolean empty = !shuffle.hasNext() && current == null;
		
		while (shuffle.size() < QUEUED && source.hasNext()) {
			Track t = source.next();
			shuffle.append(t);
			System.out.println("added " + t);
		}
		
		if (empty && shuffle.hasNext()) {
			notifyTracksAvailable();
		}
		
		expectingChange(false);
	}
	
	public synchronized void removed(Set<? extends Track> tracks) {
		
		boolean changed = false;
		
		expectingChange(true);
		
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
		for (Track t: shuffle.tracks()) {
			if (tracks.contains(t)) {
				shuffle.remove(t);
				changed = true;
			}
		}
		
		expectingChange(false);
		
		if (!changed) return;
		
		if (current == null) {
			next();
		}
		
		if (current == null) {
			notifyQueueEmpty();
		}
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
	
	public Collection<? extends Track> shuffle() {
		return shuffle.collection();
	}
	
	public Collection<? extends Track> recent() {
		return recent.collection();
	}

	public synchronized Collection<? extends Track> playlist() {
		QueueSource qs = new QueueSource();
		List<Track> recent = new ArrayList<Track>(this.recent.tracks());
		Collections.reverse(recent);
		qs.add(recent);
		qs.add(current);
		qs.add(queued.tracks());
		qs.add(shuffle.tracks());
		return qs.collection();
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
		
		if (expectingChange) {
			changed = true;
			return;
		}
		
		System.out.println("queue changed");
		for (QueueListener l: super.listeners()) {
			l.queueChanged();
		}
	}

	private class QueueSource extends AbstractCollection<Track> implements Source<Track> {
		
		private ArrayList<Track> tracks;
		private Iterator<Track> it;
		
		public QueueSource() {
			super(Collection.QUEUE_ID, Collection.QUEUE_PERSISTENT_ID);
	
			tracks = new ArrayList<Track>();
			it = tracks.iterator();
		}
		
		public void add(Track track) {
			if (track != null) {
				tracks.add(track);
				it = tracks.iterator();
			}
		}
		
		public void add(Iterable<? extends Track> collection) {
			for (Track t: collection) {
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

		public List<Track> tracks() {
			return tracks;
		}
		
		public void clear() {
			tracks.clear();
			it = tracks.iterator();
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
			return "Queued Songs";
		}

		public Collection<? extends Track> parent() {
			return null;
		}

		public Source<Track> source() {
			return this;
		}
		
	}
}
