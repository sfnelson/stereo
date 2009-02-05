package util.command.ctrlint;

import interfaces.DJInterface;
import interfaces.Track;
import interfaces.collection.Collection;

import java.math.BigInteger;
import java.util.Map;

import util.command.Command;
import util.queryparser.QueryParser;
import util.queryparser.Token;
import api.Response;

public class PlaySpec implements Command {

	private String playlist;
	
	public void init(Map<String, String> args) {
		playlist = args.get("playlist-spec");
	}

	public Response run(DJInterface dj) {
		
		Token t = (Token)QueryParser.parse(playlist);

		long playlist = new BigInteger(t.value).longValue();
		
		for (Collection<? extends Track> c: dj.library().collections()) {
			if (c.persistentId() == playlist) {
				System.out.println("playlist set: " + c.name());
				dj.playbackControl().setCollection(c);
			}
		}
		
		return new Response(null, Response.NO_CONTENT);
		
	}

}
