package util.command.ctrlint;

import interfaces.DJInterface;

import java.util.Map;

import util.command.Command;
import api.Response;

public class Stop implements Command {

	public void init(Map<String, String> args) {
		// no params
	}

	public Response run(DJInterface dj) {
		
		dj.playbackControl().stop();
		
		return new Response(null, Response.NO_CONTENT);
		
	}

	
}
