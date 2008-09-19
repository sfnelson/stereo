package daap;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.SimpleLog;



public class DAAPClient {

	private String hostname;
	
	private String dbname;

	private int id;
	
	private int dbid;

	private int revisionNumber;

	private Log log;

	private int sessionID;

	private int port;

	DAAPUtilities helper;

	public DAAPClient(String hostname, int port, int id) throws IOException {
		//This method should login and get a session ID,
		//the database ID (dbid) by updating and currentVersion [of database]
		this.hostname = hostname;
		this.port = port;
		this.id = id;

		log = new SimpleLog("Log");

		try {
			helper = new DAAPUtilities(hostname, port, log);
		} catch (IOException e) {
			System.err.println("*Probably* an invalid host");
			//e.printStackTrace();
		}
		//Get a session Id
		getSessionDetails();
		sessionID = getSessionID();
		revisionNumber = getRevisionNumber();
		dbid = getDatabaseId();

	}
	
	/**
	 * This should be the name of the library, but for now is the hostname
	 * TODO: use the library name rather than the hostname
	 * @return
	 */
	public String getName() {
		return dbname;
	}
	
	public int id() {
		return id;
	}
	
	public long getPersistantId() {
		return (((long)hostname.hashCode())<<32)|((long)dbname.hashCode());
	}
	

	public void getTracks(DAAPPlaylist tracks) {	
		if(dbid < 0) return;
		
		String request = "databases/"
			+ dbid
			+ "/items?type=music&meta=dmap.itemkind,dmap.itemid,dmap.itemname,daap.songalbum,daap.songartist,daap.songgenre,daap.songcomposer,daap.songbitrate,daap.songsamplerate,daap.songstarttime,daap.songstoptime,daap.songtime&session-id="
			+ sessionID + "&revision-id=" + revisionNumber;

		InputStream in = null;
		
		try {
			in = helper.request(hostname, port, request, log);
			DAAPEntry entry = DAAPEntry.parseStream(in, helper.types);

			if ((entry == null)
					|| (entry.getName() != DAAPUtilities.stringToInt("adbs"))) {
				return;
			}

			for (DAAPEntry e : entry) {
				if (e.getName() == DAAPUtilities.stringToInt("mlcl")) {
					entry = e;
					break;
				}
			}

			tracks.clear();
			
			for (DAAPEntry e : entry) {
				if ((entry == null) || !entry.hasChildren()) {
					continue;
				}
				final Map<Integer, Object> values = e.getValueMap();
				tracks.add(new DAAPTrack(values, this));

			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			//helper.release(in);
		}
	}

	public InputStream getStream(DAAPTrack track) throws IOException {
		int song = track.getId();
		return helper.songRequest(hostname, port, "databases/" + dbid + "/items/" + song
				+ ".mp3?session-id=" + sessionID, log);
	}

	public boolean isUpdated() throws ClientExpiredException{
		int newRevisionNumber = getRevisionNumber();
		if(newRevisionNumber == -1) throw new ClientExpiredException();
		if(revisionNumber != newRevisionNumber){
			revisionNumber = newRevisionNumber;
			return true;
		}else{
			return false;
		}
	}

	private int getSessionDetails() {
		String serverInfoRequest = "server-info";
		InputStream in = null;

		try {
			in = helper.request(hostname, port, serverInfoRequest, log);
			DAAPEntry entry = DAAPEntry.parseStream(in, helper.types);

			for (DAAPEntry e : entry) {
				switch(e.getName()) {
				case DAAPConstants.minm:
					dbname = (String) e.getValue();
					break;
				}
			}

			return -1;
		} catch (IOException e) {
			return -1;
		}finally{
			//helper.release(in);
		}

	}
	
	private int getSessionID() {
		String loginRequest = "login";
		InputStream in = null;

		try {
			in = helper.request(hostname, port, loginRequest, log);
			DAAPEntry entry = DAAPEntry.parseStream(in, helper.types);

			for (DAAPEntry e : entry) {
				if (e.getName() == DAAPUtilities.stringToInt("mlid")) {
					return (Integer) e.getValue();
				}
			}

			return -1;
		} catch (IOException e) {
			return -1;
		}finally{
			//helper.release(in);
		}

	}

	private int getRevisionNumber() {
		if(sessionID < 0) return -1;
		
		InputStream in = null;

		String request = "update?session-id=" + sessionID;

		try {
			in = helper.request(hostname, port, request, log);
			DAAPEntry entry = DAAPEntry.parseStream(in, helper.types);

			for (DAAPEntry e : entry) {
				if (e.getName() == DAAPUtilities.stringToInt("musr")) {
					return (Integer) e.getValue();
				}
			}
			return -1;
		} catch (IOException e) {
			return -1;
		}finally{
			//helper.release(in);
		}
	}

	private int getDatabaseId() {
		InputStream in = null;


		if(revisionNumber < 0) return -1;

		String request = "databases?session-id=" + sessionID + "&revision-id="
		+ revisionNumber;


		try {
			in = helper.request(hostname, port, request, log);
			DAAPEntry entry = DAAPEntry.parseStream(in, helper.types);

			for (DAAPEntry e : entry) {
				if (e.getName() == DAAPUtilities.stringToInt("mlcl")) {
					entry = e;
					break;
				}
			}

			for (DAAPEntry e : entry) {
				if (e.getName() == DAAPUtilities.stringToInt("mlit")) {
					entry = e;
					break;
				}
			}

			for (DAAPEntry e : entry) {
				if (e.getName() == DAAPUtilities.stringToInt("miid")) {
					entry = e;
					return (Integer) e.getValue();
				}
			}

			return -1;

		} catch (IOException e) {
			return -1;
		}finally{
			//helper.release(in);
		}

	}
	
	public class ClientExpiredException extends Throwable{
		private static final long serialVersionUID = 1L;
	}
}
