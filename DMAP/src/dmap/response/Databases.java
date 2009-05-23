package dmap.response;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import api.Constants;
import api.Node;
import api.Reader;
import api.Response;
import api.Writer;

public class Databases extends Response {
	
	private final List<Database> databases;
	
	public static Databases read(Reader reader) {
		
		List<Database> databases = new ArrayList<Database>();
		
		for (Constants code: reader) {
			if (code == Constants.dmap_listing) {
				Reader list = reader.nextComposite(code);
				
				for (Constants itemCode: list) {
					if (itemCode == Constants.dmap_listingitem) {
						Reader item = list.nextComposite(itemCode);
						
						Database database = Database.read(item);
						databases.add(database);
					}
				}
			}
		}
		
		return new Databases(databases);
	}
	
	public Databases(int id, long persistentId, String name, int tracks, int containers) {
		super(Constants.daap_serverdatabases, Response.OK);
		
		databases = new ArrayList<Database>();
		databases.add(new Database(id, persistentId, name, tracks, containers, 3));
	}
	
	private Databases(List<Database> databases) {
		super(Constants.daap_serverdatabases, Response.OK);
		
		this.databases = databases;
	}
	
	public void write(Writer writer) {
		
		writer.appendList(Constants.dmap_listing, (byte)0, databases);
		
	}
	
	public Iterator<Database> databases() {
		return databases.iterator();
	}
	
	public int size() {
		return databases.size();
	}

	public static class Database implements Node {

		private final int id;
		private final long persistentId;
		private final String name;
		private final int tracks;
		private final int containers;
		private final int editStatus;
		
		public static Database read(Reader reader) {
			int id = 0;
			long persistentId = 0;
			String name = null;
			int tracks = 0;
			int containers = 0;
			int editStatus = 0;
			
			for (Constants node: reader) {
				switch (node) {
				case dmap_itemid: id = reader.nextInteger(node); break;
				case dmap_persistentid: persistentId = reader.nextLong(node); break;
				case dmap_itemname: name = reader.nextString(node); break;
				case dmap_itemcount: tracks = reader.nextInteger(node); break;
				case dmap_containercount: containers = reader.nextInteger(node); break;
				case dmap_editstatus: editStatus = reader.nextInteger(node); break;
				}
			}
			
			return new Database(id, persistentId, name, tracks, containers, editStatus);
		}
		
		public Database(int id, long persistentId, String name, int tracks, int containers, int editStatus) {
			this.id = id;
			this.persistentId = persistentId;
			this.name = name;
			this.tracks = tracks;
			this.containers = containers;
			this.editStatus = editStatus;
		}
		
		public Constants type() {
			return Constants.dmap_listingitem;
		}
		
		public void write(Writer writer) {
			writer.appendInteger(Constants.dmap_itemid, id);
			writer.appendLong(Constants.dmap_persistentid, persistentId);
			writer.appendString(Constants.dmap_itemname, name);
			writer.appendInteger(Constants.dmap_itemcount, tracks);
			writer.appendInteger(Constants.dmap_containercount, containers);
			writer.appendInteger(Constants.dmap_editstatus, editStatus);
		}
		
		public boolean equals(Object o) {
			if (o == this) return true;
			if (!(o instanceof Database)) return false;
			
			Database that = (Database)o;
			
			return this.id == that.id
				&& this.persistentId == that.persistentId;
		}
		
		public int id() {
			return id;
		}

		public long persistentId() {
			return persistentId;
		}

		public String name() {
			return name;
		}

		public int tracks() {
			return tracks;
		}

		public int containers() {
			return containers;
		}
		
		public int editStatus() {
			return editStatus;
		}
	}
}

/*
public static Node buildDatabaseResponse(int items, int containers) throws UnsupportedEncodingException {

Composite response = createResponse(DACPConstants.avdb);

Composite list = createList(response, DACPConstants.mlcl, 0, 1);

Composite item = new Composite(DACPConstants.mlit);
list.append(item);

item.append(new IntegerNode(DACPConstants.miid, 1));
item.append(new LongNode(DACPConstants.mper, 0xf35226b7c8ee14d3l)); //this id matches the id in the mdns broadcast
item.append(new StringNode(DACPConstants.minm, "Memphis Stereo"));
item.append(new IntegerNode(DACPConstants.mimc, items)); //item count
item.append(new IntegerNode(DACPConstants.mctc, containers)); //container count

return response;
}
*/