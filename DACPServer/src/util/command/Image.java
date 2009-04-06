package util.command;

import api.Response;

public class Image extends Response {

	private final byte[] image;
	
	public Image(byte[] image) {
		super(null, Response.OK);
		
		this.image = image;
	}
	
	public byte[] image() {
		return image;
	}
}
