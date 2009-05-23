package api.tracks;

import api.Constants;

public interface HasMetadata {
	
	public Object get(Constants tagID);
	public Iterable<Constants> getAllTags();
	
}
