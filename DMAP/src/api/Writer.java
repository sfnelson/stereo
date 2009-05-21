package api;

import java.util.Calendar;
import java.util.List;

import interfaces.Constants;

public interface Writer {
	
	public void appendNode(Node node);
	public void appendBoolean(Constants code, boolean value);
	public void appendByte(Constants code, byte value);
	public void appendShort(Constants code, short value);
	public void appendInteger(Constants code, int value);
	public void appendLong(Constants code, long value);
	public void appendVersion(Constants code, byte[] value);
	public void appendDate(Constants code, Calendar value);
	public void appendLongLong(Constants code, int[] value);
	public void appendString(Constants code, String value);
	public void appendList(Constants code, byte type, List<? extends Node> value);
	
}
