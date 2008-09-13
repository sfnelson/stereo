package sample;



import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class CompositeEntry extends Entry {
	
	private Map<Integer, List<Entry>> map;
	
	public CompositeEntry(final String name, final int number, final short type, final int length) {
		super(name, number, type, length);
		
		this.map = new HashMap<Integer, List<Entry>>();
	}
	
	@Override
	public int count() {
		int ret = 1;
		for (final List<Entry> l: this.map.values()) {
			for (final Entry e: l) {
				ret += e.count();
			}
		}
		return ret;
	}
	
	public List<Entry> getEntries(final int key) {
		return this.map.get(key);
	}
	
	public Entry getFirst(final int key) {
		if (this.map.containsKey(key)) {
			return this.map.get(key).get(0);
		}

		return null;
	}
	
	@Override
	public Map<Integer, List<Entry>> getValue() {
		return this.map;
	}
	
	public Set<Integer> keySet() {
		return this.map.keySet();
	}
	
	public void put(final Integer key, final Entry value) {
		if (this.map.containsKey(key)) {
			this.map.get(key).add(value);
		}
		else {
			this.map.put(key, new LinkedList<Entry>());
			this.map.get(key).add(value);
		}
	}
	
	@Override
	public String toString() {
		String ret =  "\t" + this.name + " {\n";
		
		for (final Integer k: this.map.keySet()) {
			ret += "\t" + this.map.get(k) + "\n";
		}
		
		return ret + "\t}\n";
	}
}
