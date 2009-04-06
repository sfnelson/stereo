package util.command.ctrlint;

import interfaces.DJInterface;

import java.util.Map;

import util.command.Command;
import api.Response;

public class GetSpeakers implements Command {

	public void init(Map<String, String> args) {
		// no args needed
	}

	public Response run(DJInterface dj) {
			
		return new util.response.ctrlint.GetSpeakers();
	}
	
}