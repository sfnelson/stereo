package util.command;

import interfaces.DJInterface;

import java.util.Map;

import api.Response;

public class ServerInfo implements Command {

	public void init(Map<String, String> args) {
		//no arguments
	}

	public Response run(DJInterface dj) {
		
		return new util.response.ServerInfo(dj.name(), 1);
		
	}

}
