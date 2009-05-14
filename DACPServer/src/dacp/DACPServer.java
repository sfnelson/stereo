package dacp;

import interfaces.DJInterface;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

import reader.DACPRequestParser;
import spi.StereoServer;
import stereo.dnssd.DNSSD;
import stereo.dnssd.DNSSDProvider;
import util.command.Command;
import writer.DACPResponseGenerator;
import api.Response;

public class DACPServer implements StereoServer {

	private int PORT;
	private ServerSocket SERVER_SOCK;
	private DJInterface dj;

	private DACPResponseGenerator printer = new DACPResponseGenerator();

	public void start(DJInterface dj, String[] args) {

		this.PORT = (args.length>1)?new Scanner(args[1]).nextInt():3689;
		this.dj = dj;

		try {
			SERVER_SOCK = new ServerSocket(PORT);
			System.out.println("Server starting.\n--------\n");
			new ServerSocketThread().start();

			String hostname = InetAddress.getLocalHost().getHostName();
			hostname = new Scanner(hostname).useDelimiter("[.]").next();

			String hash = Integer.toHexString(hostname.hashCode()).toUpperCase();
			hash = (hash+hash).substring(0,13);

			HashMap<String, String> records = new HashMap<String, String>();
			records.put("CtlN","Stereo on " + hostname);
			records.put("OSsi","0x1F6");
			records.put("Ver","131073");
			records.put("txtvers","1");
			records.put("DvTy","iTunes");
			records.put("DvSv","2049");
			records.put("DbId", hash);
			DNSSDProvider.Service service = new DNSSDProvider.Service(hash, "_touch-able._tcp.", null, PORT, records);

			DNSSD.impl().registerService(service);
		}
		catch (IOException ex) {
			ex.printStackTrace();
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

							Response content = s.run(dj);

							if (content != null) {
								printer.success(content, sock.getOutputStream());
							}
							else {
								printer.error("204 No Content", sock.getOutputStream());
							}

						}
						else {
							System.out.println("No command to execute for " + parseText);
						}
					}
					catch (Exception ex) {
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
