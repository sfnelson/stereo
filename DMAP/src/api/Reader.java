package api;

import interfaces.Constants;

import java.util.Calendar;
import java.util.Iterator;


public interface Reader extends Iterable<Constants> {
	
	public boolean hasNextBoolean(Constants code);
	public boolean nextBoolean(Constants code);
	
	public boolean hasNextByte(Constants code);
	public byte nextByte(Constants code);
	
	public boolean hasNextShort(Constants code);
	public int nextShort(Constants code);
	
	public boolean hasNextInteger(Constants code);
	public int nextInteger(Constants code);
	
	public boolean hasNextLong(Constants code);
	public long nextLong(Constants code);
	
	public boolean hasNextVersion(Constants code);
	public byte[] nextVersion(Constants code);
	
	public boolean hasNextDate(Constants code);
	public Calendar nextDate(Constants code);
	
	public boolean hasNextLongLong(Constants code);
	public int[] nextLongLong(Constants code);
	
	public boolean hasNextString(Constants code);
	public String nextString(Constants code);
	
	public boolean hasNextComposite(Constants code);
	public Reader nextComposite(Constants code);
	
	public Iterator<Constants> iterator();
	
}