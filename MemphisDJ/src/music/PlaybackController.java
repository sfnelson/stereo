package music;

import interfaces.PlaybackControl;
import interfaces.PlaybackQueue;
import interfaces.Player;
import interfaces.Track;

import java.util.List;

import notification.AbstractEventGenerator;
import notification.EventGenerator;
import notification.PlaybackListener;
import notification.PlayerListener;
import notification.QueueListener;

public class PlaybackController implements PlaybackControl, PlayerListener, QueueListener, EventGenerator<PlaybackListener> {

	private final Player player;
	private final PlaybackQueue queue;
	
	private int revision = 1;
	
	private boolean outOfTracks = true;
	
	public PlaybackController(Player player, PlaybackQueue queue) {
		this.player = player;
		this.queue = queue;
		
		player.registerListener(this);
		queue.registerListener(this);
	}
	
	public void clear() {
		queue.clear();
	}

	public void enqueue(List<? extends Track> tracks) {
		queue.enqueue(tracks);
	}

	public void next() {
		queue.next();
		
		player.setTrack(queue.current());
	}
	
	public void prev() {
		
		if (player.elapsed() < 5000) {
			queue.prev();
		}
		
		player.setTrack(queue.current());
	}

	public void pause() {
		player.pause();
		
		revision++;
		
		notifier.notifyStateChanged(player.status());
	}

	public void play() {
		player.start();
		
		revision++;
		
		notifier.notifyStateChanged(player.status());
	}
	
	public void jump(int index) {
		
		if (index >= 0) {
			while (index > 0) {
				queue.next();
				index--;
			}
		}
		else {
			while (index <= 1) {
				queue.prev();
				index++;
			}
		}
	}

	public void stop() {
		player.stop();
		
		revision++;
		
		notifier.notifyStateChanged(player.status());
	}
	
	public int revision() {
		return revision;
	}

	public void playbackFinished() {
		if (queue.hasSongs()) {
			queue.next();
			player.setTrack(queue.current());
		}
		else {
			player.stop();
			outOfTracks = true;
			
			revision++;
			
			notifier.notifyTrackChanged(null);
		}
	}

	public void playbackStarted() {
		
		revision++;
		
		notifier.notifyTrackChanged(queue.current());
	}
	
	public void queueChanged() {
		notifier.notifyQueueChanged(queue);
	}

	public void queueEmpty() {
		outOfTracks = true;
	}
	
	public void tracksAvailable() {
		if (outOfTracks) {
			outOfTracks = false;
			next();
		}
	}

	public void registerListener(PlaybackListener listener) {
		notifier.registerListener(listener);
	}

	public void removeListener(PlaybackListener listener) {
		notifier.removeListener(listener);
	}
	
	private final EventNotifier notifier = new EventNotifier();
	private static class EventNotifier extends AbstractEventGenerator<PlaybackListener> {
		
		public void notifyTrackChanged(Track track) {
			for (PlaybackListener l: super.listeners()) {
				l.trackChanged(track);
			}
		}
		
		public void notifyStateChanged(byte state) {
			for (PlaybackListener l: super.listeners()) {
				l.stateChanged(state);
			}
		}
		
		public void notifyQueueChanged(PlaybackQueue queue) {
			for (PlaybackListener l: super.listeners()) {
				l.queueChanged(queue);
			}
		}
	}

}
