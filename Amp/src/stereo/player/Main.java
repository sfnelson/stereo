package stereo.player;

import java.util.Set;

import stereo.dnssd.DNSSDProvider.Service;

import com.apple.dnssd.DNSSDException;

public class Main {

	public static void main(String args[]) throws InterruptedException, DNSSDException {

		String host = null;
		int port = 3689;

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--host") || args[i].equals("-h")) {
				if (i+1 < args.length) {
					host = args[i+1];
					i++;
				}
			}
			else if (args[i].equals("--port") || args[i].equals("-p")) {
				if (i+1 < args.length) {
					port = Integer.parseInt(args[i+1]);
					i++;
				}
			}
		}

		if (host == null) {
			System.out.println("Available stereo servers:");

			Set<Service> services = stereo.dnssd.DNSSD.impl().browse("_touch-able._tcp.");
			for (Service service: services) {
				System.out.printf("\t%s:%d (%s)\n", service.host, service.port, service.get("CtlN"));
			}
			switch (services.size()) {
			case 1:
				Service service = services.iterator().next();
				host = service.host;
				port = service.port;
				System.out.println("Connecting...");
				break;
			case 0:
				System.out.println("\tnone");
				break;
			default:
				System.out.println("Please select a server ("+services.size()+")");
			}
		}
		
		if (host != null) {
			StereoAmp amp = new StereoAmp(host, port);
			amp.start();
			amp.listen();
		}
		
	}
}
