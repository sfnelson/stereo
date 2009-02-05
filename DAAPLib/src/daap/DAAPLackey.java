package daap;

import interfaces.Library;
import interfaces.Track;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;


public class DAAPLackey {

	public static final int HANDSHAKE_PORT = 8080;
	public static final int DAAP_PORT = 3689;

	private final Set<DAAPClient> clients;
	
	private final Library<? extends Track> library;

	public DAAPLackey(Library<? extends Track> library) {

		this.library = library;
		this.clients = new HashSet<DAAPClient>();

		new HandshakeThread().start();
		new PollThread().start();
	}
	
	private synchronized void add(DAAPClient client) {
		clients.add(client);
		library.addSource(client);
		library.addCollection(client);
	}
	
	private synchronized void remove(DAAPClient client) {
		library.removeCollection(client);
		library.removeSource(client);
		clients.remove(client);
	}
	
	private synchronized List<DAAPClient> clients() {
		return new ArrayList<DAAPClient>(clients);
	}
	
	private class HandshakeThread extends Thread {
		public void run() {
			ServerSocket connection = null;
			try {
				connection = new ServerSocket(HANDSHAKE_PORT);
			}
			catch (IOException ex) {
				System.err.println("Unable to bind handshake port");
				ex.printStackTrace();
				System.exit(1);
			}

			while (true) {
				Socket client = null;
				try {
					client = connection.accept();
					new ConnectionThread(client).start();
				}
				catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	private class ConnectionThread extends Thread {
		private Socket socket;
		public ConnectionThread(Socket s) {
			super("DAAP Connection Thread");
			this.socket = s;
		}
		public void run() {
			try {
				Scanner sc = new Scanner(socket.getInputStream());
				String line = sc.nextLine();
				if (line.equals("GET /")) {
					String server = socket.getInetAddress().getHostAddress();
					
					List<DAAPClient> pls =  DAAPClient.create(server, DAAP_PORT, library.nextCollectionId());
					
					for (DAAPClient c: pls) {
						add(c);
					}
				}
			}
			catch (IOException ex) {
				System.err.println("Error connecting to daap server: " + ex.getMessage());
			}
			
			try {
				socket.close();
			}
			catch (IOException ex) {
				System.err.println("Error closing connection: " + ex.getMessage());
			}
		}
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
						System.err.println("Unable to update client, closing (" + client.name() + ")");
						ex.printStackTrace();
						
						client.close();
						remove(client);
					}
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					//swallow
				}
			}
		}
	}

}
