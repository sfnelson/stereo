package dmap.response;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import interfaces.Constants;
import api.Node;
import api.Reader;
import api.Response;
import api.Writer;

public class ContentCodes extends Response implements Iterable<Constants> {

	private final List<Constants> codes;
	
	public static ContentCodes read(Reader reader) {
		List<Constants> codes = new ArrayList<Constants>();
		for (Constants d: reader) {
			if (d == Constants.dmap_dictionary) {
				Reader dict = reader.nextComposite(d);
				int code = 0;
				String name = null;
				int type = 0;
				
				for (Constants c: dict) {
					switch (c) {
					case dmap_contentcodesnumber:
						code = dict.nextInteger(c);
						break;
					case dmap_contentcodesname:
						name = dict.nextString(c);
						break;
					case dmap_contentcodestype:
						type = dict.nextShort(c);
						break;
					}
				}
				
				Constants cons = Constants.get(code);
				if (cons == null) {
					throw new RuntimeException(name + " (" + code + ") not found");
				}
				if (!cons.longName.equals(name)) {
					throw new RuntimeException(name + " (" + code + ") does not match " + cons.longName);
				}
				if (cons.type != type) {
					throw new RuntimeException(name + " (" + code + ") has different type: " + cons.type  + " != " + type);
				}
				codes.add(cons);
			}
		}
		return new ContentCodes(codes);
	}
	
	public ContentCodes() {
		super(Constants.dmap_contentcodesresponse, Response.OK);
		
		codes = new ArrayList<Constants>();
		for (Constants c: Constants.values()) {
			codes.add(c);
		}
	}
	
	private ContentCodes(List<Constants> codes) {
		super(Constants.dmap_contentcodesresponse, Response.OK);
		
		this.codes = codes;
	}
	
	public void write(Writer writer) {
		super.write(writer);
		
		Code code = new Code();
		
		for (Constants c: codes) {
			code.code = c;
			writer.appendNode(code);
		}
	}
	
	public int size() {
		return codes.size();
	}
	
	public Iterator<Constants> iterator() {
		return codes.iterator();
	}
	
	private class Code implements Node {
		
		public Constants code;
		
		public Constants type() {
			return Constants.dmap_dictionary;
		}
		
		public void write(Writer writer) {
			writer.appendInteger(Constants.dmap_contentcodesnumber, code.code);
			writer.appendString(Constants.dmap_contentcodesname, code.longName);
			writer.appendShort(Constants.dmap_contentcodestype, (short)code.type);
		}
	}

}
