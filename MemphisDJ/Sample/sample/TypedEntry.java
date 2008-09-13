package sample;

import testing.DaapUtilities;

public class TypedEntry<T> extends Entry {
	
	public T value;
	
	public TypedEntry(final String name, final int number, final short type, final int length, final T value) {
		super(name, number, type, length);
		this.value = value;
	}
	
	@Override
	public T getValue() {
		return this.value;
	}
	
	@Override
	public int count() {
		return 1;
	}
	
	@Override
	public String toString() {
		return "\t" + this.name + " (" + DaapUtilities.intToString(this.number) + ") = " + this.value;
	}
}