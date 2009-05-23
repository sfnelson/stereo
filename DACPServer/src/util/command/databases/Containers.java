package util.command.databases;

import interfaces.DJInterface;

import java.util.Map;

import util.command.Command;
import util.command.PathNode;
import api.Constants;
import api.Response;
import dmap.response.databases.Playlists;

public class Containers extends PathNode implements Command {

	private final int container;
	
	private Containers(int container) {
		this.container = container;
	}
	
	public Containers() {
		this.container = 1;
	}
	
	public void init(Map<String, String> args) {
		//no args used
	}

	public Response run(DJInterface dj) {
		//TODO only reply with the parameters requested in the args
		
		return new Playlists(dj.library().collections());
		
	}
	
	public Command browse(int container) {
		return new Containers(container);
	}
	
	public Command artists() {
		return new Browse(container, Constants.daap_browseartistlisting, Constants.daap_songartist);
	}
	
	public Command albums() {
		return new Browse(container, Constants.daap_browsealbumlisting, Constants.daap_songalbum);
	}
	
	public Command genres() {
		return new Browse(container, Constants.daap_browsegenrelisting, Constants.daap_songgenre);
	}
	
	public Command items(int container) {
		return new Items(container);
	}
	
	public Command edit(int container) {
		return new ContainerEdit(container);
	}
}
