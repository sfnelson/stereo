package dacp;

import interfaces.DJInterface;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Set;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import reader.DACPRequestParser;
import util.command.Command;
import util.node.Node;
import writer.DACPResponseGenerator;


public class DACPServer {

	private final int PORT;

	private final ServerSocket SERVER_SOCK;

	private final DJInterface dj;

	private final DACPResponseGenerator printer = new DACPResponseGenerator();

	public DACPServer(String device, int port, DJInterface dj) throws IOException {
		this.PORT = port;
		this.dj = dj;
		SERVER_SOCK = new ServerSocket(PORT);
		System.out.println("Server starting.\n--------\n");
		new ServerSocketThread().start();

		Set<InetAddress> addresses = new HashSet<InetAddress>();

		addresses.add(InetAddress.getByName(device));

		String hostname = InetAddress.getLocalHost().getHostName();
		hostname = new Scanner(hostname).useDelimiter("[.]").next();
		
		String hash = Integer.toHexString(hostname.hashCode()).toUpperCase();
		hash = (hash+hash).substring(0,13);
		
		System.out.println("registering mDNS for " + hostname + " (" + hash + ")");
		
		Hashtable<String, String> records = new Hashtable<String, String>();

		records.put("CtlN","Stereo on " + hostname);
		records.put("OSsi","0x1F6");
		records.put("Ver","131073");
		records.put("txtvers","1");
		records.put("DvTy","iTunes");
		records.put("DvSv","2049");
		records.put("DbId", hash);

		ServiceInfo dmcp = ServiceInfo.create("_touch-able._tcp.local.", hash, PORT, 0, 0, records);

		for (InetAddress a: addresses) {
			final JmDNS mdns = JmDNS.create(a);
			System.out.println("binding on " + a);
			mdns.registerService(dmcp);
		}
	}

	private class ServerSocketThread extends Thread {
		public ServerSocketThread() {
			super("DACP Server");
		}
		public void run() {
			while (true) {
				System.out.println("Waiting for connection.");
				try {
					new ServerRunnable(SERVER_SOCK.accept()).start();
				} catch (IOException e) {}
				System.out.println("Accepting connections.");
			}
		}
	}

	private class ServerRunnable extends Thread {

		private final Socket sock;

		public void run() {
			try {
				Scanner scan = new Scanner(sock.getInputStream());

				while (scan.hasNextLine()){
					String parseText="";
					while(true){
						String current = scan.nextLine();
						if(current.equals("")) break;
						parseText+=current;
					}

					try {
						Command s = DACPRequestParser.parse(parseText);
						
						if (s != null) {
							Node reply = s.run(dj);
							printer.visit(reply, sock.getOutputStream());
						}
						else {
							System.out.println("No command to execute for " + parseText);
						}
					}
					catch (IllegalArgumentException ex) {
						ex.printStackTrace();

						printer.error("204 No Content", sock.getOutputStream());
					}

				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		private ServerRunnable(Socket s) {
			this.sock = s;
		}

	}
}
