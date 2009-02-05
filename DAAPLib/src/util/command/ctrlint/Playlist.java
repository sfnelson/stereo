package util.command.ctrlint;

import interfaces.DJInterface;
import interfaces.Track;
import interfaces.collection.Collection;

import java.util.Map;

import util.command.Command;
import util.response.PlaylistSongs;
import api.Response;

public class Playlist implements Command {

	public void init(Map<String, String> args) {
		//no args
	}

	public Response run(DJInterface dj) {
		Collection<? extends Track> coll = dj.playbackStatus().playlist();
		
		return new PlaylistSongs(coll);
		
	}

}
