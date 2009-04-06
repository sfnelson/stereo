package api;

import interfaces.Constants;

public interface Node {
	
	public Constants type();
	
	public void write(Writer writer);

}
