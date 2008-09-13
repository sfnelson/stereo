package util.command.ctrlint;

import interfaces.DJInterface;
import interfaces.Track;

import java.util.List;
import java.util.Map;

import util.command.Command;
import util.node.Node;
import dacp.DACPTreeBuilder;

public class RequestPlaylist implements Command {

	public String toString() {
		return "requestplaylist";
	}

	public void init(Map<String, String> args) {
		// no args
	}

	public Node run(DJInterface dj) {
		
		List<Track> playlist = dj.getPlaylist();
		return DACPTreeBuilder.buildPlaylistResponse(playlist);
	}

}
