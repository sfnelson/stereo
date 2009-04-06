package util.node;

import java.util.ArrayList;
import java.util.List;

public class Composite extends Node {
    public Composite(int code) {
        super(code);
    }
    
    public void append(Node node) {
        nodes.add(node);
    }
    
    public String toString() {
        String s = "<" + super.toString() + ">";
        for (Node n : nodes) {
            s += "\n  " + n.toString().replace("\n", "\n  ");
        }
        return s + "\n</" + super.toString() + ">";
    }
    
    public List<Node> nodes = new ArrayList<Node>();

	@Override
	public int visit(Visitor visitor) {
		return visitor.visitComposite(this);
	}
}