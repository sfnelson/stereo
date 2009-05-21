package stereo.daap.server;

import interfaces.Constants;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileFilter;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.Tag;

import stereo.dnssd.DNSSD;
import stereo.dnssd.DNSSDProvider;

public class Main {

	public static void main(String [] args) {

		Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);

		String path = "";

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--path") || args[i].equals("-p")) {
				if (i+1 < args.length) {
					path = args[i+1];
					i++;
				}
			}
		}

		File root = new File(path);
		if (root.isDirectory()) {
			System.out.println("Starting scan in " + root.getAbsolutePath());
		}
		else {
			System.err.println("Invalid directory: " + path);
			System.exit(1);
		}

		Set<File> files = new HashSet<File>();
		Stack<File> s = new Stack<File>();
		s.push(root);
		while (!s.isEmpty()) {
			File d = s.pop();

			//enqueue directories
			for (File f: d.listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					return pathname.isDirectory();
				}
			})) {
				s.push(f);
			}

			//store media files
			for (File f: d.listFiles(new AudioFileFilter())) {
				if (f.isDirectory()) continue;
				files.add(f);
			}
		}

		System.out.println("Found " + files.size() + " files");

		Set<Track> tracks = new HashSet<Track>();
		int size = files.size();
		int count = 0;
		for (File f: files) {

			try {
				int pid = f.getAbsoluteFile().hashCode();
				Track t = new Track(count, pid, AudioFileIO.read(f));
				tracks.add(t);
			} catch (Exception e) {
				//e.printStackTrace();
			}

			count++;
			if (count%(size/20)==0) {
				System.out.print(".");
			}
		}
		System.out.println();

		try {
			ServerSocket socket = new ServerSocket(0);

			String hostname = InetAddress.getLocalHost().getHostName();
			hostname = new Scanner(hostname).useDelimiter("[.]").next();

			String hash = Integer.toHexString(hostname.hashCode()).toUpperCase();
			hash = (hash+hash).substring(0,13);

			String name = "Stereo on " + hostname;

			HashMap<String, String> records = new HashMap<String, String>();
			records.put("txtvers", "1");
			records.put("Database ID", hash);
			records.put("Machine ID", hash);
			records.put("Machine Name", "Stereo on " + hostname);
			records.put("txtvers","1");
			records.put("iTSh Version", "131073");
			records.put("Version","196615");
			records.put("Password", "false");
			records.put("DbId", hash);
			DNSSDProvider.Service service = new DNSSDProvider.Service(name, "_daap._tcp", null, socket.getLocalPort(), records);

			System.out.println("Starting server...");
			new ServerSocketThread(socket, service).start();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static class ServerSocketThread extends Thread {
		private final ServerSocket socket;
		private final DNSSDProvider.Service service;

		public ServerSocketThread(ServerSocket socket, DNSSDProvider.Service service) {
			super("DAAP Server");
			this.socket = socket;
			this.service = service;
		}
		public void run() {
			DNSSD.impl().registerService(service);
			while (true) {
				System.out.println("Waiting for connection.");
				try {
					new Connection(socket.accept()).start();
				} catch (IOException e) {}
				System.out.println("Accepting connections.");
			}
			//DNSSD.impl().removeService(service);
		}
	}

	private static class Track extends interfaces.AbstractTrack {

		private final File f;

		public Track(int id, long pid, AudioFile f) {
			super(id, pid);
			this.f = f.getFile();

			Tag tag = f.getTag();

			put(Constants.daap_songalbum, tag.getFirstAlbum());
			put(Constants.daap_songartist, tag.getFirstArtist());
			put(Constants.daap_songcomment, tag.getFirstComment());
			put(Constants.daap_songgenre, tag.getFirstGenre());
			put(Constants.dmap_itemname, tag.getFirstTitle());

			Scanner track = new Scanner(tag.getFirstTrack());
			track.useDelimiter("/");
			if (track.hasNextShort()) put(Constants.daap_songtracknumber, track.nextShort());
			if (track.hasNextShort()) put(Constants.daap_songtrackcount, track.nextShort());

			Scanner year = new Scanner(tag.getFirstYear());
			if (year.hasNextShort()) put(Constants.daap_songyear, year.nextShort());

			AudioHeader audio = f.getAudioHeader();
			put(Constants.daap_songtime, audio.getTrackLength());
			put(Constants.daap_songbitrate, (short)audio.getBitRateAsNumber());
			put(Constants.daap_songformat, audio.getFormat());
			put(Constants.daap_songsamplerate, audio.getSampleRateAsNumber());

			String type = audio.getEncodingType();
			int codec = 0;
			if (type.equals("mp3")) {
				codec = stringToCode("mpeg");
			}
			else if (type.equals("m4a") || type.equals("m4p")) {
				codec = stringToCode("mp4a");
			}
			else if (type.equals("ogg")) {
				codec = stringToCode("ogg"+'\0');
			}
			else if (type.equals("flac")) {
				codec = stringToCode("flac");
			}
			if (codec != 0) {
				put(Constants.daap_songcodectype, codec);
			}
		}

		public void getStream(StreamReader reader) throws IOException {
			reader.read(new FileInputStream(f));
		}
	}
	
	private static int stringToCode(String code) {
		char[] b = code.toCharArray();
		int v = 0;
		for (int i = 0; i < 4; i++) {
			v <<= 8;
			v += b[i] & 255;
		}
		return v;
	}

	private static class Connection extends Thread {

		public Connection(Socket sock) {
			super("DAAP Connection Worker");

			//sock.getInputStream();
		}

	}
}
