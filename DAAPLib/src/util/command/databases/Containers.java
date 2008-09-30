package util.command.databases;

import interfaces.DJInterface;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import util.command.Command;
import util.command.PathNode;
import util.node.Node;
import dacp.DACPTreeBuilder;

public class Containers extends PathNode implements Command {

	public void init(Map<String, String> args) {
		//no args used
	}

	public Node run(DJInterface dj) {
		//TODO only reply with the parameters requested in the args
		
		try {
			return DACPTreeBuilder.buildPlaylistsResponse(dj.library().getPlaylists());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Command items(int container) {
		return new Items(container);
	}

}
