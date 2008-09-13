package util.node;

public class LongLongNode extends Node {
    private final long value;
    private final long value2;
    
    public LongLongNode(int code, long value, long value2) {
        super(code);
        this.value = value;
        this.value2 = value2;
    }
    
    public LongLongNode(int code, int i, int j, int k, int l) {
		super(code);
		this.value = ((long)i<<32) | j;
		this.value2 = ((long)k<<32) | l;
	}

	public String toString() {
        return "<" + super.toString() + " value=\"" + value  + " " + value2 + "\" />";
    }

	public long getValue() {
		return value;
	}

	public long getValue2() {
		return value2;
	}

	@Override
	public int visit(Visitor visitor) {
		return visitor.visitLongLongNode(this);
	}
}