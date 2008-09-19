package interfaces;

import java.io.IOException;
import java.io.InputStream;

public interface Track extends Element {
	
	/**Returns null if the connection closed*/
	public InputStream getStream() throws IOException;
	
	public Album getAlbum();
	public void setAlbum(Album album);
}
