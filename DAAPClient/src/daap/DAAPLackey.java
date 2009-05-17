package daap;

import interfaces.Library;
import interfaces.Track;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spi.SourceProvider;
import stereo.dnssd.DNSSD;
import stereo.dnssd.DNSSDProvider;
import stereo.dnssd.DNSSDProvider.Service;

/**
 * DAAPLackey polls DAAP libraries for changes and updates the DJ's library.
 */
public class DAAPLackey implements SourceProvider {

	private Set<String> hosts;
	private Map<DAAPClient, String> clients;
	
	private Library<? extends Track> library;

	public void create(Library<? extends Track> library) {
		this.library = library;
		this.hosts = new HashSet<String>();
		this.clients = new HashMap<DAAPClient, String>();

		new PollThread().start();
		
		DNSSD.impl().registerListener(new DNSSDProvider.ServiceListener() {

			public void serviceAvailable(Service service) {
				connect(service.name, "daap://"+service.host+":"+service.port);
			}

			public void serviceUnavailable(Service service) {}

			public String type() {
				return "_daap._tcp";
			}
			
		});
	}
	
	public synchronized void connect(final String name, final String path) {
		
		if (clients.containsKey(name)) return;
		
		if (!path.substring(0, 4).equals("daap")) return;
			
		final String pth = "http"+path.substring(4);
		
		new Thread("DAAP Connection Thread") {
			public void run() {
				
				try {
					List<DAAPClient> pls =  DAAPClient.create(pth, library.nextCollectionId());

					for (DAAPClient c: pls) {
						add(name, c);
					}
				}
				catch (IOException ex) {
					System.err.println("Error connecting to daap server: " + ex.getMessage());
				}
				
			}
		}.start();
	}
	
	private synchronized void add(String name, DAAPClient client) {
		hosts.add(name);
		clients.put(client, name);
		library.addSource(client);
		library.addCollection(client.collection());
	}
	
	private synchronized void remove(DAAPClient client) {
		library.removeCollection(client.collection());
		library.removeSource(client);
		clients.remove(client);
		hosts.remove(client);
	}
	
	private synchronized List<DAAPClient> clients() {
		return new ArrayList<DAAPClient>(clients.keySet());
	}

	private class PollThread extends Thread {
		public void run() {
			while (true) {

				List<DAAPClient> clients = clients();
				
				for (DAAPClient client: clients) {
					try {
						client.update();
					}
					catch (IOException ex) {
						System.err.println("Unable to update client, closing (" + client.collection().name() + ")");
						ex.printStackTrace();
						
						client.close();
						remove(client);
					}
				}
				
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					//swallow
				}
			}
		}
	}

}
