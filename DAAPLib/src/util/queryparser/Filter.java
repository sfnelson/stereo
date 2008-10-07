package util.queryparser;

import interfaces.HasMetadata;

public interface Filter {

	public boolean check(HasMetadata e);
	
}
