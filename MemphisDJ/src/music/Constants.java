package music;

public class Constants {
	
	public static final int ITEM_KIND = parseInt("mikd");
	public static final int TRACK_ID = parseInt("miid");
	public static final int NAME = parseInt("minm");
	public static final int ARTIST = parseInt("asar");
	public static final int PERSISTENT_ID = parseInt("mper");
	public static final int ALBUM = parseInt("asal");
    public static final int BITRATE = parseInt("asbr");
    public static final int COMPOSER = parseInt("ascp");
    public static final int GENRE = parseInt("asgn");
    public static final int SONG_TIME = parseInt("astm");
    public static final int START_TIME = parseInt("asst");
    public static final int STOP_TIME = parseInt("assp");
    
    
    protected static int parseInt(String code) {
        return readInteger(code.toCharArray());
    }
    
    private static int readInteger(char[] b) {
        int size = 0;
        for (int i = 0; i < 4; i++) {
            size <<= 8;
            size += b[i] & 255;
        }
        return size;
    }
}
