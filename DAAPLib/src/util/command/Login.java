package util.command;

import interfaces.DJInterface;

import java.util.Map;

import util.node.Node;
import dacp.DACPTreeBuilder;

public class Login implements Command {

	public void init(Map<String, String> args) {
		System.out.println("logged in with argument: " + args);
	}

	public Node run(DJInterface dj) {
		
		return DACPTreeBuilder.buildLoginNode();
	}
}