package util.node;

public class ByteNode extends Node {
    private final byte value;
    
    public ByteNode(int code, byte value) {
        super(code);
        this.value = value;
    }
    
    public String toString() {
        return "<" + super.toString() + " value=\"" + value + "\" />";
    }

	public byte getValue() {
		return value;
	}

	@Override
	public int visit(Visitor visitor) {
		return visitor.visitByteNode(this);
	}
}

