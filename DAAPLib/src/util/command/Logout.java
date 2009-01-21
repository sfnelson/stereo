package util.command;

import interfaces.DJInterface;

import java.util.Map;

import api.Response;

public class Logout implements Command {

	public void init(Map<String, String> args) {
		System.out.println("logging out with argument: " + args);
	}

	public Response run(DJInterface dj) {
		//nothing to do until we manage connections.
		
		//expects 204
		return new Response(null, Response.NO_CONTENT);
	}
}