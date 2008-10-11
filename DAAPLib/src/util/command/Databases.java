package util.command;

import interfaces.DJInterface;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import dacp.DACPTreeBuilder;

import util.DACPConstants;
import util.command.databases.Browse;
import util.command.databases.Containers;
import util.command.databases.Edit;
import util.command.databases.Groups;
import util.node.Node;

public class Databases extends PathNode implements Command {

	public void init(Map<String, String> args) {
		// no parameters expected
	}

	public Node run(DJInterface dj) {
		int items = dj.library().size();
		int containers = dj.library().numCollections();
		try {
			return DACPTreeBuilder.buildDatabaseResponse(items, containers);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Command browse(int db) {
		return this;
	}
	
	public Command groups(int db) {
		return new Groups();
	}
	
	public Command containers(int db) {
		return new Containers();
	}
	
	public Command edit(int db) {
		return new Edit();
	}
	
	public Command artists() {
		return new Browse(DACPConstants.abar, DACPConstants.ARTIST);
	}
	
	public Command albums() {
		return new Browse(DACPConstants.abal, DACPConstants.ALBUM);
	}
	
	public Command genres() {
		return new Browse(DACPConstants.abgn, DACPConstants.GENRE);
	}

}
