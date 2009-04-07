package stereo.player;

import interfaces.Constants;
import interfaces.Track.TrackFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.sound.sampled.UnsupportedAudioFileException;

import api.Response;
import api.nodes.AlbumNode.AlbumFactory;
import api.nodes.PlaylistNode.PlaylistFactory;

import reader.DACPResponseParser;

import util.command.Song;
import util.response.ctrlint.PlayStatusUpdate;

public class StereoAmp extends Thread {

	private final String host;
	private final int port;
	private final DACPResponseParser parser;
	
	public StereoAmp(String host, int port) {
		
		this.host = host;
		this.port = port;
		
		parser = new AmpResponseParser();
	}
	
	public void run() {
		
		while (true) {
			Song song = (Song)request("/ctrl-int/1/current");
		
			AudioPlayer player = null;
			try {
				player = new AudioPlayer(new ByteArrayInputStream(song.song()));
				System.out.println("playing");
				player.play();
				System.out.println("done");
				player.close();
			}
			catch (IOException ex) {
				ex.printStackTrace();
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
			}
			player.close();
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
				
				if (psu.active()!=null) {
					PlayStatusUpdate.Active psua = psu.active();
					System.out.printf("Playing %s by %s\n", psua.trackTitle, psua.trackArtist);
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
