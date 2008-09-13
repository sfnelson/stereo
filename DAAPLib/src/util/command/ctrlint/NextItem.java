package util.command.ctrlint;

import interfaces.DJInterface;

import java.util.Map;

import util.command.Command;
import util.node.Node;

public class NextItem implements Command {

	public void init(Map<String, String> args) {
		// no args
	}

	public Node run(DJInterface dj) {
		
		dj.next();
		
		return null;
	}

}
