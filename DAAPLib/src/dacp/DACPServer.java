package dacp;

import interfaces.ControlServerCreator;
import interfaces.DJInterface;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Set;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import music.DJ;
import reader.DACPRequestParser;
import util.command.Command;
import util.node.Node;
import writer.DACPResponseGenerator;


public class DACPServer {

	private final int PORT;

	private final ServerSocket SERVER_SOCK;

	private final DJInterface dj;

	private final DACPResponseGenerator printer = new DACPResponseGenerator();

	public DACPServer(int port, DJInterface dj) throws IOException {
		this.PORT = port;
		this.dj = dj;
		SERVER_SOCK = new ServerSocket(PORT);
		System.out.println("Server starting.\n--------\n");
		new ServerSocketThread().start();

		Set<InetAddress> addresses = new HashSet<InetAddress>();

		for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements();) {
			NetworkInterface i = e.nextElement();
			for (Enumeration<InetAddress> f = i.getInetAddresses(); f.hasMoreElements();) {
				InetAddress a = f.nextElement();
				if (a.isAnyLocalAddress()) continue;
				if (a.isLoopbackAddress()) continue;

				//TODO obviously, we should support ipv6 :)
				if (a instanceof Inet4Address) addresses.add(a);
			}
		}

		String hostname = InetAddress.getLocalHost().getHostName();
		String hash = Integer.toHexString(hostname.hashCode()).substring(0, 13).toUpperCase();
		
		System.out.println("registering mDNS for " + hostname + " (" + hash + ")");
		
		Hashtable<String, String> records = new Hashtable<String, String>();

		records.put("CtlN","Memphis Stereo");
		records.put("OSsi","0x1F6");
		records.put("Ver","131072");
		records.put("txtvers","1");
		records.put("DvTy","iTunes");
		records.put("DvSv","1905");
		records.put("DbId", hash);

		ServiceInfo dmcp = ServiceInfo.create("_touch-able._tcp.local.", hash, 3689, 0, 0, records);

		InetAddress a = Inet4Address.getAllByName(hostname)[1];
		InetAddress addr = Inet4Address.getByAddress(hostname, a.getAddress());
		final JmDNS mdns = JmDNS.create(addr);
		System.out.println("creating hook for " + addr.getHostAddress());
		Runtime.getRuntime().addShutdownHook(new Thread("mDNS Shutdown Hook") {
			public void run() {
				System.out.println("removing services");
				mdns.unregisterAllServices();
				System.out.println("removed services");
				mdns.close();
				System.out.println("done");
			}
		});
		mdns.registerService(dmcp);
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

						printer.error("501 Not Implemented", sock.getOutputStream());
					}
					catch (NullPointerException ex) {
						printer.visit(null, sock.getOutputStream());
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

	public static void register() {
		DJ.registerServerCreator(new DACPServerCreator());
	}

	private static class DACPServerCreator implements ControlServerCreator {
		public void create(DJInterface dj) {
			try {
				new DACPServer(3689, dj);
			}
			catch (IOException ex) {
				System.err.println("Unable to create DACP Server");
				ex.printStackTrace();
			}
		}
	}
}
