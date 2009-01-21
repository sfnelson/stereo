package util.command.ctrlint;

import interfaces.DJInterface;

import java.util.Map;

import util.command.Command;
import api.Response;

public class NextItem implements Command {

	public void init(Map<String, String> args) {
		// no args
	}

	public Response run(DJInterface dj) {
		
		dj.playbackControl().next();
		
		return new Response(null, Response.NO_CONTENT);
	}

}
