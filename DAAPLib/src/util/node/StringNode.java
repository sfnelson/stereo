package util.node;

import java.io.UnsupportedEncodingException;

public class StringNode extends Node {
    private final String value;
    private final byte[] bytes;
    
    public StringNode(int code, String value) throws UnsupportedEncodingException {
        super(code);
        this.value = (value != null)?value:"";
        this.bytes = value.getBytes("UTF-8");
    }
    
    public String toString() {
        return "<" + super.toString() + " value=\"" + value + "\" />";
    }

	public String getValue() {
		return value;
	}
	
	public byte[] getBytes() {
		return bytes;
	}

	@Override
	public int visit(Visitor visitor) {
		return visitor.visitStringNode(this);
	}
}