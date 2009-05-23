package interfaces.collection;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import api.collections.Source;
import api.tracks.Track;

import notification.AbstractEventGenerator;


public abstract class AbstractSetSource<T extends Track>
		extends AbstractEventGenerator<Source.Listener>
		implements Source<T> {

	public void add(Iterable<T> tracks) {
		
		List<T> added = new ArrayList<T>();
		
		for (T t: tracks) {
			if (!store.contains(t)) {
				store.add(t);
				added.add(t);
			}
		}
		
		for (Source.Listener l: this.listeners()) {
			l.added(added);
		}
	}
	
	public void remove(Set<T> tracks) {
		
		Set<T> removed = new HashSet<T>();
		
		for (T t: tracks) {
			if (store.contains(t)) {
				store.remove(t);
				removed.add(t);
			}
		}

		for (Source.Listener l: this.listeners()) {
			l.removed(removed);
		}
	}

	public boolean hasNext() {
		return !store.isEmpty();
	}

	public T next() {
		return store.random();
	}

	public List<T> tracks() {
		return store.tracks();
	}

	public Iterator<T> iterator() {
		return store.tracks().iterator();
	}
	
	public int size() {
		return this.store.size();
	}
	
	protected final Store<T> store = new Store<T>();
	protected static class Store<T extends Track> {
		private List<T> trackList = new ArrayList<T>();
		private Set<T> trackSet = new HashSet<T>();
		
		public synchronized boolean contains(T t) {
			return trackSet.contains(t);
		}
		
		public synchronized boolean add(T t) {
			if (!trackSet.contains(t)) {
				trackSet.add(t);
				trackList.add(t);
				return true;
			}
			return false;
		}
		
		public synchronized boolean remove(T t) {
			if (trackSet.contains(t)) {
				trackSet.remove(t);
				trackList.remove(t);
				return true;
			}
			return false;
		}
		
		public synchronized T get(int i) {
			return trackList.get(i);
		}
		
		public synchronized T random() {
			int size = trackList.size();
			int addr = ((int)(Math.random()*size))%size;
			return trackList.get(addr);
		}
		
		public synchronized boolean isEmpty() {
			return trackList.isEmpty();
		}
		
		public synchronized int size() {
			return trackList.size();
		}
		
		public synchronized List<T> tracks() {
			return new ArrayList<T>(trackList);
		}
	}
}