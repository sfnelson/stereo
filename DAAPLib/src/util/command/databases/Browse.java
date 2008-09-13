package util.command.databases;

import interfaces.DJInterface;
import interfaces.Track;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dacp.DACPTreeBuilder;

import util.command.Command;
import util.node.Node;
import util.queryparser.Filter;
import util.queryparser.FilterTracks;
import util.queryparser.QueryParser;

public class Browse implements Command {
	
	private Map<String,String> args;
	private int code;
	private int field;
	
	public Browse(int code, int field) {
		this.code = code;
		this.field = field;
	}
	
	public void init(Map<String, String> args) {
		this.args = args;
		
		System.out.println(args);
	}

	public Node run(DJInterface dj) {
		
		List<Track> songs = dj.getLibrary();
		
		if (args != null && args.containsKey("filter")) {
			Filter f = QueryParser.parser.parse(args.get("filter"));
			System.out.println(f);
			songs = FilterTracks.filter(f, songs);
		}
		
		Set<String> results = new HashSet<String>();
		
		for (Track t: songs) {
			String name = (String)t.getTag(field);
			if (name != null) results.add(name);
		}
		
		System.out.println("partial result has " + results.size() + " unique");
		
		List<String> list = new ArrayList<String>();
		list.addAll(results);
		
		Collections.sort(list);
		
		System.out.println("returning " + list.size() + " elements from " + songs.size());
		
		return DACPTreeBuilder.buildBrowseResponse(code, list);
	}

}
