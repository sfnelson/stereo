package org.freedesktop;

import java.util.Map;

import mpris.dbustypes.FullVersion;
import mpris.dbustypes.StatusCode;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;

public interface MediaPlayer extends DBusInterface {
	/**
	 * Constants for capabilities bitfield.
	 */
	public static final int
	NONE 					= 0,
	CAN_GO_NEXT				= 1 << 0,
	CAN_GO_PREV				= 1 << 1,
	CAN_PAUSE				= 1 << 2,
	CAN_PLAY				= 1 << 3,
	CAN_SEEK				= 1 << 4,
	CAN_PROVIDE_METADATA	= 1 << 5,
	CAN_HAS_TRACKLIST		= 1 << 6;

	
	//Methods for root object '/'
	/**
	 * Identify the "media player" as in "VLC 0.9.0", "bmpx 0.34.9", "Audacious 1.4.0" ...
	 * @return Returns a string containing the media player identification.
	 */
	public String Identity();
	
	/**
	 * Makes the "Media Player" exit.
	 */
	public void Quit();
	
	/**
	 * Returns a struct that represents the version of the MPRIS spec being implemented.
	 */
	public FullVersion MprisVersion();
	
	//Methods for '/Tracklist'
	/**
	 * Gives all meta data available for element at given position in the TrackList, counting from 0.
	 * Guidelines for field names are at http://wiki.xmms2.xmms.se/wiki/MPRIS_Metadata .
	 * Each dict entry is organized as follows
	 *  * string: Metadata item name
	 *  * variant: Metadata value.
	 *  @param position Position in the TrackList of the item of which the metadata is requested.
	 *  @return Metadata for the requested track. 
	 */
	public Map<String, Variant<?>> GetMetadata(int position);
	
	/**
	 * Return the position of current URI in the TrackList.
	 * The return value is zero-based, so the position of the first URI in the TrackList is 0.
	 * The behavior of this method is unspecified if there are zero elements in the TrackList. 
	 * @return Position in the TrackListReturn the position of current URI in the TrackList The return value is zero-based, so the position of the first URI in the TrackList is 0. The behavior of this method is unspecified if there are zero elements in the TrackList.  of the active element. 
	 */
	public int GetCurrentTrack();
	
	/**
	 * @return Number of elements in the TrackList.
	 */
	public int GetLength();
	
	/**
	 * Appends a URI in the TrackList. 
	 * @param uri The URI of the item to append.
	 * @param playImmediately true if the item should be played immediately, false otherwise.
	 * @return 0 means success.
	 */
	public int AddTrack(String uri, boolean playImmediately);
	
	/**
	 * Removes an URI from the TrackList.
	 * @param position Position in the tracklist of the item to remove. 
	 */
	public void DelTrack(int position);
	
	/**
	 * Set playlist loop.
	 * @param loop TRUE to loop, FALSE to stop looping 
	 */
	public void SetLoop(boolean loop);
	
	/**
	 * Set playlist shuffle / random. It may or may not play tracks only once. 
	 * @param random TRUE to play randomly / shuffle playlist, FALSE to play normally / reorder playlist 
	 */
	public void SetRandom(boolean random);
	
	//Methods for '/Player'
	/**
	 * Goes to the next element.
	 */
	public void Next();
	
	/**
	 * Goes to the previous element.
	 */
	public void Prev();
	
	/**
	 * If playing: pause. If paused: unpause.
	 */
	public void Pause();
	
	/**
	 * Stop playing.
	 */
	public void Stop();
	
	/**
	 * If playing : rewind to the beginning of current track, else : start playing.
	 */
	public void Play();
	
	/**
	 * Set the current track repeat.
	 * @param repeat TRUE to repeat the current track, FALSE to stop repeating.
	 */
	public void Repeat(boolean repeat);
	
	/**
	 * Return the status of "Media Player" as a struct of 4 ints:
	 * <ol>
	 *  <li>0 = Playing, 1 = Paused, 2 = Stopped.</li>
	 *  <li>0 = Playing linearly , 1 = Playing randomly.</li>
	 *  <li>0 = Go to the next element once the current has finished playing , 1 = Repeat the current element</li>
	 *  <li>0 = Stop playing once the last element has been played, 1 = Never give up playing</li>
	 * </ol> 
	 * @return Status code, as above.
	 */
	public StatusCode GetStatus();
	
	/**
	 * Gives all meta data available for the currently played element.
	 * Guidelines for field names are at http://wiki.xmms2.xmms.se/wiki/MPRIS_Metadata.
	 * Each dict entry is organized as follows
	 *  * string: Metadata item name
	 *  * variant: Metadata value.
	 *  @return Metadata for the requested track. 
	 */
	public Map<String, Variant<?>> GetMetadata();
	
	/**
	 * Return the "media player"'s current capabilities.
	 * This is a bitfield, see the constants MediaPlayer.CAN_* for possible values.
	 * @return Capabilities bitfield.
	 */
	public int GetCaps();
	
	/**
	 * Sets the volume.
	 * @param volume New volume (must be in range 0-100)
	 */
	public void VolumeSet(int volume);
	
	/**
	 * Gets the current volume.
	 * @return Current volume, in the range 0-100.
	 */
	public int VolumeGet();
	
	/**
	 * Sets the playing position.
	 * @param position New position, in milliseconds, in the range 0-&lt;track length&gt;
	 */
	public void PositionSet(int position);
	
	/**
	 * Returns the playing position.
	 * @return Current position, in milliseconds, in the range 0-&lt;track length&gt;
	 */
	public int PositionGet();
	
	//Signals for '/Player' object
	/**
	 * Signal is emitted when the "Media Player" plays another "Track".
	 * The argument of the signal is the metadata attached to the new "Track".
	 */
	public class TrackChange extends DBusSignal {
		public final Map<String, Variant<?>> newTrack;
		
		public TrackChange(String path, Map<String, Variant<?>> newTrack) throws DBusException {
			super(path, newTrack);
			this.newTrack = newTrack;
		}
	}

	/**
	 * Signal is emitted when the status of the "Media Player" change.
	 * The argument has the same meaning as the value returned by GetStatus.
	 * @author andrew
	 */
	public class StatusChange extends DBusSignal {
		public final StatusCode newStatus;
		
		public StatusChange(String path, StatusCode newStatus) throws DBusException {
			super(path, newStatus);
			this.newStatus = newStatus;
		}
	}

	/**
	 * Signal is emitted when the "Media Player" changes capabilities, see GetCaps method.
	 * @author andrew
	 */
	public class CapsChange extends DBusSignal {
		public final int newCaps;
		
		public CapsChange(String path, int newCaps) throws DBusException {
			super(path, newCaps);
			this.newCaps = newCaps;
		}
	}
	
	//Signals for '/TrackList' object
	/**
	 * Signal is emitted when the "TrackList" content has changed:
	 * <ul>
	 *  <li>When one or more elements have been added</li>
	 *  <li>When one or more elements have been removed</li>
	 *  <li>When the ordering of elements has changed</li>
	 * </ul> 
	 * The argument is the number of elements in the TrackList after the change happened. 
	 */
	public class TrackListChange extends DBusSignal {
		public final int newSize;
		
		public TrackListChange(String path, int newSize) throws DBusException {
			super(path, newSize);
			this.newSize = newSize;
		}
	}
}
