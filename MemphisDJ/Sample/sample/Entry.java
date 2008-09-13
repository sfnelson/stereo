package sample;

public abstract class Entry {

	public String name;
	public int number;
	public short type;
	public int length;
	
	public abstract Object getValue();
	
	public abstract int count();
	
	public Entry(String name, int number, short type, int length) {
		this.name = name;
		this.number = number;
		this.type = type;
		this.length = length;
	}
}