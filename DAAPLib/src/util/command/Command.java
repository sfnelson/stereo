package util.command;

import java.util.Map;

import interfaces.DJInterface;
import util.node.Node;

public interface Command extends RequestNode {

	public void init(Map<String, String> args);
	public Node run(DJInterface dj);
	
}
