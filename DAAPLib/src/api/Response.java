package api;

import interfaces.Constants;

public class Response implements Node {
	
	public static final int OK = 200;
	public static final int CREATED = 201;
	public static final int NO_CONTENT = 204;
	public static final int NOT_FOUND = 404;
	
	private final Constants type;
	private final int status;
	
	public Response(Constants type, int status) {
		this.type = type;
		this.status = status;
	}
	
	public Constants type() {
		return type;
	}
	
	public int status() {
		return status;
	}
	
	public String statusText() {
		switch (status) {
		case OK: return "200 OK";
		case CREATED: return "201 CREATED";
		case NO_CONTENT: return "204 NO CONTENT";
		case NOT_FOUND: return "404 NOT FOUND";
		default: return "";
		}
	}
	
	public void write(Writer writer) {
		writer.appendInteger(Constants.dmap_status, status);
	}

}
