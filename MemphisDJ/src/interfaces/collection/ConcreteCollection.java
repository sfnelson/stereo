package interfaces.collection;

import interfaces.Track;

public class ConcreteCollection<T extends Track> extends AbstractCollection<T> {

	private final String name;
	private final int edit;
	private final boolean isRoot;
	private final Collection<? extends T> parent;
	private final int size;
	private final Source<T> source;
	 
	public ConcreteCollection(int id, long persistentId, String name,
			int edit, boolean isRoot, Collection<? extends T> parent,
			int size, Source<T> source) {
		super(id, persistentId);
		
		this.name = name;
		this.edit = edit;
		this.isRoot = isRoot;
		this.parent = parent;
		this.size = size;
		this.source = source;
	}

	public int editStatus() {
		return edit;
	}

	public boolean isRoot() {
		return isRoot;
	}

	public String name() {
		return name;
	}

	public Collection<? extends T> parent() {
		return parent;
	}

	public int size() {
		return size;
	}

	public Source<T> source() {
		return source;
	}

}
