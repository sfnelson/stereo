package util.command.databases;

import interfaces.DJInterface;
import interfaces.Track;
import interfaces.collection.Collection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import util.command.Command;
import util.queryparser.ApplyFilter;
import util.queryparser.Filter;
import util.queryparser.QueryParser;
import api.Response;

public class Items implements Command {

	private Map<String,String> args;
	private int container;
	
	public Items(int container) {
		this.container = container;
	}
	
	public void init(Map<String, String> args) {
		this.args = args;
	}

	public Response run(DJInterface dj) {

		Collection<? extends Track> playlist = null;
		
		for (Collection<? extends Track> p: dj.library().collections()) {
			if (p.id() == container) {
				playlist = p;
				break;
			}
		}
		
		if (playlist == null) {
			System.err.println("requested playlist not found!");
		}
		
		System.out.println("playlist has " + playlist.size() + " elements");
		
		List<? extends Track> pl;
		
		if (args != null && args.containsKey("query")) {
			Filter f = QueryParser.parse(args.get("query"));
			System.out.println(f);
			pl = ApplyFilter.filter(f, playlist);
		}
		else {
			List<Track> l = new ArrayList<Track>();
			for (Track t: playlist) {
				l.add(t);
			}
			pl = l;
		}
		
		/*Collections.sort(playlist, new Comparator<Track>() {
			public int compare(Track o1, Track o2) {
				String a = (String)o1.getTag(DAAPConstants.NAME);
				String b = (String)o2.getTag(DAAPConstants.NAME);
				if (a == null) a = "";
				if (b == null) b = "";
				return a.compareTo(b);
			}
		});*/
		
		System.out.println("returning " + playlist.size() + " elements");
		
		return new util.response.PlaylistSongs(pl);
	}
	
}
