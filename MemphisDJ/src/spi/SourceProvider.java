package spi;

import api.tracks.Track;
import interfaces.Library;

public interface SourceProvider {

	public void create(Library<? extends Track> library);
	public void connect(String url);
	
}
