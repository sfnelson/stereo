package dmap.response.ctrlint;

import interfaces.Constants;
import api.Reader;
import api.Response;
import api.Writer;

public class GetProperty extends Response {

	private final Constants property;
	private final Object value;
	
	public static GetProperty read(Reader reader) {

		Constants property = null;
		Object value = null;
		
		for (Constants c: reader) {
			property = c;
			switch (c.type) {
			case Constants.BYTE: value = reader.nextByte(c); break;
			case Constants.SHORT: value = reader.nextShort(c); break;
			case Constants.INTEGER: value = reader.nextInteger(c); break;
			case Constants.SIGNED_INTEGER: value = reader.nextInteger(c); break;
			case Constants.LONG: value = reader.nextLong(c); break;
			case Constants.DATE: value = reader.nextInteger(c); break;
			case Constants.VERSION: value = reader.nextVersion(c); break;
			case Constants.STRING: value = reader.nextString(c); break;
			default:
				throw new RuntimeException("unexpected node " + c.longName);
			}
		}

		return new GetProperty(property, value);
	}
	
	public GetProperty(Constants property, Object value) {
		super(Constants.dmcp_getpropertyresponse, Response.OK);
		
		this.property = property;
		this.value = value;
	}
	
	public Constants getProperty() {
		return property;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void write(Writer writer) {
		super.write(writer);
		
		Constants c = property;
		Object v = value;
		
		switch (property.type) {
		case Constants.BYTE: writer.appendByte(c, (Byte)v); break;
		case Constants.SHORT: writer.appendShort(c, (Short)v); break;
		case Constants.INTEGER: writer.appendInteger(c, (Integer)v); break;
		case Constants.SIGNED_INTEGER: writer.appendInteger(c, (Integer)v); break;
		case Constants.LONG: writer.appendLong(c, (Long)v); break;
		case Constants.DATE: writer.appendInteger(c, (Integer)v); break;
		case Constants.VERSION: writer.appendVersion(c, (byte[])v); break;
		case Constants.STRING: writer.appendString(c, (String)v); break;
		default:
			throw new RuntimeException("unexpected node " + c.longName);
		}
	}
}
