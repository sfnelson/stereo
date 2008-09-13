package interfaces;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface Track {

	public Object getTag(int tagID);
	public int getTrackId();
	
	/**Returns null if the connection closed*/
	public InputStream getStream() throws IOException;

	public Map<Integer, Object> getAllTags();
	
}
