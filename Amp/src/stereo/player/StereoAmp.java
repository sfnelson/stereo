package stereo.player;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.sound.sampled.UnsupportedAudioFileException;

import reader.DACPResponseParser;
import util.command.Song;
import api.Constants;
import api.Response;
import api.tracks.Track.TrackFactory;
import dmap.node.AlbumNode.AlbumFactory;
import dmap.node.PlaylistNode.PlaylistFactory;
import dmap.response.ctrlint.PlayStatusUpdate;

public class StereoAmp extends Thread {

	private final String host;
	private final int port;
	private final DACPResponseParser parser;
	
	private AudioPlayer player;
	private int current;
	
	public StereoAmp(String host, int port) {
		
		this.host = host;
		this.port = port;
		
		parser = new AmpResponseParser();
	}
	
	public void run() {
		
		int revision = -1;
		
		while (true) {
			
			PlayStatusUpdate response;
			if (revision > 0) {
				response = (PlayStatusUpdate)request("/ctrl-int/1/playstatusupdate?revision-number="+revision);
			}
			else {
				response = (PlayStatusUpdate)request("/ctrl-int/1/playstatusupdate");
			}
			revision = response.revision;

			if (response.state == PlayStatusUpdate.Status.PLAYING) {

				current = response.active().currentTrackId;
				Song song = (Song)request("/ctrl-int/1/current");

				if (song != null && song.song() != null) {
					try {
						String name = response.active().trackTitle + " by " + response.active().trackArtist;
						player = new AudioPlayer(name, new ByteArrayInputStream(song.song()));
						System.out.println("playing");
						player.start(); //blocks until stream finishes playing
						System.out.println("done");
						
						if (!player.stopped()) {
							System.out.println("finished: next song");
							request("/ctrl-int/1/nextitem?revision-number="+revision);
						}
						
						player = null;
						song = null;
					}
					catch (IOException ex) {
						ex.printStackTrace();
					} catch (UnsupportedAudioFileException e) {
						e.printStackTrace();
					}
				}
				
				if (song != null) {
					if (player != null) {
						player.stop();
						player = null;
					}
					
					System.out.println("error: next song");
					request("/ctrl-int/1/nextitem?revision-number="+revision);
				}
			}
		}
	}
	
	public void listen() {
		
		int revision = 0;
		String rev = "";
		while (true) {
			
			if (revision != 0) {
				rev = "?revision-number="+revision;
			}
			Response response = request("/ctrl-int/1/playstatusupdate"+rev);
			
			if (response == null) break;
			
			if (response.type() == Constants.dmcp_status) {
				PlayStatusUpdate psu = (PlayStatusUpdate)response;
				
				revision = psu.revision;
				
				switch (psu.state) {
				case STOPPED:
					if (player != null && !player.isPlaying()) player.stop();
					break;
				case PAUSED:
					if (player != null && player.isPlaying()) player.pause();
					break;
				case PLAYING:
					if (player != null && !player.isPlaying()) player.play();
					break;
				}
				
				if (psu.active()!=null) {
					PlayStatusUpdate.Active psua = psu.active();
					System.out.printf("Playing %s by %s\n", psua.trackTitle, psua.trackArtist);
					if (current != psua.currentTrackId) {
						if (player != null) player.stop();
					}
				}
				else {
					System.out.println("Stopped");
				}
			}
			else {
				System.err.println("Unexpected reponse type: " + response.type().longName);
				break;
			}
		}
	}
	
	private Response request(String request) {
		try {
			return parser.request(host, port, request);
		} catch (UnknownHostException e) {
			System.err.println("Unable to connect to " + host);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Problem contacting " + host);
			e.printStackTrace();
		}
		
		return null;
	}
	
	private class AmpResponseParser extends DACPResponseParser {

		@Override
		public AlbumFactory albumFactory() {
			return null;
		}

		@Override
		public PlaylistFactory playlistFactory() {
			return null;
		}

		@Override
		public TrackFactory trackFactory() {
			return null;
		}
		
	}
}
