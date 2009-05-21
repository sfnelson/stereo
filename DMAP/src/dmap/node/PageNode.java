package dmap.node;

import java.io.UnsupportedEncodingException;

import api.Response;

public class PageNode extends Response {

	private final String contentType;
	private final byte[] text;
	
	public PageNode(String contentType, String text) throws UnsupportedEncodingException {
		super(null, Response.OK);
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

}