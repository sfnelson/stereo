package util.node;

import java.io.UnsupportedEncodingException;

public class PageNode extends Node {

	private final String contentType;
	private final byte[] text;
	
	public PageNode(String contentType, String text) throws UnsupportedEncodingException {
		super(0);
		this.contentType = contentType;
		this.text = text.getBytes("UTF-8");
	}
	
	public String contentType() {
		return contentType;
	}

	public int length() {
		return text.length;
	}
	
	public byte[] text() {
		return text;
	}

	@Override
	public int visit(Visitor visitor) {
		return visitor.visitPageNode(this);
	}

}