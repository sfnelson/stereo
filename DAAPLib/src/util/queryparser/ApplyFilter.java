package util.queryparser;

import interfaces.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ApplyFilter {

	public static <E extends Element> List<E> filter(Filter filter, List<E> items) {
		
		List<E> es = new ArrayList<E>();
		es.addAll(items);
		
		for (Iterator<E> it = es.iterator(); it.hasNext();) {
			E e = it.next();
			
			if (!filter.check(e)) {
				it.remove();
			}
		}
		
		return es;
	}
}
