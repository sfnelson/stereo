package util.command.databases;

import interfaces.DJInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import util.command.Command;
import util.queryparser.ApplyFilter;
import util.queryparser.Filter;
import util.queryparser.QueryParser;
import api.Response;
import api.collections.Collection;
import api.collections.Source;
import api.tracks.Track;

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

		Source<? extends Track> source = null;
		
		for (Collection<? extends Track> p: dj.library().collections()) {
			if (p.id() == container) {
				source = p.source();
				break;
			}
		}
		
		if (source == null) {
			System.err.println("requested playlist not found!");
			return null;
		}
		
		System.out.println("playlist has " + source.size() + " elements");
		
		List<? extends Track> pl;
		
		if (args != null && args.containsKey("query")) {
			Filter f = QueryParser.parse(args.get("query"));
			System.out.println(f);
			pl = ApplyFilter.filter(f, source.tracks());
		}
		else {
			List<Track> l = new ArrayList<Track>();
			for (Track t: source.tracks()) {
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
		
		System.out.println("returning " + source.size() + " elements");
		
		return new dmap.response.PlaylistSongs(pl);
	}
	
}
