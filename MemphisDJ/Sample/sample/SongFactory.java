package sample;



import java.util.Map;

import testing.DaapEntry;
import testing.DaapUtilities;

import memphis.stereo.backend.impl.DatabaseSong;
import memphis.stereo.sources.Source;
import memphis.stereo.songs.Song;
import memphis.stereo.songs.Metadata;
import memphis.stereo.songs.metadata.MetadataFactory;

public class SongFactory {
	
	public static final int ALBUM = DaapUtilities.stringToInt("asal");
	public static final int ARTIST = DaapUtilities.stringToInt("asar");
	public static final int BITRATE = DaapUtilities.stringToInt("asbr");
	public static final int COMPOSER = DaapUtilities.stringToInt("ascp");
	public static final int GENRE = DaapUtilities.stringToInt("asgn");
	public static final int NAME = DaapUtilities.stringToInt("minm");
	public static final int TIME = DaapUtilities.stringToInt("astm");

	/*
	private static final int getInteger(final int key, final Map<Integer, Object> map) {
		final Object o = map.get(key);
		if (o != null) {
			return (Integer)o;
		}
		
		return 0;
	}
	
	private static final short getShort(final int key, final Map<Integer, Object> map) {
		final Object o = map.get(key);
		if (o != null) {
			return (Short)o;
		}
		
		return 0;
	}
	*/
	
	private static final String getString(final int key, final Map<Integer, Object> map) {
		final Object o = map.get(key);
		if (o != null) {
			return (String)o;
		}
		
		return null;
	}

	private final Source source;
	
	public SongFactory(final Source source) {
		this.source = source;
	}

	public DaapSong constructSong(final DaapEntry e) {
		
		final Map<Integer, Object> values = e.getValueMap();
		
		int reference = (Integer)values.get(DaapUtilities.stringToInt("miid"));

		String title = SongFactory.getString(SongFactory.NAME, values);
		String artist = SongFactory.getString(SongFactory.ARTIST, values);
		String album = SongFactory.getString(SongFactory.ALBUM, values);
		String genre = SongFactory.getString(SongFactory.GENRE, values);
		//String composer = SongFactory.getString(SongFactory.COMPOSER, values);
		//short bitrate = SongFactory.getShort(SongFactory.BITRATE, values);
		//int time = SongFactory.getInteger(SongFactory.TIME, values);
		
		if (title == null && artist == null && album == null) {
			return null;
		}
		
		final Metadata metadata = MetadataFactory.factory().createMetadata(title, album, artist, genre);
		
		final DaapSong song = new DaapSong(this.source, MetadataFactory.factory().id(metadata), reference, metadata);
		
		return song;
	}
	
	public static String serializeSong(Metadata data) {
		
		String ret = "(";
		
		ret += serializeString(data.title().name()) + ", ";
		ret += serializeString(data.artist().name()) + ", ";
		ret += serializeString(data.album().name()) + ", ";
		ret += serializeString(data.genre().name()) + ", ";
		//ret += serializeString(""+data.playcount) + ", ";
		//ret += serializeString(""+data.rating);
		
		ret += ")";
		
		return ret;
	}
	
	public static String describeSerialSong() {
		return "(title, artist, album, genre)"; //, playcount, rating)";
	}
	
	private static String serializeString(String in) {
		if (in == null) return "\'\'";
		
		return '\'' + in.replaceAll("'", "\\\\'") + '\'';
	}
 }