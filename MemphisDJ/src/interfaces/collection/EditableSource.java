package interfaces.collection;


import interfaces.Track;

public interface EditableSource<T extends Track> {

	public void insertFirst(T track);
	public void append(T t);
	public void remove(T t);
	public void move(T track, T marker);
	public void clear();
	public void appendAll(java.util.Collection<? extends T> tracks);
	
}
