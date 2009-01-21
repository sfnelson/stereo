package util.command.databases;

import interfaces.DJInterface;

import java.util.Map;

import util.command.Command;
import util.command.PathNode;
import util.response.databases.Playlists;
import api.Response;

public class Containers extends PathNode implements Command {

	public void init(Map<String, String> args) {
		//no args used
	}

	public Response run(DJInterface dj) {
		//TODO only reply with the parameters requested in the args
		
		return new Playlists(dj.library().collections());
		
	}
	
	public Command items(int container) {
		return new Items(container);
	}
	
	public Command edit(int container) {
		return new ContainerEdit(container);
	}

}
