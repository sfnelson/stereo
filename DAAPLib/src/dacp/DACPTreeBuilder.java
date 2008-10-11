package dacp;

import interfaces.Album;
import interfaces.collection.Collection;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import daap.DAAPConstants;

import music.Track;
import util.DACPConstants;
import util.node.BooleanNode;
import util.node.ByteNode;
import util.node.Composite;
import util.node.IntegerNode;
import util.node.LongLongNode;
import util.node.LongNode;
import util.node.Node;
import util.node.StringNode;
import util.node.VersionNode;


public class DACPTreeBuilder {

	public static Composite buildServerInfoNode() {

		Composite serverInfo = createResponse(DACPConstants.msrv);

		serverInfo.append(new VersionNode(DACPConstants.mpro, new byte[] { 0, 2, 0, 5}));
		serverInfo.append(new VersionNode(DACPConstants.apro, new byte[] { 0, 3, 0, 7}));
		
		serverInfo.append(new BooleanNode(DACPConstants.msed, true)); //supports edit
		serverInfo.append(new BooleanNode(DACPConstants.msup, true)); //supports update
		serverInfo.append(new BooleanNode(DACPConstants.mspi, true)); //supports persistent ids
		serverInfo.append(new BooleanNode(DACPConstants.msex, true)); //supports extensions
		serverInfo.append(new BooleanNode(DACPConstants.msbr, true)); //supports browse
		serverInfo.append(new BooleanNode(DACPConstants.msqy, true)); //supports query
		serverInfo.append(new BooleanNode(DACPConstants.msix, true)); //supports index
		serverInfo.append(new BooleanNode(DACPConstants.msrs, true)); //supports resolve
		serverInfo.append(new IntegerNode(DACPConstants.msdc, 1)); //number of databases

		return serverInfo;
	}

	public static Composite buildLoginNode() {

		Composite login = createResponse(DACPConstants.mlog);

		login.append(new IntegerNode(DACPConstants.mlid, 845893608));

		return login;
	}

	public static Composite buildLogoutNode() {

		Composite login = createResponse(DACPConstants.mlog);

		login.append(new IntegerNode(DACPConstants.mlid, 845893608));

		return login;
	}

	public static Composite buildCtrlIntNode() {

		Composite response = createResponse(DACPConstants.caci);
		Composite list = createList(response, DACPConstants.mlcl, 0, 1);

		Composite item = new Composite(DACPConstants.mlit);

		//here's where it gets confusing. What do these codes mean?
		item.append(new IntegerNode(DACPConstants.mlid, 1));
		item.append(new BooleanNode(DACPConstants.cmik, true));
		item.append(new BooleanNode(DACPConstants.cmsp, true));
		item.append(new BooleanNode(DACPConstants.cmsv, true));
		item.append(new BooleanNode(DACPConstants.cass, true));
		item.append(new BooleanNode(DACPConstants.casu, true));

		list.append(item);

		return response;
	}

	public static Composite buildGetVolume(int volume) {

		Composite response = createResponse(DACPConstants.cmgt);

		response.append(new IntegerNode(DACPConstants.cmvo, volume));

		return response;
	}

	public static Composite buildGetSpeakers() throws UnsupportedEncodingException {

		Composite response = createResponse(DACPConstants.casp);

		Composite dict = createDictionaryNode();
		response.append(dict);

		dict.append(new LongNode(DACPConstants.msma, 0));
		dict.append(new BooleanNode(DACPConstants.caia, true));
		dict.append(new StringNode(DACPConstants.minm, "Computer"));

		return response;
	}

	public static final byte STOPPED = 2;
	public static final byte PAUSED = 3;
	public static final byte PLAYING = 4;

	/**
	 * @param revision
	 * @param playing
	 * @return
	 */
	public static Composite buildPlayStatusUpdate(
			int revision,
			byte status,
			byte shuffle,
			byte repeat,
			Track track,
			int playlistId,
			int position,
			int elapsed) throws UnsupportedEncodingException {

		Composite response = createResponse(DACPConstants.cmst);

		response.append(new IntegerNode(DACPConstants.cmsr, revision)); //revision number
		response.append(new ByteNode(DACPConstants.caps, status)); //playback state
		response.append(new ByteNode(DACPConstants.cash, shuffle)); //shuffle mode
		response.append(new ByteNode(DACPConstants.carp, repeat)); //repeat mode

		response.append(new IntegerNode(DACPConstants.caas, 2)); //no idea
		response.append(new IntegerNode(DACPConstants.caar, 6)); //no idea

		if (status != STOPPED && track != null) {

			//new  0x0000002a00020b8d00020b9200000773 //new song, same album (different artist, 1 after)
			//new  0x0000002a00020b8d00020b9100000763 //new song, same album (different artist, 3 before)
			//new  0x0000002a0001f0a00001f0a3000006de //new song, same artist different album
			//new  0x0000002a0000f1290000f12f00000d40 //next song in album playing
			//new  0x0000002a0000f1290000f12e00000d3f //next song in album playing
			//new  0x0000002a0000f1290000f12d00000d3e //next song in album playing
			//and  0x0000002a000000000000000000000d3c //paused
			//new  0x0000002a0000f1290000f12c00000d3c //playing new song
			//and  0x0000002a000078240000782700000794 //playing again
			//and  0x0000002a000078240000782700000794 //playing
			//new  0x0000002a000000000000000000000794 //paused
			//now  0x0000002a000000000000000000000f1b //paused
			//was  0x000000280000210f00002df300000f19 //playing
			// 2a (42) is the database number - significant?
			
			//ok, some new revelations on cans of peas (canp):
			//1: definitely the database id
			//2: playlist id. Not sure what this means when the playlist isn't a playlist per se, ie from an album
			//3: always (slightly) bigger than the playlist id. playlist position? should check if overlap.
			//4: item id (track id)
			
			//more poking around: playlist ids are sequential and generally seperated by the number of items
			//in the preceding one, plus 3. There are exceptions however, and the relationship is not maintained
			//while the library is open (changes to playlists do not change ids).
			
			//exciting news! cue requests return a tag if they result in a new playlist:
			//cacr (mstt 200, miid [playlist id])
			//otherwise, the request (playspec) returns 204 (no content)
			//this playlist id matches the 2nd field of canp.
			//fairly certain now that the third field is the current track (with offset in playlist).
			//not sure how this is used though as remote shows a speaker icon wherever the current track is
			//found, presumably by checking for the song id.
			
			//more poking. party shuffle is like an unnamed playlist: creates a new playlist id (although
			//internally consistent - persists while itunes is open. Third int in canp is song position plus
			//3 from playlist id. Starts 5 in (+8) but can't go back before those 5. offset continues to
			//increment while playlist is active. Doesn't reset.

			int dbid = 1;
			int id = (Integer)track.get(DACPConstants.TRACK_ID);

			response.append(new LongLongNode(DACPConstants.canp, dbid, playlistId, position, id));

			String name = (String)track.get(DACPConstants.NAME);
			if (name==null) name = "";
			response.append(new StringNode(DACPConstants.cann, name));

			String artist = (String)track.get(DACPConstants.ARTIST);
			if (artist==null) artist = "";
			response.append(new StringNode(DACPConstants.cana, artist));

			String album = (String)track.get(DACPConstants.ALBUM);
			if (album==null) album = "";
			response.append(new StringNode(DACPConstants.canl, album));

			String genre = (String)track.get(DACPConstants.GENRE);
			if (genre==null) genre = "";
			response.append(new StringNode(DACPConstants.cang, genre));

			long albumid = (Long)track.getAlbum().get(DACPConstants.asai);

			//TODO get the album id
			response.append(new LongNode(DACPConstants.asai, albumid)); //album id
			response.append(new IntegerNode(DACPConstants.cmmk, 1)); //media kind?

			Integer time = (Integer)track.get(DACPConstants.SONG_TIME);
			if (time == null) time = new Integer(0);
			response.append(new IntegerNode(DACPConstants.cant, time-elapsed)); //remaining
			response.append(new IntegerNode(DACPConstants.cast, time)); //total
		}

		return response;
	}

	public static Node buildPlaylistResponse(List<? extends Track> playlist) throws UnsupportedEncodingException {

		Composite response = createResponse(DACPConstants.apso);

		Composite list = createList(response, DACPConstants.mlcl, 0, playlist.size());

		for (Track t: playlist) {
			list.append(buildTrackNode(t));
		}

		return response;
	}

	public static Node buildPlaylistsResponse(Iterable<Collection<? extends Track>> list2, int size) throws UnsupportedEncodingException {

		Composite response = createResponse(DACPConstants.aply);

		Composite list = createList(response, DACPConstants.mlcl, 0, size);

		for (Collection<? extends Track> p: list2) {
			list.append(buildPlaylistNode(p));
		}

		return response;
	}
	

	public static Node buildNewPlaylistResponse(int id) {
		
		Composite response = createResponse(DAAPConstants.medc);
		
		response.append(new IntegerNode(DACPConstants.miid, id));
		
		return response;
	}

	public static Node buildControlPromptUpdate(int promptId) {

		Composite response = createResponse(DACPConstants.apso);

		response.append(new IntegerNode(DACPConstants.miid, 0));

		return response;
	}

	public static Node buildUpdateResponse(int version) {

		Composite response = createResponse(DACPConstants.mupd);

		response.append(new IntegerNode(DACPConstants.musr, version));

		return response;
	}

	public static Node buildDatabaseResponse(int items, int containers) throws UnsupportedEncodingException {

		Composite response = createResponse(DACPConstants.avdb);

		Composite list = createList(response, DACPConstants.mlcl, 0, 1);

		Composite item = new Composite(DACPConstants.mlit);
		list.append(item);

		item.append(new IntegerNode(DACPConstants.miid, 1));
		item.append(new LongNode(DACPConstants.mper, 0xf35226b7c8ee14d3l)); //this id matches the id in the mdns broadcast
		item.append(new StringNode(DACPConstants.minm, "Memphis Stereo"));
		item.append(new IntegerNode(DACPConstants.mimc, items)); //item count
		item.append(new IntegerNode(DACPConstants.mctc, containers)); //container count

		return response;
	}

	public static Node buildBrowseResponse(int code, List<String> artists) throws UnsupportedEncodingException {

		Composite response = createResponse(DACPConstants.abro);

		Composite list = createList(response, code, 0, artists.size());

		for (String s: artists) {
			list.append(new StringNode(DACPConstants.mlit, s));
		}
		
		response.append(buildIndex(artists));

		return response;
	}

	public static Node buildAlbumResponse(int code, List<? extends Album> albums) throws UnsupportedEncodingException {

		Composite response = createResponse(code);

		Composite list = createList(response, DACPConstants.mlcl, 0, albums.size());

		for (Album a: albums) {
			list.append(buildAlbumNode(a));
		}

		return response;
	}

	private static Composite createResponse(int code) {
		Composite response = new Composite(code);
		response.append(new IntegerNode(DACPConstants.mstt, 200));
		return response;
	}

	private static Composite createList(Composite response, int listType, int updateType, int size) {

		response.append(new IntegerNode(DACPConstants.muty, updateType));
		response.append(new IntegerNode(DACPConstants.mtco, size)); //total count
		response.append(new IntegerNode(DACPConstants.mrco, size)); //in this packet count

		Composite list = new Composite(listType);
		response.append(list);

		return list;
	}

	private static Composite buildTrackNode(Track track) throws UnsupportedEncodingException {

		List<Node> tags = new ArrayList<Node>();
		for(int code : track.getAllTags()){
			Object tagValue = track.get(code);
			if(tagValue instanceof Byte){
				tags.add(new ByteNode(code, (Byte)tagValue));
			}else if(tagValue instanceof Boolean){
				tags.add(new BooleanNode(code, (Boolean)tagValue));
			}else if(tagValue instanceof Integer){
				tags.add(new IntegerNode(code, (Integer)tagValue));
			}else if(tagValue instanceof Long){
				tags.add(new LongNode(code, (Long)tagValue));
			}else if(tagValue instanceof String){
				tags.add(new StringNode(code, (String)tagValue));
			}
		}
		Composite trackNode = new Composite(DACPConstants.mlit);

		for(Node tag:tags){
			trackNode.append(tag);
		}

		return trackNode;
	}

	private static Composite buildAlbumNode(Album album) throws UnsupportedEncodingException {

		Composite albumNode = new Composite(DACPConstants.mlit);

		albumNode.append(new IntegerNode(DACPConstants.miid, (Integer)album.get(DACPConstants.miid)));
		albumNode.append(new LongNode(DACPConstants.mper, (Long)album.get(DACPConstants.asai)));
		albumNode.append(new StringNode(DACPConstants.minm, (String)album.get(DACPConstants.ALBUM)));
		albumNode.append(new StringNode(DACPConstants.asaa, (String)album.get(DACPConstants.ARTIST)));

		return albumNode;
	}

	private static Node buildPlaylistNode(Collection<? extends Track> p) throws UnsupportedEncodingException {

		Composite playlistNode = new Composite(DACPConstants.mlit);

		playlistNode.append(new IntegerNode(DACPConstants.miid, p.id()));
		playlistNode.append(new LongNode(DACPConstants.mper, p.persistentId()));
		playlistNode.append(new StringNode(DACPConstants.minm, p.name()));

		if (p.isRoot()) {
			playlistNode.append(new BooleanNode(DACPConstants.abpl, true));
		}
		
		if (p.editStatus() == Collection.GENERATED) {
			playlistNode.append(new BooleanNode(DACPConstants.aeSP, true));
		}
		playlistNode.append(new IntegerNode(DACPConstants.meds, p.editStatus()));

		Collection<? extends Track> par = p.parent();
		int pid = (par == null)?0:par.id();
		playlistNode.append(new IntegerNode(DACPConstants.mpco, pid));

		playlistNode.append(new IntegerNode(DACPConstants.mimc, p.size()));

		return playlistNode;
	}

	private static Composite createDictionaryNode() {
		return new Composite(DACPConstants.mdcl);
	}
	
	private static Node buildIndex(List<String> items) throws UnsupportedEncodingException {
		
		Composite index = new Composite(DACPConstants.mshl);
		
		char current = 0;
		int count = 0;
		int offset = 0;
		
		for (String s: items) {
			
			if (s.charAt(0) == current) {
				count++;
			}
			else {
				if (current != 0) {
					Composite item = new Composite(DACPConstants.mlit);
					item.append(new StringNode(DACPConstants.mshc, "\0"+current));
					item.append(new IntegerNode(DACPConstants.mshi, offset));
					item.append(new IntegerNode(DACPConstants.mshn, count));
					index.append(item);
				}
				
				current = s.charAt(0);
				offset += count;
				count = 0;
			}
		}
		
		if (current != 0) {
			Composite item = new Composite(DACPConstants.mlit);
			item.append(new StringNode(DACPConstants.mshc, "\0"+current));
			item.append(new IntegerNode(DACPConstants.mshi, offset));
			item.append(new IntegerNode(DACPConstants.mshn, count));
			index.append(item);
		}
		
		return index;
	}
}
