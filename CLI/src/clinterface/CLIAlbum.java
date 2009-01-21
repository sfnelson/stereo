package clinterface;

import interfaces.AbstractAlbum;
import interfaces.Album;
import api.nodes.AlbumNode.AlbumFactory;


public class CLIAlbum extends AbstractAlbum {

	public CLIAlbum(int id, long pid, String name, String artist, int tracks) {
		super(id, pid, name, artist, tracks);
	}
	
	public static AlbumFactory factory() {
		return factory;
	}
	
	private static CLIAlbumFactory factory = new CLIAlbumFactory();
	private static class CLIAlbumFactory implements AlbumFactory {

		public Album create(int id, long pid, String name, String artist,
				int tracks) {
			return new CLIAlbum(id, pid, name, artist, tracks);
		}
		
	}
}
