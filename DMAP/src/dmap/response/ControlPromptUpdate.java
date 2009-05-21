package dmap.response;

import interfaces.Constants;
import api.Reader;
import api.Response;
import api.Writer;

public class ControlPromptUpdate extends Response {

	private final int promptId;
	
	public static ControlPromptUpdate read(Reader reader) {
		for (Constants code: reader) {
			switch (code) {
			case dmap_itemid:
				return new ControlPromptUpdate(reader.nextInteger(code));
			}
		}
		
		return null;
	}
	
	public ControlPromptUpdate(int promptId) {
		super(Constants.dmcp_controlprompt, Response.OK);
		this.promptId = promptId;
	}
	
	public int promptId() {
		return promptId;
	}
	
	public void write(Writer writer) {
		super.write(writer);
		
		writer.appendInteger(Constants.dmap_itemid, promptId);

	}
}
