package util.node;

public class ImageNode extends Node {

	private final byte[] image;
	
	public ImageNode(byte[] image) {
		super(0);
		this.image = image;
	}
	
	public byte[] image() {
		return image;
	}
	
	@Override
	public int visit(Visitor visitor) {
		return visitor.visitImageNode(this);
	}

}
