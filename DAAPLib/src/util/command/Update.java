package util.command;

import interfaces.DJInterface;

import java.util.Map;

import notification.LibraryListener;
import util.node.Node;
import dacp.DACPTreeBuilder;

public class Update implements Command, LibraryListener {

	private int revision;

	public void init(Map<String, String> args) {
		String rev = args.get("revision-number");
		if (rev == null) {
			revision = 0;
			System.err.println("update: revision number not present");
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

		if (this.revision >= dj.library().version()) {

			dj.library().registerListener(this);
			
			try {
				synchronized (this) {
					this.wait(10000); //wait 10 seconds if nothing has changed
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			dj.library().removeListener(this);
		}

		//TODO use version to check whether update is needed
		return DACPTreeBuilder.buildUpdateResponse(dj.library().version());
	}

	public void libraryVersionChanged(int version) {
		synchronized (this) {
			this.notify();
		}
	}


}
