package dmap.response.databases;

import interfaces.Constants;
import api.Reader;
import api.Response;
import api.Writer;

public class NewPlaylist extends Response {

	private final int id;
	
	public static NewPlaylist read(Reader reader) {
		for (Constants c: reader) {
			switch (c) {
			case dmap_itemid:
				return new NewPlaylist(reader.nextInteger(c));
			}
		}
		throw new RuntimeException("item id not found in response");
	}
	
	public NewPlaylist(int id) {
		super(Constants.dmap_editdictionary, Response.OK);
		
		this.id = id;
	}
	
	public int id() {
		return id;
	}
	
	public void write(Writer writer) {
		super.write(writer);
		
		writer.appendInteger(Constants.dmap_itemid, id);
	}

}
