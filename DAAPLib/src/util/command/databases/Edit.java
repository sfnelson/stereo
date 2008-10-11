package util.command.databases;

import interfaces.DJInterface;

import java.util.Map;

import util.command.Command;
import util.node.Node;

public class Edit implements Command {

	private Map<String, String> args;
	
	public void init(Map<String, String> args) {
		this.args = args;
	}

	public Node run(DJInterface dj) {
		// TODO Auto-generated method stub
		return null;
	}

}
