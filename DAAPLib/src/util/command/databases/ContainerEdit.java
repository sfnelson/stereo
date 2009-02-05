package util.command.databases;

import interfaces.DJInterface;
import interfaces.Track;
import interfaces.collection.Collection;

import java.util.List;
import java.util.Map;

import music.UserCollection;
import util.command.Command;
import util.queryparser.ApplyFilter;
import util.queryparser.Filter;
import util.queryparser.QueryParser;
import api.Response;

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

	public Response run(DJInterface dj) {

		UserCollection collection = null;
		for (Collection<? extends Track> c: dj.library().collections()) {
			if (c.id() == id) {
				collection = (UserCollection)c;
			}
		}
		if (collection == null) {
			throw new RuntimeException("collection not found (" + id + ")");
		}
		if (collection.editStatus() != Collection.EDITABLE) {
			throw new RuntimeException("collection is not editable (" + id + ")");
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
		}
		else if (action.equals("remove")) {
			//action=remove&edit-params='dmap.containeritemid:17667'
			Iterable<? extends Track> songs = collection.tracks();
			List<? extends Track> filtered = null;

			if (params != null) {
				Filter f = QueryParser.parse(params);
				System.out.println(f);
				filtered = ApplyFilter.filter(f, songs);
			}

			System.out.println("found " + filtered.size() + " to remove");

			collection.remove(filtered);
		}
		else if (action.equals("move")) {
			//action=move&edit-params='edit-param.move-pair:17978,17980'
			
			throw new RuntimeException("action: move is not yet implemented");
		}
		else {
			throw new RuntimeException("unknown action: " + action);
		}
		
		return new Response(null, Response.NO_CONTENT);
	}

}
