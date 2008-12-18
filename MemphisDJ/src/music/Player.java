package music;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.UnsupportedAudioFileException;

import notification.AbstractEventGenerator;
import notification.PlayerListener;
import de.vdheide.mp3.ID3v2;
import de.vdheide.mp3.ID3v2DecompressionException;
import de.vdheide.mp3.ID3v2Frame;
import de.vdheide.mp3.ID3v2IllegalVersionException;
import de.vdheide.mp3.ID3v2NoSuchFrameException;
import de.vdheide.mp3.ID3v2WrongCRCException;
import de.vdheide.mp3.NoID3v2TagException;

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

	@SuppressWarnings("deprecation")
	public synchronized void setTrack(Track t) {
		System.out.println("Setting the track on the player: " + t);
		if (thread != null) {
			thread.resume();
			thread.close();
			thread = null;
		}
		thread = new TrackThread(t);
		start();
	}

	@SuppressWarnings("deprecation")
	public synchronized void start() {
		if (thread == null);
		else if (thread.isAlive()) {
			thread.resume();
			state = PLAYING;
		}
		else {
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
			return thread.image;
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

	private class TrackThread extends Thread {

		private AudioPlayer player;
		private volatile boolean stopped = false;
		private final Track track;
		//private final BufferedInputStream in;
		private byte[] image;

		public TrackThread(final Track track) {

			this.track = track;

			getAlbumArt(track);
			
			try {
				InputStream stream = track.getStream();
				
				if (stream == null) {
					player = null;
					return;
				}
				player = new AudioPlayer(new BufferedInputStream(stream));
				
			} catch (IOException e) {
				System.err.println("Unable to get track stream, failing");
				e.printStackTrace();
				player = null;
				return;
			} 
			catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
				player = null;
				return;
			}
		}
		
		private void getAlbumArt(Track track) {
			
			try {
			Thread.sleep(500); //TODO Tag reader doesn't block to read the whole tag
			}
			catch (InterruptedException ex) {}

			try {
				InputStream in = track.getStream();
				ID3v2 t = new ID3v2(in);
				in.close();

				ID3v2Frame img = (ID3v2Frame)t.getFrame("APIC").firstElement();
				byte[] bytes = img.getContent();
				ByteArrayInputStream ba = new ByteArrayInputStream(bytes);

				int read = 0;

				ba.read(); //read the first null byte
				read++;

				String contentType = "";

				//read content type from stream:
				while (true) {
					int b = ba.read();
					read++;

					if (b == 0) break;
					contentType += (char)b;
				}
				System.out.println("Album art: " + contentType);

				ba.read(); //picture type;
				read++;

				//read description
				while (true) {
					int b = ba.read();
					read++;

					if (b == 0) break;
				}

				image = new byte[bytes.length - read];
				ba.read(image);

			} catch (ID3v2IllegalVersionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ID3v2WrongCRCException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ID3v2DecompressionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NegativeArraySizeException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoID3v2TagException e) {
				System.out.println("no album art available");
			} catch (ID3v2NoSuchFrameException e) {
				System.out.println("no album art available");
			}
		}

		public int elapsed() {
			if (player != null) {
				return player.getPosition();
			}
			return 0;
		}

		public void run() {
			System.out.println("running");
			if (player == null) {
				trackFinished();
				return;
			}

			trackStarted();
			try {
				System.out.println("playing");
				player.play();
				System.out.println("done");
			}
			catch (IOException ex) {
				System.out.println("playback stopped with an exception");
				ex.printStackTrace();
			}
			player.close();
			if (!stopped) trackFinished();
		}

		public void close() {
			stopped = true;
			if (player != null) {
				player.close();
			}
		}
	}

}
