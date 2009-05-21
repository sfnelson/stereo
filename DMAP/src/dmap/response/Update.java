package dmap.response;

import interfaces.Constants;
import api.Reader;
import api.Response;
import api.Writer;

public class Update extends Response {

	private final int revision;
	
	public static Update read(Reader reader) {
		for (Constants c: reader) {
			switch (c) {
			case dmap_serverrevision:
				return new Update(reader.nextInteger(c));
			}
		}
		
		throw new RuntimeException("server revision not found");
	}
	
	public Update(int revision) {
		super(Constants.dmap_updateresponse, Response.OK);
		
		this.revision = revision;
	}
	
	public int revision() {
		return revision;
	}
	
	public void write(Writer writer) {
		super.write(writer);
		
		writer.appendInteger(Constants.dmap_serverrevision, revision);
	}
}
