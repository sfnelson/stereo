package util.node;

public class LongNode extends Node {
    private final long value;
    
    public LongNode(int code, long value) {
        super(code);
        this.value = value;
    }
    
    public String toString() {
        return "<" + super.toString() + " value=\"" + value + "\" />";
    }

	public long getValue() {
		return value;
	}

	@Override
	public int visit(Visitor visitor) {
		return visitor.visitLongNode(this);
	}
}