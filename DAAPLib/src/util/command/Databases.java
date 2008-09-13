package util.command;

import interfaces.DJInterface;

import java.util.Map;

import dacp.DACPTreeBuilder;

import util.DACPConstants;
import util.command.databases.Browse;
import util.command.databases.Groups;
import util.node.Node;

public class Databases extends PathNode implements Command {

	public void init(Map<String, String> args) {
		// no parameters expected
	}

	public Node run(DJInterface dj) {
		//TODO when multiple playlist support is added this will need to change
		return DACPTreeBuilder.buildDatabaseResponse(dj.getLibrary().size(), 1); //no playlists so far
	}
	
	public PathNode _1() {
		return this;
	}
	
	public Command browse() {
		return this;
	}
	
	public Command groups() {
		return new Groups();
	}
	
	public Command artists() {
		return new Browse(DACPConstants.abar, DACPConstants.asar);
	}
	
	public Command albums() {
		return new Browse(DACPConstants.abal, DACPConstants.asal);
	}

}
