package util.node;

public class BooleanNode extends Node {
    private final boolean value;
    
    public BooleanNode(int code, boolean value) {
        super(code);
        this.value = value;
    }
    
    public boolean getValue(){
    	return value;
    }
    
    public String toString() {
        return "<" + super.toString() + " value=\"" + value + "\" />";
    }

	@Override
	public int visit(Visitor visitor) {
		return visitor.visitBooleanNode(this);
	}
}