package util.command.ctrlint;

import interfaces.DJInterface;

import java.util.Map;

import util.command.Command;
import api.Response;

public class PrevItem implements Command {

	public void init(Map<String, String> args) {
		// no args
	}

	public Response run(DJInterface dj) {
		
		dj.playbackControl().prev();
		
		return new Response(null, Response.NO_CONTENT);
	}

}
