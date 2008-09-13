package util.queryparser;

import interfaces.Track;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class FilterTracks {

	public static List<Track> filter(Filter filter, List<Track> tracks) {
		
		List<Track> tks = new ArrayList<Track>();
		tks.addAll(tracks);
		
		for (Iterator<Track> it = tks.iterator(); it.hasNext();) {
			Track t = it.next();
			
			if (!filter.check(t)) {
				it.remove();
			}
		}
		
		return tks;
	}
}
