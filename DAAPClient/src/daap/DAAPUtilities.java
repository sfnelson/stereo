package daap;

import interfaces.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class DAAPUtilities {

	public static final String SERVER_INFO_REQUEST = "server-info";
	public static final String LOGIN_REQUEST = "login";
	public static final String UPDATE_REQUEST = "update";
	public static final String DATABASE_REQUEST = "databases";

	private final HttpClient  client;	
	private final HttpClient  clientSong;

	private final String path;

	private int session;

	public static DAAPEntry getProperty(DAAPEntry response, Constants property) throws IOException {

		for (DAAPEntry e : response.children()) {
			if (e.code() == property) {
				return e;
			}
		}

		return null;
	}

	public DAAPUtilities(final String path) {
		this.path = path;

		client = new DefaultHttpClient();
		clientSong = new DefaultHttpClient();
	}

	public String connect() throws IOException {

		String name = (String)getProperty(SERVER_INFO_REQUEST, Constants.dmap_itemname).value();
		if (name == null) throw new IOException("Database name not found");

		DAAPEntry session = getProperty(LOGIN_REQUEST, Constants.dmap_sessionid);
		if (session == null) throw new IOException("Unsuccessful login: no id returned");

		this.session = (Integer)session.value();

		return name;
	}

	public int update(int revision) throws IOException {

		String request = UPDATE_REQUEST + "?session-id=" + session;
		if (revision > 0) request += "&revision-id=" + revision;

		DAAPEntry nr = getProperty(request, Constants.dmap_serverrevision);
		if (nr == null) throw new IOException("Unsuccessful update: no revision returned");

		return (Integer)nr.value();
	}

	public List<DAAPEntry> databases(int revision) throws IOException {

		String request = DATABASE_REQUEST + "?session-id=" + session + "&revision-id=" + revision;

		DAAPEntry response = getProperty(request, Constants.dmap_listing);
		if (response == null) throw new IOException("Unsuccessful database request: no databases returned");

		List<DAAPEntry> dbs = new ArrayList<DAAPEntry>();
		for (DAAPEntry e: response.children()) {
			dbs.add(e);
		}

		return dbs;
	}

	public List<DAAPEntry> tracks(int dbid, int revision) throws IOException {

		String request = DATABASE_REQUEST + "/" + dbid + "/items?"
		+ "type=music&meta=dmap.itemkind,dmap.itemid,dmap.itemname,daap.songalbum,daap.songartist,daap.songgenre,daap.songcomposer,daap.songbitrate,daap.songsamplerate,daap.songstarttime,daap.songstoptime,daap.songtime"
		+ "&session-id=" + session
		+ "&revision-id=" + revision;

		DAAPEntry response = getProperty(request, Constants.dmap_listing);
		if (response == null) throw new IOException("Unsuccessful database request: no tracks returned");

		List<DAAPEntry> tracks = new ArrayList<DAAPEntry>();
		for (DAAPEntry e: response.children()) {
			tracks.add(e);
		}

		return tracks;
	}

	public InputStream song(int db, int song) throws IOException {

		String request = DATABASE_REQUEST + "/" + db + "/items/" + song + ".mp3"
		+ "?session-id=" + session;

		return songRequest(request);
	}

	private DAAPEntry getProperty(String request, Constants property) throws IOException {

		DAAPEntry response = request(request);

		return getProperty(response, property);
	}

	private DAAPEntry request(String request) throws IOException {
		return DAAPEntry.parseStream(request(client, path, request));
	}

	private InputStream songRequest(String request) throws IOException {
		return request(clientSong, path, request);
	}

	private static InputStream request(HttpClient client, String path, String request) throws IOException {
		String requestURI = path + "/" + request;

		HttpGet method = new HttpGet(requestURI);

		// Execute the method.
		HttpResponse response = client.execute(method);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode/100 != HttpStatus.SC_OK/100) {
			System.err.println("Method failed: " + response.getStatusLine());
		}

		// Read the response body.
		return response.getEntity().getContent();
	}
}
