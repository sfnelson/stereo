package util.command.databases;

import interfaces.DJInterface;
import interfaces.Track;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import util.command.Command;
import util.node.Node;
import util.queryparser.Filter;
import util.queryparser.FilterTracks;
import util.queryparser.QueryParser;
import daap.DAAPConstants;

public class Groups implements Command {

	public Map<String, String> args;

	public void init(Map<String, String> args) {
		this.args = args;
	}

	public Node run(DJInterface dj) {

		List<Track> songs = dj.getLibrary();
		
		if (args == null) {
			throw new IllegalArgumentException("no arguments to group query");
		}
		
		if (args.containsKey("query")) {
			Filter q = QueryParser.parser.parse(args.get("query"));
			songs = FilterTracks.filter(q, songs);
		}
		/*
		meta=all
		type=music
		group-type=albums
		sort=album
		include-sort-headers=1
		query=(('com.apple.itunes.mediakind:1','com.apple.itunes.mediakind:32')+'daap.songalbum!:'
		 */
		
		if (!args.containsKey("group-type") || !args.get("group-type").equals("album")) {
			throw new IllegalArgumentException("trying to handle unknown group-type: " + args.get("group-type"));
		}
		
		Collections.sort(songs, new Comparator<Track>() {
			public int compare(Track o1, Track o2) {
				String a = (String)o1.getTag(DAAPConstants.ALBUM);
				String b = (String)o2.getTag(DAAPConstants.ALBUM);
				return a.compareTo(b);
			}
		});
		
		//TODO complete this
		
		//return DACPTreeBuilder.buildAlbumGroupResponse(songs);
		return null;
	}
}
