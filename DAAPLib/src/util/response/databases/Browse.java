package util.response.databases;

import interfaces.Constants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import api.Node;
import api.Reader;
import api.Response;
import api.Writer;

public class Browse extends Response implements Iterable<String> {

	private final Constants content;
	private final Listing elements;
	
	public static Browse read(Reader reader) {
		
		Listing elements = null;
		Constants content = null;
		
		for (Constants code: reader) {
			switch (code) {
			case daap_browseartistlisting:
			case daap_browsealbumlisting:
			case daap_browsegenrelisting:
			case daap_browsecomposerlisting:
				content = code;
				elements = Listing.read(reader.nextComposite(code), code);
				break;
			}
		}
		
		if (content == null) throw new RuntimeException("element not found");
		
		return new Browse(content, elements);
	}
	
	public Browse(Constants content, List<String> elements) {
		super(Constants.daap_databasebrowse, Response.OK);
		
		this.content = content;
		this.elements = new Listing(elements, content);
	}
	
	private Browse(Constants content, Listing elements) {
		super(Constants.daap_databasebrowse, Response.OK);
		
		this.content = content;
		this.elements = elements;
	}
	
	public Constants content() {
		return content;
	}
	
	public Iterator<String> iterator() {
		return elements.list.iterator();
	}
	
	public int size() {
		return elements.list.size();
	}
	
	public void write(Writer writer) {
		super.write(writer);
		
		writer.appendInteger(Constants.dmap_updatetype, 0);
		writer.appendInteger(Constants.dmap_specifiedtotalcount, size());
		writer.appendInteger(Constants.dmap_returnedcount, size());
		writer.appendNode(elements);
		
		//TODO append index
	}
	
	private static class Listing implements Node {
		public final List<String> list;
		public final Constants type;
		
		public static Listing read(Reader reader, Constants type) {
			List<String> list = new ArrayList<String>();
			for (Constants code: reader) {
				if (code == Constants.dmap_listingitem) {
					list.add(reader.nextString(code));
				}
			}
			return new Listing(list, type);
		}
		
		public Listing(List<String> list, Constants type) {
			this.list = list;
			this.type = type;
		}
		public Constants type() {
			return type;
		}
		public void write(Writer writer) {
			for (String s: list) {
				writer.appendString(Constants.dmap_listingitem, s);
			}
		}
	} 
}

/*
public static Node buildBrowseResponse(int code, List<String> artists) throws UnsupportedEncodingException {

	Composite response = createResponse(DACPConstants.abro);

	Composite list = createList(response, code, 0, artists.size());

	for (String s: artists) {
		list.append(new StringNode(DACPConstants.mlit, s));
	}
	
	response.append(buildIndex(artists));

	return response;
}

private static Node buildIndex(List<String> items) throws UnsupportedEncodingException {

Composite index = new Composite(DACPConstants.mshl);

char current = 0;
int count = 0;
int offset = 0;

for (String s: items) {
	
	if (s.charAt(0) == current) {
		count++;
	}
	else {
		if (current != 0) {
			Composite item = new Composite(DACPConstants.mlit);
			item.append(new StringNode(DACPConstants.mshc, "\0"+current));
			item.append(new IntegerNode(DACPConstants.mshi, offset));
			item.append(new IntegerNode(DACPConstants.mshn, count));
			index.append(item);
		}
		
		current = s.charAt(0);
		offset += count;
		count = 0;
	}
}

if (current != 0) {
	Composite item = new Composite(DACPConstants.mlit);
	item.append(new StringNode(DACPConstants.mshc, "\0"+current));
	item.append(new IntegerNode(DACPConstants.mshi, offset));
	item.append(new IntegerNode(DACPConstants.mshn, count));
	index.append(item);
}

return index;
}
*/