package util.command.ctrlint;

import interfaces.DJInterface;
import interfaces.PlaybackControl;

import java.util.Map;

import util.command.Command;
import api.Response;

public class NextItem implements Command {

	private int revision;
	
	public void init(Map<String, String> args) {
		if (args.containsKey("revision-number")) {
			revision = Integer.parseInt(args.get("revision-number"));
		}
		else {
			revision = -1;
		}
	}

	public Response run(DJInterface dj) {
		
		PlaybackControl control = dj.playbackControl();
		
		if (this.revision == -1 || this.revision >= control.revision()) {
			dj.playbackControl().next();
		}
		
		return new Response(null, Response.NO_CONTENT);
	}

}
