package api;

import api.Constants;

public interface Node {
	
	public Constants type();
	
	public void write(Writer writer);

}
