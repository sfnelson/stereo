package util.command.ctrlint;

import interfaces.DJInterface;
import interfaces.collection.Collection;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import music.Track;

import util.command.Command;
import util.node.Node;
import dacp.DACPTreeBuilder;

public class Playlist implements Command {

	public void init(Map<String, String> args) {
		//no args
	}

	public Node run(DJInterface dj) {
		try {
			Collection<? extends Track> coll = dj.playbackStatus().playlist();
			
			List<Track> pl = new ArrayList<Track>();
			for (Track t: coll) {
				pl.add(t);
			}
			return DACPTreeBuilder.buildPlaylistResponse(pl);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

}
