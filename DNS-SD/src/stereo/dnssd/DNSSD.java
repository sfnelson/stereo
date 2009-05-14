package stereo.dnssd;

import java.util.Iterator;

import javax.imageio.spi.ServiceRegistry;

public class DNSSD {

	public static DNSSDProvider impl() {
		for (Iterator<DNSSDProvider> it = ServiceRegistry.lookupProviders(DNSSDProvider.class); it.hasNext();) {
			try {
				DNSSDProvider server = it.next();
				return server;
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}
}
