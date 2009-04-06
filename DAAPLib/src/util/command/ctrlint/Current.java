package util.command.ctrlint;

import interfaces.DJInterface;

import java.util.Map;

import util.command.Command;
import util.command.Song;
import api.Response;

public class Current implements Command {

	public void init(Map<String, String> args) {
		// TODO Auto-generated method stub

	}

	public Response run(DJInterface dj) {
		
		byte[] song = dj.playbackStatus().getCurrentSong();
		if (song == null) return null;
		return new Song(song);
	}

}
