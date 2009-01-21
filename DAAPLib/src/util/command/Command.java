package util.command;

import interfaces.DJInterface;

import java.util.Map;

import api.Response;

public interface Command extends RequestNode {

	public void init(Map<String, String> args);
	public Response run(DJInterface dj);
	
}
