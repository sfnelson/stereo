package daap;

import interfaces.Library;
import interfaces.Track;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * DAAPLackey polls DAAP libraries for changes and updates the DJ's library.
 */
public class DAAPLackey {

	private final Set<DAAPClient> clients;
	
	private final Library<? extends Track> library;
	
	private static DAAPLackey lackey;
	public static DAAPLackey lackey() {
		return lackey;
	}

	public DAAPLackey(Library<? extends Track> library) {

		lackey = this;
		
		this.library = library;
		this.clients = new HashSet<DAAPClient>();

		new PollThread().start();
	}
	
	public void request(final String host, final int port) {
		
		new Thread("DAAP Connection Thread") {
			public void run() {
				
				try {
					List<DAAPClient> pls =  DAAPClient.create(host, port, library.nextCollectionId());

					for (DAAPClient c: pls) {
						add(c);
					}
				}
				catch (IOException ex) {
					System.err.println("Error connecting to daap server: " + ex.getMessage());
				}
				
			}
		}.start();
	}
	
	private synchronized void add(DAAPClient client) {
		clients.add(client);
		library.addSource(client);
		library.addCollection(client.collection());
	}
	
	private synchronized void remove(DAAPClient client) {
		library.removeCollection(client.collection());
		library.removeSource(client);
		clients.remove(client);
	}
	
	private synchronized List<DAAPClient> clients() {
		return new ArrayList<DAAPClient>(clients);
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
