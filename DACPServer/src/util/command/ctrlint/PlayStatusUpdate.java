package util.command.ctrlint;

import interfaces.Constants;
import interfaces.DJInterface;
import interfaces.PlaybackControl;
import interfaces.PlaybackQueue;
import interfaces.Track;

import java.util.Map;

import notification.PlaybackListener;
import util.command.Command;
import util.response.ctrlint.PlayStatusUpdate.Status;
import api.Response;

public class PlayStatusUpdate implements Command, PlaybackListener {

	private int revision;
	
	public void init(Map<String, String> args) {
		if (args.containsKey("revision-number")) {
			revision = Integer.parseInt(args.get("revision-number"));
		}
		else {
			revision = 0;
		}
	}

	public Response run(DJInterface dj) {
		
		PlaybackControl control = dj.playbackControl();
		
		if (this.revision >= control.revision()) {
			
			control.registerListener(this);

			try {
				synchronized (this) {
					this.wait(60000); //wait 60 seconds if nothing has changed
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			control.removeListener(this);
		}
		
		int database = 1;
		Track current = dj.playbackStatus().current();
		interfaces.collection.Collection<? extends Track> playlist = dj.playbackStatus().playlist(); 
		int position = dj.playbackStatus().position();
		byte stateValue = dj.playbackStatus().state();
		int revision = dj.playbackControl().revision();
		int elapsed = dj.playbackStatus().elapsedTime();
		
		Status state = null;
		for (Status s: Status.values()) {
			if (s.value() == stateValue) {
				state = s;
			}
		}
		
		if (state == null) {
			throw new RuntimeException("unexpected playback state " + stateValue);
		}
		
		util.response.ctrlint.PlayStatusUpdate update;
		if (state == Status.STOPPED) {
			update = new util.response.ctrlint.PlayStatusUpdate(revision, false, 0);
		}
		else {
			Integer total = (Integer)current.get(Constants.daap_songtime);
			if (total == null) total = 0;
			update = new util.response.ctrlint.PlayStatusUpdate.Active(
					revision, state, false, 0,
					database, playlist.id(), position, current.id(),
					(String)current.get(Constants.dmap_itemname, ""),
					(String)current.get(Constants.daap_songartist, ""),
					(String)current.get(Constants.daap_songalbum, ""),
					(String)current.get(Constants.daap_songgenre, ""),
					(Long)current.get(Constants.daap_songalbumid, new Long(0)),
					1 /* only support songs atm */,
					total-elapsed,
					total);
		}
		
		return update;
	}

	public void trackChanged(Track t) {
		synchronized (this) {
			this.notify();
		}
	}

	public void queueChanged(PlaybackQueue queue) {
		synchronized (this) {
			this.notify();
		}
	}

	public void stateChanged(byte state) {
		synchronized (this) {
			this.notify();
		}
	}

}
