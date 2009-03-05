package util.response.ctrlint;

import interfaces.Constants;
import api.Reader;
import api.Response;
import api.Writer;

/**
 *	<playstatusupdate>
 *		<revision>
 *		<state>
 *		<shuffle>
 *		<repeat>
 *		<!-- optional -->
 *		<current>
 *			database id
 *			playlist id
 *			playlist position id
 *			track id
 *		<track title>
 *		<track artist>
 *		<track album>
 *		<track genre>
 *		<album id>
 *		<media kind>
 *		<remaining time>
 *		<total time>
 */

public class PlayStatusUpdate extends Response {

	//required
	public final int revision;
	public final Status state;
	
	/**
	 * false: no shuffle, true: shuffle
	 */
	public final boolean shuffle;
	
	/**
	 * 0: no repeat
	 * 1: repeat song
	 * 2: repeat (playlist)
	 */
	public final int repeat;

	public PlayStatusUpdate(int revision, boolean shuffle, int repeat) {
		super(Constants.dmcp_status, Response.OK);
		this.revision = revision;
		this.state = Status.STOPPED;
		this.shuffle = shuffle;
		this.repeat = repeat;
	}

	protected PlayStatusUpdate(int revision, Status state, boolean shuffle, int repeat) {
		super(Constants.dmcp_status, Response.OK);
		this.revision = revision;
		this.state = state;
		this.shuffle = shuffle;
		this.repeat = repeat;
	}

	public static PlayStatusUpdate read(Reader reader) {

		int revision = 0;
		Status state = Status.STOPPED;
		boolean shuffle = false;
		int repeat = 0;
		int currentDatabase = 0;
		int currentPlaylist = 0;
		int currentPlaylistTrack = 0;
		int currentTrack = 0;
		String trackTitle = null;
		String trackArtist = null;
		String trackAlbum = null;
		String trackGenre = null;
		long currentAlbumId = 0;
		int mediaKind = 0;
		int remainingTime = 0;
		int totalTime = 0;

		for (Constants code: reader) {

			switch (code) {
			case dmcp_mediarevision:
				revision = reader.nextInteger(code);
				break;
			case dacp_state:
				state = status(reader.nextByte(code));
				break;
			case dacp_shuffle:
				shuffle = reader.nextBoolean(code);
				break;
			case dacp_repeat:
				repeat = reader.nextByte(code);
				break;
			case dacp_nowplaying:
				int[] canp = reader.nextLongLong(code);
				currentDatabase = canp[0];
				currentPlaylist = canp[1];
				currentPlaylistTrack = canp[2];
				currentTrack = canp[3];
				break;
			case dacp_nowplayingname: 
				trackTitle = reader.nextString(code);
				break;
			case dacp_nowplayingartist: 
				trackArtist = reader.nextString(code);
				break;
			case dacp_nowplayingalbum: 
				trackAlbum = reader.nextString(code);
				break;
			case dacp_nowplayinggenre: 
				trackGenre = reader.nextString(code);
				break;
			case daap_songalbumid: 
				currentAlbumId = reader.nextLong(code);
				break;
			case dacp_remainingtime: 
				remainingTime = reader.nextInteger(code);
				break;
			case dacp_songtime:
				totalTime = reader.nextInteger(code);
				break;
			}
		}

		if (state == Status.STOPPED) {
			return new PlayStatusUpdate(revision, shuffle, repeat);
		}
		else {
			return new Active(revision, state, shuffle, repeat,
					currentDatabase, currentPlaylist, currentPlaylistTrack, currentTrack,
					trackTitle, trackArtist, trackAlbum, trackGenre, currentAlbumId,
					mediaKind, remainingTime, totalTime);
		}
	}

	public Constants type() {
		return Constants.dmcp_status;
	}

	public void write(Writer writer) {

		writer.appendInteger(Constants.dmcp_mediarevision, revision);
		writer.appendByte(Constants.dacp_state, state.value());
		writer.appendBoolean(Constants.dacp_shuffle, shuffle);
		writer.appendByte(Constants.dacp_repeat, (byte)repeat);
		writer.appendInteger(Constants.dacp_albumshuffle, 2);
		writer.appendInteger(Constants.dacp_albumrepeat, 6);

	}

	public String toString() {
		return revision + ": stopped";
	}

	public static final Status status(byte status) {
		switch (status) {
		case 2: return Status.STOPPED;
		case 3: return Status.PAUSED;
		case 4: return Status.PLAYING;
		}
		return null;
	}

	public Active active() {
		return null;
	}

	public enum Status {
		STOPPED (2),
		PAUSED (3),
		PLAYING (4);

		Status(int value) {
			this.value = value;
		}
		public byte value() {
			return (byte)value;
		}
		private int value;
	}

	/**
	 *	<playstatusupdate>
	 *		<revision>
	 *		<state>
	 *		<shuffle>
	 *		<repeat>
	 *		<current>
	 *			database id
	 *			playlist id
	 *			playlist position id
	 *			track id
	 *		<track title>
	 *		<track artist>
	 *		<track album>
	 *		<track genre>
	 *		<album id>
	 *		<media kind>
	 *		<remaining time>
	 *		<total time>
	 */

	public static class Active extends PlayStatusUpdate {

		public final int currentDatabase;
		public final int currentPlaylist;
		public final int currentPosition;
		public final int currentTrackId;

		public final String trackTitle;
		public final String trackArtist;
		public final String trackAlbum;
		public final String trackGenre;

		public final long currentAlbumId;
		public final int mediaKind;
		public final int remainingTime;
		public final int totalTime;

		public Active(int revision, Status state, boolean shuffle,
				int repeat, int currentDatabase, int currentPlaylist,
				int currentPosition, int currentTrackId, String trackTitle,
				String trackArtist, String trackAlbum, String trackGenre,
				long currentAlbumId, int mediaKind, int remainingTime, int totalTime) {

			super(revision, state, shuffle, repeat);

			this.currentDatabase = currentDatabase;
			this.currentPlaylist = currentPlaylist;
			this.currentPosition = currentPosition;
			this.currentTrackId = currentTrackId;
			this.trackTitle = trackTitle;
			this.trackArtist = trackArtist;
			this.trackAlbum = trackAlbum;
			this.trackGenre = trackGenre;
			this.currentAlbumId = currentAlbumId;
			this.mediaKind = mediaKind;
			this.remainingTime = remainingTime;
			this.totalTime = totalTime;
		}

		public void write(Writer writer) {
			super.write(writer);

			writer.appendLongLong(Constants.dacp_nowplaying, new int[] {
					currentDatabase,
					currentPlaylist,
					currentPosition,
					currentTrackId
			});
			writer.appendString(Constants.dacp_nowplayingname, trackTitle);
			writer.appendString(Constants.dacp_nowplayingartist, trackArtist);
			writer.appendString(Constants.dacp_nowplayingalbum, trackAlbum);
			writer.appendString(Constants.dacp_nowplayinggenre, trackGenre);
			writer.appendLong(Constants.daap_songalbumid, currentAlbumId);
			writer.appendInteger(Constants.dmcp_mediakind, 1); //media kind - only support songs
			writer.appendInteger(Constants.dacp_remainingtime, remainingTime);
			writer.appendInteger(Constants.dacp_songtime, totalTime);
		}

		public Active active() {
			return this;
		}

		public String toString() {
			return this.revision + ": " + trackTitle + " - " + trackArtist;
		}
	}
}