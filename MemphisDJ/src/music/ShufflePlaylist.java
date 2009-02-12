package music;

import interfaces.Track;
import interfaces.collection.AbstractCollection;
import interfaces.collection.Collection;
import interfaces.collection.EditableSource;
import interfaces.collection.Source;

import java.util.LinkedList;

import notification.AbstractEventGenerator;

public class ShufflePlaylist<T extends Track>
			extends AbstractEventGenerator<Source.Listener>
			implements Source<T>, EditableSource<T> {

	private volatile LinkedList<T> _list = new LinkedList<T>();
	
	private LinkedList<T> getList() {
		return _list;
	}
	private void setList(LinkedList<T> list) {
		this._list = list;
	}
	

	public void clear() {
		setList(new LinkedList<T>());
	}
	
	public boolean hasNext() {
		return !getList().isEmpty();
	}

	public T next() {
		LinkedList<T> old = getList();
		T next = old.peek();
		LinkedList<T> list = new LinkedList<T>(old.subList(1, old.size()));
		setList(list);
		return next;
	}

	public int size() {
		return getList().size();
	}

	public Iterable<T> tracks() {
		return getList();
	}

	public void append(T t) {
		if (t != null) {
			LinkedList<T> n = new LinkedList<T>(getList());
			n.addLast(t);
			setList(n);
 		}
	}
	
	public void appendAll(java.util.Collection<? extends T> ts) {
		if (ts != null) {
			LinkedList<T> n = new LinkedList<T>(getList());
			n.addAll(ts);
			setList(n);
 		}
	}

	public void insertFirst(T t) {
		if (t != null) {
			LinkedList<T> n = new LinkedList<T>(getList());
			n.addFirst(t);
			setList(n);
		}
	}

	public void move(T track, T marker) {
		if (track != null) {
			LinkedList<T> n = new LinkedList<T>(getList());
			if (marker == null) {
				n.remove(track);
				n.addLast(track);
			}
			else if (marker.equals(track));
			else {
				n.remove(track);
				int i = 0;
				for (T t: n) {
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

	public void remove(T t) {
		LinkedList<T> list = getList();
		if (t != null && list.contains(t)) {
			list = new LinkedList<T>(list);
			list.remove(t);
			setList(list);
		}
	}
	
	public void trim(int size) {
		if (getList().size() <= size) return;
		LinkedList<T> list = new LinkedList<T>(getList().subList(0, size));
		setList(list);
	}
	
	// Collection stuff
	
	private final ShufflePlaylist<T> shuffle = this;
	private final Collection<T> collection = new AbstractCollection<T>((int)Collection.QUEUE_PERSISTENT_ID, Collection.QUEUE_PERSISTENT_ID) {

		public int editStatus() {
			return Collection.GENERATED;
		}

		public boolean isRoot() {
			return false;
		}

		public String name() {
			return "Shuffle";
		}

		public Collection<? extends T> parent() {
			return null;
		}

		public int size() {
			return shuffle.size();
		}

		public Source<T> source() {
			return shuffle;
		}
		
	};

	public final Collection<T> collection() {
		return collection;
	}
}
