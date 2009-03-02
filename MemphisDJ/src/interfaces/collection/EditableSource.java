package interfaces.collection;


import interfaces.Track;

public interface EditableSource<T extends Track> extends Source<T> {

	public void insertFirst(T track);
	public void append(T t);
	public void remove(T t);
	public void removeAll(java.util.Collection<? extends T> tracks);
	public void move(T track, T marker);
	public void clear();
	public void appendAll(java.util.Collection<? extends T> tracks);
	
}
