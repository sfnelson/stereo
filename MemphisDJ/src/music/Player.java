package music;

import interfaces.Constants;
import interfaces.Track;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import notification.AbstractEventGenerator;
import notification.PlayerListener;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class Player extends AbstractEventGenerator<PlayerListener> implements interfaces.Player {

	private byte state = STOPPED;
	
	public void pause() {
		Current c = current();
		if (c != null) {
			state = PAUSED;
		}
	}

	public void setTrack(Track t) {
		setTrackWithoutStarting(t);
		start();
	}

	public byte setTrackWithoutStarting(Track t) {
		System.out.println("Setting the track on the player: " + t);

		Current c = new Current(t);
		current(c);
		
		byte oldState = state;
		state = STOPPED;
		return oldState;
	}
	
	public boolean setTrackKeepStatus(Track t) {
		byte oldStatus = setTrackWithoutStarting(t);
		if (oldStatus == PLAYING) {
			start();
			return false;
		}
		else {
			return true;
		}
	}

	public void start() {
		Current c = current();
		if (c == null); //No current track, so do nothing
		else if (c.paused()) {
			c.play();
			state = PLAYING;
		}
		else { //Was stopped
			c.start();
			state = PLAYING;
		}
	}

	public void stop() {
		Current c = current();
		if (c != null){
			c = null;
			state = STOPPED;
		}
	}

	public byte status() {
		return state;
	}

	public int elapsed() {
		Current current = current();
		if (current != null) {
			return current.elapsed();
		}
		else {
			return 0;
		}
	}

	public byte[] getAlbumArt() {
		Current current = current();
		if (current != null) {
			System.out.println("retrieving album art for " + current.track);
			return current.image();
		}
		else {
			return null;
		}
	}
	
	public byte[] getCurrentSong() {
		Current current = current();
		if (current != null) {
			System.out.println("retrieving song data for " + current.track);
			return current.data();
		}
		else {
			return null;
		}
	}

	@SuppressWarnings("unused")
	private void trackFinished() {
		Thread t = new Thread() {
			public void run() {
				for (PlayerListener l: listeners())
					l.playbackFinished();
			}
		};
		t.start();
	}

	private void trackStarted() {
		Thread t = new Thread() {
			public void run() {
				for (PlayerListener l: listeners())
					l.playbackStarted();
			}
		};
		t.start();
	}

	private synchronized Current current() {
		return _current;
	}
	
	private synchronized void current(Current current) {
		this._current = current;
	}

	private volatile Current _current;

	private class Current implements Track.StreamReader {

		private final Track track;
		private byte[] data;
		private byte[] imageArray;

		public Current(Track track) {

			this.track = track;
			
			try {
				new Thread() {
					public void run() {
						getAlbumArt();
					}
				}.start();
				
				track.getStream(this);
				start();
				
			} catch (IOException e) {
				System.err.println("Unable to get track stream, failing");
				e.printStackTrace();
				data = new byte[0];
				return;
			}

		}
		
		public void read(InputStream stream) throws IOException {

				if (stream == null) {
					data = new byte[0];
					return;
				}
				
				ByteArrayOutputStream in = new ByteArrayOutputStream();
				
				byte[] buf = new byte[256];
				while (true) {
					int read = stream.read(buf, 0, 256);
					
					if (read > 0) in.write(buf, 0, read);
					
					if (read == -1) break;
				}
				
				data = in.toByteArray();
				
		}
		
		private long started;
		private long elapsed;
		
		private boolean paused = false;
		
		public boolean paused() {
			return paused;
		}

		public void start() {
			trackStarted();
			started = System.currentTimeMillis();
		}
		
		public void pause() {
			elapsed = System.currentTimeMillis()-started;
			paused = true;
		}
		
		public void play() {
			started = System.currentTimeMillis();
			paused = false;
		}
		
		public int elapsed() {
			return (int)((paused?0:System.currentTimeMillis()-started)+elapsed);
		}
		
		public byte[] data() {
			return data;
		}
		
		public synchronized byte[] image() {
			return imageArray;
		}
		
		private synchronized void getAlbumArt() {
			String req = "http://albumartexchange.com/search.php?q="
				+ track.get(Constants.daap_songartist) + " "
				+ track.get(Constants.daap_songalbum);

			DocumentBuilder builder;
			try {
				System.out.println(req);
				builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document document = builder.parse(req);
				Node node = (Node)XPathFactory.newInstance().newXPath().compile("/search-results/image-info[1]/image-direct").evaluate(document, XPathConstants.NODE);
				if (node != null) {
					HttpURLConnection connection = (HttpURLConnection)new URL(node.getTextContent()).openConnection();
					if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						int b = connection.getInputStream().read();
						while (b!= -1) {
							stream.write(b);
							b = connection.getInputStream().read();
						}
						stream.close();
						connection.disconnect();
						imageArray = stream.toByteArray();
						System.out.println("read albumart successfully");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
