package reader;

import interfaces.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import music.Track.TrackFactory;
import util.response.ControlPromptUpdate;
import util.response.CtrlInt;
import util.response.Databases;
import util.response.Login;
import util.response.PlaylistSongs;
import util.response.ServerInfo;
import util.response.Update;
import util.response.ctrlint.GetProperty;
import util.response.ctrlint.PlayStatusUpdate;
import util.response.databases.Browse;
import util.response.databases.Groups;
import util.response.databases.Playlists;
import api.Reader;
import api.Response;
import api.nodes.AlbumNode.AlbumFactory;
import api.nodes.PlaylistNode.PlaylistFactory;
import dacp.DACPReader;


public abstract class DACPResponseParser {

	public final Response request(String host, int port, String request) throws IOException, UnknownHostException {

		Socket socket = new Socket(host, port);
		PrintStream out = new PrintStream(socket.getOutputStream());
		out.print("GET " + request + " HTTP/1.1\r\n\r\n");
		InputStream input = socket.getInputStream();

		String buffer = "";
		while (true) {
			char c = (char) input.read();
			buffer += c;
			if (c == '\n' && buffer.endsWith("\r\n\r\n"))
				break;
		}

		Scanner sc = new Scanner(buffer);

		String protocol = sc.next();
		int status = sc.nextInt();
		String code = sc.next();

		if (!protocol.equals("HTTP/1.1"))
			throw new IOException("Unsupported Protocol: " + protocol);
		if (status/100 != 2)
			throw new IOException("Error connecting to server: " + status + " " + code);

		if (!sc.nextLine().equals(""))
			throw new IOException("Expected empty line after header");

		//TODO use these fields for sanity check
		@SuppressWarnings("unused")
		String contentType, date, version = null;
		
		int available = 0;
		String line;

		while (sc.hasNextLine()) {
			line = sc.nextLine();
			if (line.equals(""))
				break;

			Scanner lineScanner = new Scanner(line);
			String token = lineScanner.next();
			if (token.equals("Content-Type:")) {
				contentType = lineScanner.next();
			}
			else if (token.equals("Content-Length:")) {
				available = lineScanner.nextInt();
			}
			else if (token.equals("DAAP-Server:")) {
				version = lineScanner.next();
			}
			else if (token.equals("Date:")) {
				date = lineScanner.next();
			}
		}
		
		return parse(input, available);
	}
	
	public Response parse(InputStream input, int available) {
		Reader reader = new DACPReader(input, available);
		
		for (Constants c: reader) {
			if (!reader.hasNextComposite(c)) {
				throw new RuntimeException("unexpected node " + c.longName);
			}
			
			Reader node = reader.nextComposite(c);
			
			switch (c) {
			case dmap_serverinforesponse:
				return ServerInfo.read(node);
			case dmap_loginresponse:
				return Login.read(node);
			case dacp_controlint:
				return CtrlInt.read(node);
			case dmcp_getpropertyresponse:
				return GetProperty.read(node);
			case daap_serverdatabases:
				return Databases.read(node);
			case dmap_updateresponse:
				return Update.read(node);
			case dmcp_status:
				return PlayStatusUpdate.read(node);
			case daap_databasebrowse:
				return Browse.read(node);
			case dmcp_controlprompt:
				return ControlPromptUpdate.read(node);
			case daap_databaseplaylists:
				return Playlists.read(node, playlistFactory());
			case daap_playlistsongs:
				return PlaylistSongs.read(node, trackFactory());
			case daap_albumgrouping:
				return Groups.read(node, albumFactory());
			case daap_songgrouping:
				return Groups.read(node, trackFactory());
			default:
				throw new RuntimeException("unexpected node " + c.longName);
			}
		}
		
		throw new RuntimeException("empty response");
	}
	
	public abstract PlaylistFactory playlistFactory();
	public abstract TrackFactory trackFactory();
	public abstract AlbumFactory albumFactory();

}