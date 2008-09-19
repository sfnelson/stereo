package notification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AbstractEventGenerator<T extends Listener> implements EventGenerator<T> {

	public void registerListener(T listener) {
		listeners.add(listener);
	}
	
	public void removeListener(T listener) {
		listeners.remove(listener);
	}
	
	protected Iterable<T> listeners() {
		return new ArrayList<T>(listeners);
	}
	
	private Set<T> listeners = new HashSet<T>();
}
