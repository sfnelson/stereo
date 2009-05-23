package util.queryparser;

import api.tracks.HasMetadata;

public interface Filter {

	public boolean check(HasMetadata e);
	
}
