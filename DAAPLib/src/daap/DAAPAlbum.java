package daap;

import interfaces.AbstractAlbum;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import util.DACPConstants;

public class DAAPAlbum extends AbstractAlbum {

	private DAAPAlbum(int id, long persistentId) {
		super(id, persistentId);
	}
	
	public int getItems() {
		return (Integer)get(DACPConstants.mimc);
	}
	
	public void setItems(int size) {
		put(DACPConstants.mimc, size);
	}
	
	private static int lastId = 0;
	private static Map<Long, DAAPAlbum> albums = new HashMap<Long, DAAPAlbum>();
	
	static DAAPAlbum createAlbum(String name, String artist) {
		
		if (name == null) name = "";
		if (artist == null) artist = "";
		
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
		
		int id = ++lastId;
		long per = new BigInteger(persistant).longValue();
		
		if (albums.containsKey(per)) {
			return albums.get(per);
		}
		else {
			DAAPAlbum a = new DAAPAlbum(id, per);
			a.put(DACPConstants.miid, id); //album id (local)
			a.put(DACPConstants.asai, per); //album id (persistant)
			a.put(DACPConstants.ALBUM, name);
			a.put(DACPConstants.ARTIST, artist);

			albums.put(per, a);
			
			return a;
		}
	}
	
	public static void main(String[] args) {
		DAAPAlbum album = DAAPAlbum.createAlbum("Eyes Open", "Snow Patrol");
		long id = (Long)album.get(DACPConstants.asai);
		System.out.println(id);
		System.out.println(new BigInteger("15739427192547115913").longValue());
		
	}

}
