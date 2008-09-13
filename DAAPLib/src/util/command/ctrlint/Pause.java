package util.command.ctrlint;

import interfaces.DJInterface;

import java.util.Map;

import util.command.Command;
import util.node.Node;

public class Pause implements Command {

	public void init(Map<String, String> args) {
		//no args
	}

	public Node run(DJInterface dj) {
		dj.pause();
		return null;
	}

}
