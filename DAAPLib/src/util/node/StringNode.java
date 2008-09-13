package util.node;

public class StringNode extends Node {
    private final String value;
    
    public StringNode(int code, String value) {
        super(code);
        this.value = (value != null)?value:"";
    }
    
    public String toString() {
        return "<" + super.toString() + " value=\"" + value + "\" />";
    }

	public String getValue() {
		return value;
	}

	@Override
	public int visit(Visitor visitor) {
		return visitor.visitStringNode(this);
	}
}