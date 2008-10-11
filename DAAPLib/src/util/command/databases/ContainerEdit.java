package util.command.databases;

import interfaces.DJInterface;
import interfaces.collection.Collection;

import java.util.List;
import java.util.Map;

import music.Track;
import music.UserCollection;
import util.command.Command;
import util.node.Node;
import util.queryparser.ApplyFilter;
import util.queryparser.Filter;
import util.queryparser.QueryParser;

//  /databases/1/containers/[id]/edit?action=add&edit-params='dmap.itemid:[id]' -> need to decode response

public class ContainerEdit implements Command {

	private Map<String, String> args;
	private int id;

	public ContainerEdit(int id) {

		this.id = id;
	}

	public void init(Map<String, String> args) {
		this.args = args;
	}

	public Node run(DJInterface dj) {

		UserCollection collection = null;
		for (Collection<? extends Track> c: dj.library().collections()) {
			if (c.id() == id) {
				collection = (UserCollection)c;
			}
		}
		if (collection == null) {
			throw new RuntimeException("collection not found (" + id + ")");
		}

		String action = args.get("action");
		String params = args.get("edit-params");

		if (action.equals("add")) {
			Iterable<? extends Track> songs = dj.library().tracks();
			List<? extends Track> filtered = null;

			if (params != null) {
				Filter f = QueryParser.parse(params);
				System.out.println(f);
				filtered = ApplyFilter.filter(f, songs);
			}

			System.out.println("found " + filtered.size() + " to add");

			collection.add(filtered);

			return null;
		}
		else {
			throw new RuntimeException("unknown action: " + action);
		}
	}

}
