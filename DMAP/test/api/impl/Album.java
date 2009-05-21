package api.impl;

import interfaces.AbstractAlbum;

public class Album extends AbstractAlbum {

	public Album(int id, long pid, String name, String artist, int tracks) {
		super(id, pid, name, artist, tracks);
	}

	public static class AlbumFactory implements dmap.node.AlbumNode.AlbumFactory {
		public Album create(int id, long pid, String name, String artist, int tracks) {
			return new Album(id, pid, name, artist, tracks);
		}
	}
}
