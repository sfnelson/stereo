package util.command.ctrlint;

import interfaces.DJInterface;

import java.util.Map;

import util.command.Command;
import util.node.ImageNode;
import util.node.Node;

public class NowPlayingArtwork implements Command {

	public void init(Map<String, String> args) {
		// TODO read maximum width and height and actually use these
	}

	public Node run(DJInterface dj) {
		
		byte[] image = dj.getAlbumArt();
		
		if (image != null) {
			return new ImageNode(image);
		}
		else {
			return null;
		}
	}

}
