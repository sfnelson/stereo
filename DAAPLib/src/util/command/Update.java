package util.command;

import interfaces.DJInterface;
import interfaces.LibraryInterface;
import interfaces.LibraryListener;

import java.util.Map;

import util.node.Node;
import dacp.DACPTreeBuilder;

public class Update implements Command, LibraryListener {

	private int revision;

	public void init(Map<String, String> args) {
		String rev = args.get("revision-number");
		if (rev == null) {
			revision = 0;
		}
		else {
			try {
				revision = Integer.parseInt(rev);
			}
			catch (NumberFormatException ex) {
				throw new IllegalArgumentException("revision number " + rev + " is not valid");
			}
		}
	}

	public Node run(DJInterface dj) {

		if (this.revision >= dj.playbackRevision()) {

			dj.registerLibraryListener(this);
			
			try {
				synchronized (this) {
					this.wait(10000); //wait 10 seconds if nothing has changed
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			dj.registerLibraryListener(this);
		}

		//TODO use version to check whether update is needed
		return DACPTreeBuilder.buildUpdateResponse(dj.libraryVersion());
	}

	public void libraryVersionChanged(LibraryInterface l) {
		synchronized (this) {
			this.notify();
		}
	}


}
