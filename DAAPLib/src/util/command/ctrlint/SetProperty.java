package util.command.ctrlint;

import interfaces.DJInterface;

import java.util.Map;
import java.util.Scanner;

import util.command.Command;
import util.node.Node;

public class SetProperty implements Command {

	private Map<String, String> args;
	
	public void init(Map<String, String> args) {
		this.args = args;
	}

	public Node run(DJInterface dj) {
		
		if (args != null && args.containsKey("dmcp.volume")) {
			Scanner property = new Scanner(args.get("dmcp.volume"));
			if (property.hasNextInt()) {
				dj.volume().setVolume(property.nextInt());
			}
			else {
				throw new IllegalArgumentException("property not understood: "+args);
			}
		}
		else {
			throw new IllegalArgumentException("unknown property requested: " + args);
		}
		
		return null;
		
	}

}
