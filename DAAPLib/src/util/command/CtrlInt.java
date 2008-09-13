package util.command;

import java.util.Map;

import dacp.DACPTreeBuilder;
import interfaces.DJInterface;
import util.command.ctrlint.GetProperty;
import util.command.ctrlint.NextItem;
import util.command.ctrlint.NowPlayingArtwork;
import util.command.ctrlint.Pause;
import util.command.ctrlint.PlayPause;
import util.command.ctrlint.PlayStatusUpdate;
import util.command.ctrlint.RequestPlaylist;
import util.node.Node;

public class CtrlInt extends PathNode implements Command {

	public void init(Map<String, String> args) {
		// no args
	}

	public Node run(DJInterface dj) {
		
		return DACPTreeBuilder.buildCtrlIntNode();
	}
	
	public PathNode _1() {
		return this;
	}
	
	public Command pause() {
		return new Pause();
	}
	
	public Command playpause() {
		return new PlayPause();
	}
	
	public Command nextitem() {
		return new NextItem();
	}

	public Command getproperty() {
		return new GetProperty();
	}
	public Command getspeakers() {
		return new GetSpeakers();
	}
	public Command playstatusupdate() {
		return new PlayStatusUpdate();
	}
	public Command requestplaylist() {
		return new RequestPlaylist();
	}
	public Command controlpromptupdate() {
		return new ControlPromptUpdate();
	}
	
	public Command nowplayingartwork() {
		return new NowPlayingArtwork();
	}
}

class GetSpeakers implements Command {

	public void init(Map<String, String> args) {
		// no args needed
	}

	public Node run(DJInterface dj) {
		return DACPTreeBuilder.buildGetSpeakers();
	}
	
}