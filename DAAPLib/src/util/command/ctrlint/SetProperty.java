package util.command.ctrlint;

import interfaces.DJInterface;

import java.util.Map;
import java.util.Scanner;

import util.command.Command;
import api.Response;

public class SetProperty implements Command {

	private Map<String, String> args;
	
	public void init(Map<String, String> args) {
		this.args = args;
	}

	public Response run(DJInterface dj) {
		
		if (args != null && args.containsKey("dmcp.volume")) {
			Scanner property = new Scanner(args.get("dmcp.volume"));
			if (property.hasNextDouble()) {
				dj.volume().setVolume((int)Math.round(property.nextDouble()));
				
				return new Response(null, Response.NO_CONTENT);
			}
			else {
				throw new IllegalArgumentException("property not understood: "+args);
			}
		}
		else {
			throw new IllegalArgumentException("unknown property requested: " + args);
		}
		
	}

}
