package util.command.databases;

import interfaces.Constants;
import interfaces.DJInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import music.Track;
import util.command.Command;
import util.queryparser.ApplyFilter;
import util.queryparser.Filter;
import util.queryparser.QueryParser;
import api.Response;

public class Browse implements Command {

	private Map<String,String> args;
	private Constants code;
	private Constants field;

	public Browse(Constants code, Constants field) {
		this.code = code;
		this.field = field;
	}

	public void init(Map<String, String> args) {
		this.args = args;

		System.out.println(args);
	}

	public Response run(DJInterface dj) {

		Iterable<? extends Track> songs = dj.library().tracks();

		System.out.println("library has " + dj.library().size() + " elements");

		if (args != null && args.containsKey("filter")) {
			Filter f = QueryParser.parse(args.get("filter"));
			System.out.println(f);
			songs = ApplyFilter.filter(f, songs);
		}

		Set<String> results = new HashSet<String>();

		for (Track t: songs) {
			String name = (String)t.get(field);
			if (name != null) results.add(name);
		}

		System.out.println("partial result has " + results.size() + " unique");

		List<String> list = new ArrayList<String>();
		list.addAll(results);

		Collections.sort(list);

		System.out.println("returning " + list.size() + " elements from " + dj.library().size());

		return new util.response.databases.Browse(code, list);
	}

}
