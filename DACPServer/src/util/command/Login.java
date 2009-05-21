package util.command;

import interfaces.DJInterface;

import java.util.Map;

import api.Response;

public class Login implements Command {
	
	private static int session = 0;

	public void init(Map<String, String> args) {
		System.out.println("logged in with argument: " + args);
	}

	public Response run(DJInterface dj) {
		
		int session = ++Login.session;
		return new dmap.response.Login(session);
		
	}
}