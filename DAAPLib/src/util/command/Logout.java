package util.command;

import interfaces.DJInterface;

import java.util.Map;

import util.node.Node;
import dacp.DACPTreeBuilder;

public class Logout implements Command {

	public void init(Map<String, String> args) {
		System.out.println("logging out with argument: " + args);
	}

	public Node run(DJInterface dj) {
		
		return DACPTreeBuilder.buildLogoutNode();
	}
}