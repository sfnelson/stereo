package daap;

import interfaces.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class DAAPUtilities {

	public static final String SERVER_INFO_REQUEST = "server-info";
	public static final String LOGIN_REQUEST = "login";
	public static final String UPDATE_REQUEST = "update";
	public static final String DATABASE_REQUEST = "databases";
	
	private final HttpClient  client;	
	private final HttpClient  clientSong;
	
	private final String hostname;
	private final int port;
	
	private int session;
	
	public static DAAPEntry getProperty(DAAPEntry response, Constants property) throws IOException {
		
		for (DAAPEntry e : response.children()) {
			if (e.code() == property) {
				return e;
			}
		}
		
		return null;
	}
	
	public DAAPUtilities(final String hostname, final int port) {
		this.hostname = hostname;
		this.port = port;
		
		client = new HttpClient();
		clientSong = new HttpClient();
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
		return DAAPEntry.parseStream(request(client, hostname, port, request));
	}
	
	private InputStream songRequest(String request) throws IOException {
		return request(clientSong, hostname, port, request);
	}
	
	private static InputStream request(HttpClient client, String hostname, int port, String request) throws IOException {
		String requestURI = "http://" + hostname + ":" + port + "/" + request;
		
		HttpMethod method = new GetMethod(requestURI);
		
		InputStream responseBody = null;
		
		// Provide custom retry handler is necessary
	    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
	    		new DefaultHttpMethodRetryHandler(3, false));
	    
	    try {
	        // Execute the method.
	        int statusCode = client.executeMethod(method);

	        if (statusCode/100 != HttpStatus.SC_OK/100) {
	          System.err.println("Method failed: " + method.getStatusLine());
	        }

	        // Read the response body.
	        responseBody = method.getResponseBodyAsStream();
	        
	        return responseBody;

	    } catch (HttpException e) {
	        System.err.println("Fatal protocol violation: " + e.getMessage());
	        e.printStackTrace();
	    }

	    return null;
	}
}
