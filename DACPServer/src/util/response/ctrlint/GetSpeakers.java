package util.response.ctrlint;

import interfaces.Constants;
import api.Response;
import api.Writer;

public class GetSpeakers extends Response {

	public GetSpeakers() {
		super(Constants.dacp_speakers, Response.OK);
	}
	
	public void write(Writer writer) {
		
		writer.appendNode(new api.Node() {
			public void write(Writer dict) {
				dict.appendLong(Constants.dmap_speakermachineaddress, 0);
				dict.appendBoolean(Constants.dacp_isavailable, true);
				dict.appendString(Constants.dmap_itemname, "Computer");
			}
			public Constants type() {
				return Constants.dmap_dictionary;
			}
		});
	}
}