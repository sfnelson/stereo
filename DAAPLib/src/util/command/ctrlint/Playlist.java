package util.command.ctrlint;

import interfaces.DJInterface;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import util.command.Command;
import util.node.Node;
import dacp.DACPTreeBuilder;

public class Playlist implements Command {

	public void init(Map<String, String> args) {
		//no args
	}

	public Node run(DJInterface dj) {
		try {
			return DACPTreeBuilder.buildPlaylistResponse(dj.playbackStatus().getPlaylist());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

}
