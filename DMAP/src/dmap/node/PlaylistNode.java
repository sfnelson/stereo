package dmap.node;

import interfaces.Constants;
import interfaces.Track;
import interfaces.collection.Collection;
import interfaces.collection.EditableCollection;
import api.Node;
import api.Reader;
import api.Writer;

public class PlaylistNode implements Node {

	private final Collection<? extends Track> collection;
	
	public interface PlaylistFactory {
		public EditableCollection<? extends Track> create(int id, long pid);
	}

	public static PlaylistNode read(Reader reader, PlaylistFactory factory) {
		
		int id = 0;
		long pid = 0;
		String name = null;
		boolean root = false;
		@SuppressWarnings("unused")
		boolean generated = false;
		int editStatus = 0;
		int parent = 0;
		int size = 0;
		
		for (Constants code: reader) {
			switch (code) {
			case dmap_itemid: id = reader.nextInteger(code); break;
			case dmap_persistentid: pid = reader.nextLong(code); break;
			case dmap_itemname: name = reader.nextString(code); break;
			case daap_baseplaylist: root = reader.nextBoolean(code); break;
			case com_apple_itunes_smartPlaylist: generated = reader.nextBoolean(code); break;
			case dmap_editstatus: editStatus = reader.nextInteger(code); break;
			case dmap_parentcontainerid: parent = reader.nextInteger(code); break;
			case dmap_itemcount: size = reader.nextInteger(code); break;
			}
		}

		EditableCollection<? extends Track> collection = factory.create(id, pid);
		
		collection.setName(name);
		collection.setRoot(root);
		collection.setEditStatus(editStatus);
		collection.setParentId(parent);
		collection.setSize(size);
		
		return new PlaylistNode(collection);
	}

	public PlaylistNode(Collection<? extends Track> collection) {
		this.collection = collection;
	}
	
	public Collection<? extends Track> collection() {
		return collection;
	}
	
	public Constants type() {
		return Constants.dmap_listingitem;
	}
	
	public void write(Writer writer) {

		writer.appendInteger(Constants.dmap_itemid, collection.id());
		writer.appendLong(Constants.dmap_persistentid, collection.persistentId());
		writer.appendString(Constants.dmap_itemname, collection.name());
		
		if (collection.isRoot()) {
			writer.appendBoolean(Constants.daap_baseplaylist, collection.isRoot());
		}
		
		if (collection.editStatus() == Collection.GENERATED) {
			writer.appendBoolean(Constants.com_apple_itunes_smartPlaylist, true);
		}
		writer.appendInteger(Constants.dmap_editstatus, collection.editStatus());

		Collection<? extends Track> par = collection.parent();
		int pid = (par == null)?0:par.id();
		writer.appendInteger(Constants.dmap_parentcontainerid, pid);
		writer.appendInteger(Constants.dmap_itemcount, collection.size());
	}
}
