package interfaces;

public interface HasMetadata {

	public int id();
	public long persistentId();
	public Object get(int tagID);
	public Iterable<Integer> getAllTags();
	
}
