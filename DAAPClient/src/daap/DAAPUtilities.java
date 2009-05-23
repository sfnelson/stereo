package daap;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import api.Constants;
import api.tracks.Track;

public class DAAPUtilities {

	public static final String SERVER_INFO_REQUEST = "server-info";
	public static final String LOGIN_REQUEST = "login";
	public static final String UPDATE_REQUEST = "update";
	public static final String DATABASE_REQUEST = "databases";

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

	public void readSong(int db, int song, Track.StreamReader reader) throws IOException {

		String request = DATABASE_REQUEST + "/" + db + "/items/" + song + ".mp3"
		+ "?session-id=" + session;

		requestStream(request, reader);
	}

	private DAAPEntry getProperty(String request, Constants property) throws IOException {

		DAAPEntry response = requestDAAP(request);

		if (response != null) {
			return getProperty(response, property);
		}
		else {
			return null;
		}
	}

	private final HttpClient client = new DefaultHttpClient();
	private synchronized DAAPEntry requestDAAP(String request) throws IOException {
		String requestURI = path + "/" + request;

		HttpGet method = new HttpGet(requestURI);

		// Execute the method.
		HttpResponse response = client.execute(method);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode/100 != HttpStatus.SC_OK/100) {
			System.err.println("Method failed: " + response.getStatusLine());
			return null;
		}

		// Read the response body.
		HttpEntity entity = response.getEntity();
		DAAPEntry entry = DAAPEntry.parseStream(entity.getContent());
		entity.consumeContent();
		
		return entry;
	}
	
	private synchronized void requestStream(String request, Track.StreamReader reader) throws IOException {
		String requestURI = path + "/" + request;

		HttpGet method = new HttpGet(requestURI);

		// Execute the method.
		HttpResponse response = client.execute(method);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode/100 != HttpStatus.SC_OK/100) {
			System.err.println("Method failed: " + response.getStatusLine());
			return;
		}

		// Read the response body.
		HttpEntity entity = response.getEntity();
		reader.read(entity.getContent());
		entity.consumeContent();
	}
}
