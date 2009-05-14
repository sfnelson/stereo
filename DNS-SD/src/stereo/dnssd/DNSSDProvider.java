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
		public final String host;
		public final int port;
		
		private Map<String, String> records;
		
		public Service(String name, String host, int port) {
			this.name = name;
			this.host = host;
			this.port = port;
			
			records = new HashMap<String, String>();
		}
		
		public Service(String name, String host, int port, Map<String, String> records) {
			this.name = name;
			this.host = host;
			this.port = port;
			
			this.records = records;
		}
		
		public boolean equals(Object o) {
			if (o == this) return true;
			if (!o.getClass().equals(this.getClass())) return false;
			Service that = (Service)o;
			return this.name.equals(that.name)
				&& this.host.equals(that.host)
				&& this.port == that.port;
		}
		
		public int hashCode() {
			return name.hashCode() ^ host.hashCode() ^ port;
		}
		
		public String get(String key) {
			return records.get(key);
		}
		
		public Iterator<String> iterator() {
			return records.keySet().iterator();
		}
	}
}
