package notification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AbstractEventGenerator<T extends Listener> implements EventGenerator<T> {

	public synchronized void registerListener(T listener) {
		listeners.add(listener);
	}
	
	public synchronized void removeListener(T listener) {
		listeners.remove(listener);
	}
	
	protected synchronized Iterable<T> listeners() {
		return new ArrayList<T>(listeners);
	}
	
	private volatile Set<T> listeners = new HashSet<T>();
}
