package spi;

import interfaces.Library;
import interfaces.Track;

public interface SourceProvider {

	public void create(Library<? extends Track> library);
	public void connect(String url);
	
}
