package util.command;

import interfaces.DJInterface;

import java.util.Map;

import util.node.Node;
import dacp.DACPTreeBuilder;

public class ServerInfo implements Command {

	public void init(Map<String, String> args) {
		//no arguments
	}

	public Node run(DJInterface dj) {
		System.out.println("Request for server info");

		Node response = DACPTreeBuilder.buildServerInfoNode();

		return response;
	}

}
