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

//TODO: find a way to pause playback which doesn't involve deprecated methods

public class Player extends AbstractEventGenerator<PlayerListener> implements interfaces.Player {

	public Player() {
		/*final Player player = this;
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run () {
				player.stop();
			}
		});*/
	}

	@SuppressWarnings("deprecation")
	public synchronized void pause() {
		if (thread != null) {
			thread.suspend();
			state = PAUSED;
		}
	}

	public synchronized void setTrack(Track t) {
		setTrackWithoutStarting(t);
		start();
	}

	@SuppressWarnings("deprecation")
	public synchronized byte setTrackWithoutStarting(Track t) {
		System.out.println("Setting the track on the player: " + t);
		if (thread != null) {
			thread.resume();
			thread.close();
			thread = null;
		}
		thread = new TrackThread(t);
		
		byte oldState = state;
		state = STOPPED;
		return oldState;
	}
	
	public synchronized boolean setTrackKeepStatus(Track t) {
		byte oldStatus = setTrackWithoutStarting(t);
		if (oldStatus == PLAYING) {
			start();
			return false;
		}
		else {
			return true;
		}
	}

	@SuppressWarnings("deprecation")
	public synchronized void start() {
		if (thread == null); //No current track, so do nothing
		else if (thread.isAlive()) { //Was paused
			thread.resume();
			state = PLAYING;
		}
		else { //Was stopped
			thread.start();
			state = PLAYING;
		}
	}

	@SuppressWarnings("deprecation")
	public synchronized void stop() {
		if (thread != null){
			thread.resume();
			thread.close();
			thread = null;
			state = STOPPED;
		}
	}

	public synchronized byte status() {
		return state;
	}

	public synchronized int elapsed() {
		if (thread != null) {
			return thread.elapsed();
		}
		else {
			return 0;
		}
	}

	public synchronized byte[] getAlbumArt() {
		if (thread != null) {
			System.out.println("retrieving album art for " + thread.track);
			return thread.image();
		}
		else {
			return null;
		}
	}
	
	public synchronized byte[] getCurrentSong() {
		if (thread != null) {
			System.out.println("retrieving song data for " + thread.track);
			return thread.image();
		}
		else {
			return null;
		}
	}

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


	private volatile TrackThread thread;
	private volatile byte state = STOPPED;

	public static final byte STOPPED = 2;
	public static final byte PAUSED = 3;
	public static final byte PLAYING = 4;

	private class TrackThread extends Thread implements Track.StreamReader {

		private AudioPlayer player;
		private final Track track;
		private byte[] data;
		private byte[] imageArray;

		public TrackThread(final Track track) {

			this.track = track;
			
			try {
				getAlbumArt(track);
				track.getStream(this);
				
			} catch (IOException e) {
				System.err.println("Unable to get track stream, failing");
				e.printStackTrace();
				player = null;
				data = new byte[0];
				return;
			}

		}
		
		public void read(InputStream stream) throws IOException {

				if (stream == null) {
					player = null;
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
				
				//stream = new ByteArrayInputStream(data);
				//player = new AudioPlayer(new BufferedInputStream(stream));
		}

		public byte[] image() {
			synchronized (track) {
				return imageArray;
			}
		}
		
		public byte[] data() {
			return data;
		}

		private void getAlbumArt(final Track track) {

			new Thread() {
				public void run() {
					synchronized (track) {
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
			}.start();

		}

		public int elapsed() {
			if (player != null) {
				return player.getPosition();
			}
			return 0;
		}

		public void run() {
			System.out.println("running");
			/*if (player == null) {
				trackFinished();
				return;
			}*/

			trackStarted();
			/*try {
				System.out.println("playing");
				player.play();
				System.out.println("done");
			}
			catch (IOException ex) {
				System.out.println("playback stopped with an exception");
				ex.printStackTrace();
			}
			player.close();
			if (!stopped) trackFinished();*/
		}

		public void close() {
			if (player != null) {
				player.close();
			}
		}
	}

}
