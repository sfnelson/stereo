package util.command.ctrlint;

import interfaces.DJInterface;

import java.util.Map;

import util.command.Command;
import util.node.Node;
import dacp.DACPTreeBuilder;

public class GetProperty implements Command {

	private Map<String, String> args;
	
	public void init(Map<String, String> args) {
		this.args = args;
	}

	public Node run(DJInterface dj) {
		
		if (args != null && args.containsKey("properties")) {
			String property = args.get("properties");
			if (property.equals("dmcp.volume")) {
				return DACPTreeBuilder.buildGetVolume(dj.volume().getVolume());
			}
			else {
				throw new IllegalArgumentException("property not understood: "+property);
			}
		}
		else {
			throw new IllegalArgumentException("no property requested: " + args);
		}
		
	}

}
