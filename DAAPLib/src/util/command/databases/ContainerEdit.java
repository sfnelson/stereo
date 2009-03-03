package util.command.databases;

import interfaces.Constants;
import interfaces.DJInterface;
import interfaces.Track;
import interfaces.collection.Collection;
import interfaces.collection.EditableSource;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

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

	@SuppressWarnings("unchecked")
	public Response run(DJInterface dj) {

		Collection<? extends Track> collection = null;
		for (Collection<? extends Track> c: dj.library().collections()) {
			if (c.id() == id) {
				collection = c;
				break;
			}
		}
		if (collection == null) {
			throw new RuntimeException("collection not found (" + id + ")");
		}
		if (collection.editStatus() != Collection.EDITABLE) {
			throw new RuntimeException("collection is not editable (" + id + ")");
		}
		
		EditableSource<Track> source = (EditableSource<Track>)collection.source();

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

			source.appendAll(filtered);
		}
		else if (action.equals("remove")) {
			//action=remove&edit-params='dmap.containeritemid:17667'
			Iterable<? extends Track> songs = source.tracks();
			List<? extends Track> filtered = null;

			if (params != null) {
				Filter f = QueryParser.parse(params);
				System.out.println(f);
				filtered = ApplyFilter.filter(f, songs);
			}

			System.out.println("found " + filtered.size() + " to remove");

			source.removeAll(filtered);
		}
		else if (action.equals("move")) {
			//action=move&edit-params='edit-param.move-pair:17978,17980'
			if (params.startsWith("'edit-param.move-pair:")) {
				Scanner sc = new Scanner(params.substring(22, params.length()-1));
				sc.useDelimiter("[,']");
				int target = sc.nextInt();
				int marker = sc.nextInt();
				
				Track t = null;
				Track m = null;
				
				for (Track i: source.tracks()) {
					Integer id = (Integer)i.get(Constants.dmap_containeritemid);
					if (id == null);
					else if (id.intValue() == target) {
						t = i;
					}
					else if (id.intValue() == marker) {
						m = i;
					}
				}
				
				if (t != null) {
					System.out.println("moving " + t + " to " + m);
					source.move(t, m);
				}
				else {
					System.err.println("could not find " + target);
				}
			}
		}
		else {
			throw new RuntimeException("unknown action: " + action);
		}
		
		return new Response(null, Response.NO_CONTENT);
	}

}
