package stereo.dnssd;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.apple.dnssd.BrowseListener;
import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDException;
import com.apple.dnssd.DNSSDRegistration;
import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.RegisterListener;
import com.apple.dnssd.ResolveListener;
import com.apple.dnssd.TXTRecord;

public final class AppleDNSSDImpl implements stereo.dnssd.DNSSDProvider {

	private static Map<Object, Set<DNSSDService>> browsers = new HashMap<Object, Set<DNSSDService>>();
	
	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				for (Set<DNSSDService> services: browsers.values()) {
					for (DNSSDService service: services) service.stop();
				}
			}
		});
	}
	
	public Set<Service> browse(String type) throws InterruptedException {
		return browse(type, 2000);
	}

	public Set<Service> browse(final String type, final long time)
			throws InterruptedException {
		
		final Set<Service> services = new HashSet<Service>();
		ServiceListener listener = new ServiceListener() {
			public String type() {
				return type;
			}
			public void serviceAvailable(Service service) {
				services.add(service);
			}
			public void serviceUnavailable(Service service) {
				services.remove(service);
			}
		};
		
		registerListener(listener);
		
		Thread.sleep(time);
		
		removeListener(listener);
		
		return services;
	}

	public void registerListener(ServiceListener listener) {
		try {
			DNSSDService browser = DNSSD.browse(listener.type(), new Resolver(listener));
			registered(listener, browser);
		}
		catch (DNSSDException ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
	}

	public void registerService(Service service) {
		TXTRecord record = new TXTRecord();
		for (String key: service) {
			try {
				record.set(key, service.get(key).getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				record.set(key, service.get(key));
			}
		}
		try {
			DNSSDService browse = DNSSD.register(
					0, //flags: 0 or NO_AUTO_RENAME
					0, //interface id, or 0
					null, //name or null (host name)
					service.name, //type
					null, //domain
					null, //host
					service.port, //port
					record,
					new RegisterListener() {
						public void serviceRegistered(DNSSDRegistration arg0,
								int flags, String name, String type, String domain) {
							System.out.printf("registed %s successfully: %s\n", type, name);
						}
						public void operationFailed(DNSSDService arg0, int code) {
							System.err.println("error registering service (" + code + ")");
						}
						
					}
				);
			registered(service, browse);
		} catch (DNSSDException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void removeListener(ServiceListener listener) {
		removed(listener);
	}

	public void removeService(Service service) {
		removed(service);
	}
	
	private void registered(Object listener, DNSSDService service) {
		Set<DNSSDService> services;
		if (!browsers.containsKey(listener)) {
			services = new HashSet<DNSSDService>(1);
			browsers.put(listener, services);
		}
		else {
			services = browsers.get(listener);
		}
		services.add(service);
	}
	
	private void removed(Object listener) {
		Set<DNSSDService> services = browsers.remove(listener);
		if (services == null) return;
		
		for (DNSSDService service: services) {
			service.stop();
		}
	}
	
	private class Resolver implements BrowseListener, ResolveListener {
		
		private final ServiceListener listener;
		
		public Resolver(ServiceListener listener) {
			this.listener = listener;
		}

		public void serviceFound(DNSSDService browser, int flags, int ifIndex, 
				String serviceName, String regType, String domain) {

			try {
				DNSSDService service = DNSSD.resolve(flags, ifIndex, serviceName, regType, domain, this);
				registered(listener, service);
			} catch (DNSSDException e) {
				e.printStackTrace();
			}

		}

		public void serviceLost(DNSSDService browser, int flags, int ifIndex,
				String serviceName, String regType, String domain) {}
		public void operationFailed(DNSSDService browser, int flags) {}

		public void serviceResolved(DNSSDService browser, int flags, int ifIndex,
				String fullName, String hostName, int port, TXTRecord record) {

			HashMap<String, String> records = new HashMap<String, String>();
			for (int i = 0; i < record.size(); i++) {
				try {
					records.put(record.getKey(i), new String(record.getValue(i), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					records.put(record.getKey(i), record.getValueAsString(i));
				}
			}
			listener.serviceAvailable(new Service(listener.type(), hostName, port, records));
		}
	}
}
