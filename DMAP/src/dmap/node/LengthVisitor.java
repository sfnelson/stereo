package dmap.node;

public class LengthVisitor implements Visitor {

	public int visit(Node node) {
		return node.visit(this);
	}

	public int visitBooleanNode(BooleanNode node) {
		return 9;
	}

	public int visitByteNode(ByteNode node) {
		return 9;
	}
	
	public int visitComposite(Composite node) {
		int length = 8;
		for (Node n: node.nodes) {
			length += this.visit(n);
		}
		return length;
	}

	public int visitIntegerNode(IntegerNode node) {
		return 12;
	}

	public int visitLongLongNode(LongLongNode node) {
		return 24;
	}

	public int visitLongNode(LongNode node) {
		return 16;
	}

	public int visitStringNode(StringNode node) {
		return 8+node.getBytes().length;
	}
	
	public int visitVersionNode(VersionNode node) {
		return 8+4;
	}
	
	public int visitImageNode(ImageNode node) {
		return node.image().length;
	}
	
	public int visitPageNode(PageNode node) {
		return node.length();
	}
}
