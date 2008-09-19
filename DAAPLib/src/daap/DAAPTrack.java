package daap;

import interfaces.Album;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import util.DACPConstants;

public class DAAPTrack implements interfaces.Track {
	
	private Map<Integer, Object> tags;
	private DAAPClient publisher;
	private Album album;
	
	public DAAPTrack(Map<Integer, Object> values, DAAPClient parent){
		tags = new HashMap<Integer, Object>();
		
		for (Integer key: values.keySet()) {
			tags.put(key, values.get(key));
		}
		
		publisher = parent;
	}

	/**
	 * tagId should take a value from those available in Track
	 * @param tagID
	 * @return
	 */
	public Object getTag(int tagID){
		return tags.get(tagID);
	}

	public int getId() {
		return (Integer)tags.get(DAAPConstants.TRACK_ID);
	}
	
	public DAAPClient getParent(){
		return publisher;
	}
	
	/**Returns null if the connection closed*/
	public InputStream getStream() throws IOException{
		return publisher.getStream(this);
	}
	
	public String toString(){
		 //return String.format("%s - %s - %s",getTag(Constants.NAME),getTag(Constants.ARTIST),getTag(Constants.ALBUM));
		 return String.format("%s",getTag(DAAPConstants.NAME));
	}

	public Map<Integer, Object> getAllTags() {
		return tags;
	}

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
		this.tags.put(DACPConstants.asai, album.getTag(DACPConstants.asai));
	}
	
}
