package util.node;

public interface Visitor {

	public int visit(Node node);
	public int visitBooleanNode(BooleanNode node);
	public int visitByteNode(ByteNode node);
	public int visitComposite(Composite node);
	public int visitIntegerNode(IntegerNode node);
	public int visitLongNode(LongNode node);
	public int visitLongLongNode(LongLongNode node);
	public int visitStringNode(StringNode node);
	public int visitVersionNode(VersionNode node);
	public int visitImageNode(ImageNode imageNode);
	public int visitPageNode(PageNode padeNode);
}
