package music;

import interfaces.Track;
import interfaces.collection.AbstractCollection;
import interfaces.collection.Collection;
import interfaces.collection.EditableSource;
import interfaces.collection.Source;

import java.util.LinkedList;
import java.util.List;

import notification.AbstractEventGenerator;

public class ShufflePlaylist
			extends AbstractEventGenerator<Source.Listener>
			implements Source<Track>, EditableSource<Track> {
	
	private final PlaybackQueue queue;
	private final int containerId;
	private int collectionItemId;
	
	public ShufflePlaylist(PlaybackQueue queue, final int id, final long pid, final String name) {
		
		this.queue = queue;
		this.containerId = id;
		collectionItemId = id+1;
		
		collection = new AbstractCollection<Track>(id, pid) {

				public int editStatus() {
					return Collection.EDITABLE;
				}

				public boolean isRoot() {
					return false;
				}

				public String name() {
					return name;
				}

				public Collection<? extends Track> parent() {
					return null;
				}

				public int size() {
					return shuffle.size();
				}

				public Source<Track> source() {
					return shuffle;
				}
				
			};
	}
	
	private volatile LinkedList<Track> _list = new LinkedList<Track>();
	
	private LinkedList<Track> getList() {
		return _list;
	}
	
	private void setList(LinkedList<Track> list) {
		this._list = list;
		queue.notifyQueueChanged();
	}

	public void clear() {
		setList(new LinkedList<Track>());
	}
	
	public boolean hasNext() {
		return !getList().isEmpty();
	}

	public Track next() {
		LinkedList<Track> old = getList();
		Track next = old.peek();
		LinkedList<Track> list = new LinkedList<Track>(old.subList(1, old.size()));
		setList(list);
		return next;
	}

	public int size() {
		return getList().size();
	}

	public List<Track> tracks() {
		return getList();
	}

	public void append(Track t) {
		if (t != null) {
			LinkedList<Track> n = new LinkedList<Track>(getList());
			n.addLast(new CollectionTrack(t, containerId, collectionItemId++));
			setList(n);
 		}
	}
	
	public void appendAll(java.util.Collection<? extends Track> ts) {
		if (ts != null) {
			LinkedList<Track> n = new LinkedList<Track>(getList());
			for (Track t: ts) {
				n.add(new CollectionTrack(t, containerId, this.collectionItemId++));
			}
			setList(n);
 		}
	}

	public void insertFirst(Track t) {
		if (t != null) {
			LinkedList<Track> n = new LinkedList<Track>(getList());
			n.addFirst(t);
			setList(n);
		}
	}

	public void move(Track track, Track marker) {
		if (track != null) {
			LinkedList<Track> n = new LinkedList<Track>(getList());
			if (marker == null) {
				n.remove(track);
				n.addLast(track);
			}
			else if (marker.equals(track));
			else {
				n.remove(track);
				int i = 0;
				for (Track t: n) {
					if (t.equals(marker)) {
						n.subList(0, i).add(track);
						break;
					}
					i++;
				}
				if (i == n.size()) {
					//didn't find element to insert before
					n.addLast(track);
				}
			}
			setList(n);
		}
	}

	public void remove(Track t) {
		LinkedList<Track> list = getList();
		if (t != null && list.contains(t)) {
			list = new LinkedList<Track>(list);
			list.remove(t);
			setList(list);
		}
	}
	
	public void removeAll(java.util.Collection<? extends Track> coll) {
		for (Track t: coll) {
			remove(t);
		}
	}
	
	public void trim(int size) {
		if (getList().size() <= size) return;
		LinkedList<Track> list = new LinkedList<Track>(getList().subList(0, size));
		setList(list);
	}
	
	// Collection stuff
	
	private final ShufflePlaylist shuffle = this;
	private final Collection<Track> collection;

	public final Collection<Track> collection() {
		return collection;
	}
}
