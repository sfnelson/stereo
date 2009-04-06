package util.node;

public class LongLongNode extends Node {
    private final int values[] = new int[4];
    
    public LongLongNode(int code, int i, int j, int k, int l) {
		super(code);
		this.values[0] = i;
		this.values[1] = j;
		this.values[2] = k;
		this.values[3] = l;
	}

	public String toString() {
        return "<" + super.toString() + " value=\"" + values[0]  + " "  + values[1]  + " " + values[2]  + " " + values[3]  + "\" />";
    }

	public int[] getValues() {
		return values;
	}

	@Override
	public int visit(Visitor visitor) {
		return visitor.visitLongLongNode(this);
	}
}