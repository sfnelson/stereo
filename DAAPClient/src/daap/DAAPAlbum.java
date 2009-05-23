package daap;


import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import common.AbstractAlbum;

public class DAAPAlbum extends AbstractAlbum {

	private DAAPAlbum(int id, long persistentId, String name, String artist) {
		super(id, persistentId, name, artist, 0);
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
			DAAPAlbum album = albums.get(per);
			album.setTracks(album.tracks()+1);
			return album;
		}
		else {
			DAAPAlbum a = new DAAPAlbum(id, per, name, artist);
			a.setTracks(1);

			albums.put(per, a);
			
			return a;
		}
	}
}
