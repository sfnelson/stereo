package dmap.node;

import interfaces.Constants;
import interfaces.Track;

import java.util.HashMap;
import java.util.Map.Entry;

import api.Node;
import api.Reader;
import api.Writer;

public class TrackNode implements Node {

	public final Track track;

	public TrackNode(Track track) {
		this.track = track;
	}
	
	public static TrackNode read(Reader reader, Track.TrackFactory factory) {
		
		HashMap<Constants, Object> values = new HashMap<Constants, Object>();
		
		for (Constants code: reader) {
			
			switch (code.type) {
			case 1: values.put(code, reader.nextByte(code)); break;
			case 3: values.put(code, reader.nextShort(code)); break;
			case 5: values.put(code, reader.nextInteger(code)); break;
			case 7: values.put(code, reader.nextLong(code)); break;
			case 9: values.put(code, reader.nextString(code)); break;
			default:
				System.err.println("Unknown or unexpected type: " + code.type + " for " + code + " in TrackNode.read()");
			}
		}
		
		Integer id = (Integer)values.get(Constants.dmap_itemid);
		Long pid = (Long)values.get(Constants.dmap_persistentid);
		
		if (id == null || pid == null) {
			System.err.println("Track without id or pid");
			return null;
		}
		
		Track track = factory.create(id, pid);
		for (Entry<Constants, Object> e: values.entrySet()) {
			track.put(e.getKey(), e.getValue());
		}
		return new TrackNode(track);
	}

	public Constants type() {
		return Constants.dmap_listingitem;
	}

	public void write(Writer writer) {
		for(Constants code : track.getAllTags()) {
			Object value = track.get(code);
			switch (code.type) {
			case 1: writer.appendByte(code, (Byte)value); break;
			case 3: writer.appendShort(code, (Short)value); break;
			case 5: writer.appendInteger(code, (Integer)value); break;
			case 7: writer.appendLong(code, (Long)value); break;
			case 9: writer.appendString(code, (String)value); break;
			default:
				System.err.println("Unknown or unexpected type: " + code.type + " for " + code + " in TrackNode.write()");
			}
		}
	}
	
}
