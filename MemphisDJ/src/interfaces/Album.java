package interfaces;


public interface Album extends HasMetadata {
	
	public int id();
	public long persistentId();
	public String name();
	public String artist();
	public int tracks();

}
