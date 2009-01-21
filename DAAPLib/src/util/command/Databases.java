package util.command;

import interfaces.Constants;
import interfaces.DJInterface;

import java.util.Map;

import util.command.databases.Browse;
import util.command.databases.Containers;
import util.command.databases.Edit;
import util.command.databases.Groups;
import api.Response;

public class Databases extends PathNode implements Command {

	public void init(Map<String, String> args) {
		// no parameters expected
	}

	public Response run(DJInterface dj) {
		int items = dj.library().size();
		int containers = dj.library().numCollections();
		
		util.response.Databases databases =
			new util.response.Databases(1, 0xf35226b7c8ee14d3l,
					"Memphis Stereo", items, containers);
		
		return databases;
	}
	
	public Command browse(int db) {
		return this;
	}
	
	public Command groups(int db) {
		return new Groups();
	}
	
	public Command containers(int db) {
		return new Containers();
	}
	
	public Command edit(int db) {
		return new Edit();
	}
	
	public Command artists() {
		return new Browse(Constants.daap_browseartistlisting, Constants.daap_songartist);
	}
	
	public Command albums() {
		return new Browse(Constants.daap_browsealbumlisting, Constants.daap_songalbum);
	}
	
	public Command genres() {
		return new Browse(Constants.daap_browsegenrelisting, Constants.daap_songgenre);
	}

}
