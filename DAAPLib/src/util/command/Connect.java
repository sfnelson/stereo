package util.command;

import interfaces.DJInterface;

import java.util.Map;

import daap.DAAPLackey;

import api.Response;

public class Connect implements Command {
	
	private String host;
	private int port;

	public void init(Map<String, String> args) {
		if (args.containsKey("host")) {
			host = args.get("host");
		}
		if (args.containsKey("port")) {
			port = Integer.parseInt(args.get("port"));
		}
	}

	public Response run(DJInterface dj) {

		if (port == 0) port = 3689;
		if (host != null) {
			DAAPLackey.lackey().request(host, port);
		}
		
		return new Response(null, Response.NO_CONTENT);

	}
}
