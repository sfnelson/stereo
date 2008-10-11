package util.command.ctrlint;

import interfaces.DJInterface;
import interfaces.collection.Collection;

import java.math.BigInteger;
import java.util.Map;

import music.Track;
import util.command.Command;
import util.node.Node;
import util.queryparser.QueryParser;
import util.queryparser.Token;

public class PlaySpec implements Command {

	private String playlist;
	
	public void init(Map<String, String> args) {
		playlist = args.get("playlist-spec");
	}

	public Node run(DJInterface dj) {
		
		Token t = (Token)QueryParser.parse(playlist);

		long playlist = new BigInteger(t.value).longValue();
		
		for (Collection<? extends Track> c: dj.library().collections()) {
			if (c.persistentId() == playlist) {
				System.out.println("playlist set: " + c.name());
				dj.playbackControl().setCollection(c);
				return null;
			}
		}
		
		return null;
		
	}

}
