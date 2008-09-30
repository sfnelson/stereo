package daap;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import util.DACPConstants;

import interfaces.Album;

public class DAAPAlbum implements Album {

	private int id;
	private Map<Integer, Object> tags;
	
	private DAAPAlbum(int id) {
		this.id = id;
		this.tags = new HashMap<Integer, Object>();
	}
	
	public int getItems() {
		return (Integer)tags.get(DACPConstants.mimc);
	}
	
	public void setItems(int size) {
		tags.put(DACPConstants.mimc, size);
	}
	
	public Map<Integer, Object> getAllTags() {
		return tags;
	}

	public Object getTag(int keyId) {
		return tags.get(keyId);
	}
	
	private void addTag(int tagId, Object value) {
		tags.put(tagId, value);
	}
	
	private static int lastId = 0;
	static DAAPAlbum createAlbum(String name, String artist) {
		
		DAAPAlbum a = new DAAPAlbum(++lastId);
		a.addTag(DACPConstants.miid, a.id); //album id (local)
		
		int na = name.hashCode();
		int ar = artist.hashCode();
		
		byte[] persistant = new byte[8];
		persistant[0] = (byte)(na>>24 & 0xFF);
		persistant[1] = (byte)(na>>16 & 0xFF);
		persistant[2] = (byte)(na>>8  & 0xFF);
		persistant[3] = (byte)(na	  & 0xFF);
		persistant[4] = (byte)(ar>>24 & 0xFF);
		persistant[5] = (byte)(ar>>16 & 0xFF);
		persistant[6] = (byte)(ar>>8  & 0xFF);
		persistant[7] = (byte)(ar	  & 0xFF);
		
		long per = new BigInteger(persistant).longValue();
		
		a.addTag(DACPConstants.asai, per); //album id (persistant)
		a.addTag(DACPConstants.ALBUM, name);
		a.addTag(DACPConstants.ARTIST, artist);
		
		return a;
	}
	
	public static void main(String[] args) {
		DAAPAlbum album = DAAPAlbum.createAlbum("Eyes Open", "Snow Patrol");
		long id = (Long)album.getTag(DACPConstants.asai);
		System.out.println(id);
		System.out.println(new BigInteger("15739427192547115913").longValue());
		
	}

}
