package music;

import interfaces.Track;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.JavaLayerException;
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
		System.out.println("Setting input stream in player");
		if (thread != null) {
			thread.resume();
			thread.close();
			thread = null;
		}
		System.out.println("Making new thread");	
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

		private javazoom.jl.player.Player player;
		private volatile boolean stopped = false;
		private final Track track;
		private final InputStream in;
		private byte[] image;

		public TrackThread(Track track) {
			
			this.track = track;
			
			InputStream stream = null;
			try {
				stream = track.getStream();
			} catch (IOException e2) {
				System.err.println("Unable to get track stream, failing");
				e2.printStackTrace();
			}
			
			in = stream;
			
			if (stream == null) {
				return;
			}
			
			try {
				Thread.sleep(100); //TODO Bitstream is broken and doesn't read the whole tag
				Bitstream s = new Bitstream(in);
				InputStream tags = s.getRawID3v2();
				if (tags != null) {
					try {
						ID3v2 t = new ID3v2(tags);
						if (t.hasTag()) System.out.println("We have an ID3v2 Tag");
						
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
						System.out.println(contentType);
						
						ba.read(); //picture type;
						read++;
						
						//read description
						while (true) {
							int b = ba.read();
							read++;
							
							if (b == 0) break;
							System.out.print((char)b);
						}
						System.out.println();
						
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
				
				player = new javazoom.jl.player.Player(in);
			}
			catch (JavaLayerException ex) {
				System.err.println("Funky error with JavaLayer - could not create Player");
				//ex.printStackTrace();
				player.close();
				try {
					in.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
				player = null;
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		public int elapsed() {
			return player.getPosition();
		}

		public void run() {
			
			if (in == null) {
				trackFinished();
				return;
			}
			
			trackStarted();
			try {
				player.play();
			}
			catch (JavaLayerException ex) {
				System.out.println("playback stopped with an exception");
				//ex.printStackTrace();
			}
			player.close();
			try {
				in.close();
			} catch (IOException e) {
				//e.printStackTrace();
			}
			if (!stopped) trackFinished();
		}

		public void close() {
			stopped = true;
			player.close();
		}
	}

}
