package util.command.ctrlint;

import interfaces.DJInterface;

import java.util.Map;

import util.command.Command;
import util.command.Image;
import api.Response;

public class NowPlayingArtwork implements Command {

	public void init(Map<String, String> args) {
		// TODO read maximum width and height and actually use these
	}

	public Response run(DJInterface dj) {

		return new Image(dj.playbackStatus().getAlbumArt());
	}

}
