package stereo.dnssd;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public interface DNSSDProvider {
	
	public void registerListener(ServiceListener listener);
	public void removeListener(ServiceListener listener);
	
	public void registerService(Service service);
	public void removeService(Service service);
	
	public Set<Service> browse(String type) throws InterruptedException;
	public Set<Service> browse(String type, long time) throws InterruptedException;

	interface ServiceListener {
		public String type();
		public void serviceAvailable(Service service);
		public void serviceUnavailable(Service service);
	}
	
	class Service implements Iterable<String> {
		public final String name;
		public final String type;
		public final String host;
		public final int port;
		
		private Map<String, String> records;
		
		public Service(String type, int port) {
			this.name = null;
			this.type = type;
			this.host = null;
			this.port = port;
			
			records = new HashMap<String, String>();
		}
		
		public Service(String name, String type, String host, int port, Map<String, String> records) {
			this.name = name;
			this.type = type;
			this.host = host;
			this.port = port;
			
			this.records = records;
		}
		
		public boolean equals(Object o) {
			if (o == this) return true;
			if (!o.getClass().equals(this.getClass())) return false;
			Service that = (Service)o;
			if (this.name == null) {
				return that.name == null && this.type.equals(that.type);
			}
			else {
				return this.type.equals(that.type) && this.name.equals(that.name);
			}
		}
		
		public int hashCode() {
			return type.hashCode() ^ ((name!=null)?name.hashCode():0);
		}
		
		public String get(String key) {
			return records.get(key);
		}
		
		public Iterator<String> iterator() {
			return records.keySet().iterator();
		}
		
		public String toString() {
			return this.name + " on " + this.host + ":" + this.port + "(" + this.type + ")";
		}
	}
}
