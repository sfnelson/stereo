package util.command;

import interfaces.DJInterface;

import java.util.Map;

import dacp.DACPTreeBuilder;

import util.node.Node;

public class ControlPromptUpdate implements Command {

	private int promptId;
	
	public void init(Map<String, String> args) {
		String id = args.get("prompt-id");
		if (id != null) {
			promptId = Integer.parseInt(id);
		}
	}

	public Node run(DJInterface dj) {
		return DACPTreeBuilder.buildControlPromptUpdate(promptId);
	}
	
	

}
