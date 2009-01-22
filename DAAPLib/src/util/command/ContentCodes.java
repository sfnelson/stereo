package util.command;

import interfaces.DJInterface;

import java.util.Map;

import api.Response;

public class ContentCodes implements Command {
	
	private static Response contentCodes = new util.response.ContentCodes();

	public void init(Map<String, String> args) {
		//nothing to do
	}

	public Response run(DJInterface dj) {

		return contentCodes;

	}
}
