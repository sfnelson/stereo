package interfaces;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractDJ implements DJInterface {

	public static void registerServerCreator(ControlServerCreator creator) {
		serverCreators.add(creator);
	}
	public static void removeServerCreator(ControlServerCreator creator) {
		serverCreators.remove(creator);
	}
	protected static Set<ControlServerCreator> serverCreators = new HashSet<ControlServerCreator>();
	
	public static void setLackeyCreator(LackeyCreator creator) {
		lackeyCreator = creator;
	}
	protected static LackeyCreator lackeyCreator;
}
