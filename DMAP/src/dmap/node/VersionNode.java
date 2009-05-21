package dmap.node;

public class VersionNode extends Node {
	
    private final byte[] value;
    
    public VersionNode(int code, byte[] value) {
        super(code);
        this.value = value;
    }
    
    public String toString() {
        return "<" + super.toString()
        	+ " value=\"" + value[0]
        	+ "." + value[1] + "." + value[2]
        	+ "." + value[3] + "\" />";
    }

	public byte[] getValue() {
		return value;
	}

	@Override
	public int visit(Visitor visitor) {
		return visitor.visitVersionNode(this);
	}
}

