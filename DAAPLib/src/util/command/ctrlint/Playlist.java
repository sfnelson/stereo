package util.command.ctrlint;

import interfaces.DJInterface;

import java.util.Map;

import dacp.DACPTreeBuilder;

import util.command.Command;
import util.node.Node;

public class Playlist implements Command {

	public void init(Map<String, String> args) {
		//no args
	}

	public Node run(DJInterface dj) {
		return DACPTreeBuilder.buildPlaylistResponse(dj.playbackStatus().getPlaylist());
	}

}
