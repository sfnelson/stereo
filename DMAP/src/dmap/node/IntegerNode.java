package dmap.node;

public class IntegerNode extends Node {
    private final int value;
    
    public IntegerNode(int code, int value) {
        super(code);
        this.value = value;
    }
    
    public String toString() {
        return "<" + super.toString() + " value=\"" + value + "\" />";
    }

	public int getValue() {
		return value;
	}

	@Override
	public int visit(Visitor visitor) {
		return visitor.visitIntegerNode(this);
	}
}