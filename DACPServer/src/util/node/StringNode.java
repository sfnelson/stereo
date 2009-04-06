package util.node;

import java.io.UnsupportedEncodingException;


public class StringNode extends Node {
    private final String value;
    private final byte[] bytes;
    
    public StringNode(int code, String value) {
        super(code);
        this.value = (value != null)?value:"";
        byte[] b = null;
        try {
        	b = value.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException ex) {
        	ex.printStackTrace();
        	b = value.getBytes();
        }
        this.bytes = b;
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