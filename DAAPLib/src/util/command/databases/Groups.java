package util.command.databases;

import interfaces.Album;
import interfaces.Constants;
import interfaces.DJInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import util.command.Command;
import util.queryparser.ApplyFilter;
import util.queryparser.Filter;
import util.queryparser.QueryParser;
import api.Response;
import api.nodes.AlbumNode;

public class Groups implements Command {

	private Map<String, String> args;

	public void init(Map<String, String> args) {
		this.args = args;
	}

	public Response run(DJInterface dj) {

		Iterable<? extends Album> albums = dj.library().albums();
		
		if (args == null) {
			throw new IllegalArgumentException("no arguments to group query");
		}
		
		//don't query, as albums store name as minm not asal
		
		List<? extends Album> als;
		if (args.containsKey("query")) {
			String query = args.get("query").replace(' ', '_');
			Filter q = QueryParser.parse(query);
			System.out.println(q);
			als = ApplyFilter.filter(q, albums);
		}
		else {
			List<Album> as = new ArrayList<Album>();
			for (Album a: albums) {
				as.add(a);
			}
			als = as;
 		}
		
		/*
		meta=all
		type=music
		group-type=albums
		sort=album
		include-sort-headers=1
		query=(('com.apple.itunes.mediakind:1','com.apple.itunes.mediakind:32')+'daap.songalbum!:'
		 */
		
		if (!args.containsKey("group-type") || !args.get("group-type").equals("albums")) {
			throw new IllegalArgumentException("trying to handle unknown group-type: " + args.get("group-type"));
		}
		
		Collections.sort(als, new Comparator<Album>() {
			public int compare(Album a, Album b) {
				int c = a.name().compareTo(b.name());
				if (c == 0) {
					return a.artist().compareTo(b.artist());
				}
				return c;
			}
		});
		
		List<AlbumNode> nodes = new ArrayList<AlbumNode>();
		for (Album a: als) {
			nodes.add(new AlbumNode(a));
		}
		
		System.out.println("returning " + nodes.size() + " elements");

		return new util.response.databases.Groups<AlbumNode>(Constants.daap_albumgrouping, nodes);
	}

}
