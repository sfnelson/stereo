package clinterface;

import interfaces.Track;
import interfaces.collection.Collection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Scanner;
import java.util.regex.Pattern;

import api.nodes.TrackNode;

import util.response.PlaylistSongs;
import util.response.ctrlint.PlayStatusUpdate;


public class CLI {

	private final static int defaultDJPort = 3689;

	private DACPDJInterface dj;


	public CLI(DACPDJInterface controller) {

		Scanner sc = new Scanner(System.in);
		this.dj = controller;
		
		run(sc);
	}

	public CLI(DACPDJInterface controller, String args, Scanner sc) {
		
		this.dj = controller;
		input(args, sc);
	}

	public void run(Scanner sc) {
		System.out.print("> ");
		while (sc.hasNextLine()) {
			String input = sc.nextLine();
			if (input.equals("exit") || input.equals("quit")) {
				sc.close();
				break;
			}
			input(input, new Scanner(input));
			System.out.print("> ");
		}
	}

	private class Top {
		public void list() {
			int pos = 0;
			PlayStatusUpdate.Active status = dj.playStatusUpdate().active();
			if (status != null) {
				pos = status.currentPosition;
			}
			
			PlaylistSongs p = dj.playlist();
			if (p == null) return;
			
			for (TrackNode t: p.tracks()) {
				--pos;
				if (pos == 0) {
					System.out.println("* " + t.track);
				}
				else {
					System.out.println("  " + t.track);
				}
			}
		}
		public void play() {
			dj.playbackControl().play();
		}
		public void status(){
			PlayStatusUpdate.Active state = dj.playStatusUpdate().active();
			if (state != null) {
				
				System.out.printf("%s - %s", state.active().trackTitle, state.trackArtist);
				
				if (state.state == PlayStatusUpdate.Status.PAUSED) {
					System.out.println(" (Paused)");
				}
				else {
					int time = state.totalTime;
					int elapsed = time - state.remainingTime;
					System.out.printf(" (%d:%d of %d:%d)\n", elapsed/60000, elapsed/1000%60, time/60000, time/1000%60);
				}
			}
			else {
				System.out.println("Stopped");
			}
		}
		public void pause() { 
			dj.playbackControl().pause();
		}
		public void skip() {
			next();
		}
		public void next() {
			dj.playbackControl().next();
		}
		public void prev() {
			dj.playbackControl().prev();
		}
		public Set set() {
			return new Set();
		}
		public Get get() {
			return new Get();
		}
		public void stop(){
			dj.playbackControl().stop();
		}
		public Browse browse() {
			return new Browse();
		}
	}
	
	private class Browse {
		public void search(String collection, String query) {
			Collection<Track> c = dj.browse().getCollectionByName(collection);
			if (c == null) {
				System.out.println("Collection not found");
				return;
			}
			/*Iterable<Track> result = c.search(query);
			for (Track t: result) {
				System.out.println(t);
			}*/
		}
	}

	private class Set {
		public void volume(String volume) {
			try {
				Integer value = Integer.parseInt(volume);
				dj.volume().setVolume(value);
			}
			catch (NumberFormatException ex) {
				System.out.println("You were supposed to give me a volume dumbass!");
			}
		}
	}
	
	private class Get {
		public void volume() {
			System.out.println(dj.volume().getVolume());
		}
	}

	public void input(final String input) {
		input(input, new Scanner(input));
	}
	
	public void input(final String input, final Scanner sc) {

		if (!sc.hasNext()) return; //no command

		Object o = new Top();
		
		while (o != null) {
			final String command = parseCommand(sc);

			try {
				Method[] methods = o.getClass().getMethods();

				boolean found = false;
				for (Method m: methods) {
					if (m.getDeclaringClass() != o.getClass()) continue;
					if (m.getName().equals(command)) {
						found = true;
						Object[] params = new Object[m.getParameterTypes().length];
						for (int i = 0; i < params.length; i++) {
							if (sc.hasNext()) {
								params[i] = parseArgument(sc);
							}
							else {
								System.out.println("wrong parameters for command: " + m.getName());
								return;
							}
						}

						o = m.invoke(o, params);

						break;
					}
				}
				if (found == false) {
					if (command.equals("help")) {
						System.out.println("Available commands:");
						for (Method m: o.getClass().getMethods()) {
							if (m.getDeclaringClass() == o.getClass()) {
								System.out.println("\t"+m.getName());
							}
						}
						o = null;
					}
					else {
						System.out.println("command not found: " + command);
						o = null;
					}
				}
			}
			catch (InvocationTargetException ex) {
				//this exception may be caught if an unchecked exception is thrown while
				//executing a method
				System.err.println("Exception when executing command: " + input);
				ex.getCause().printStackTrace();
			}
			catch (IllegalAccessException ex) {
				//this exception should not be thrown - the menu object should be accessible
				//if this happens, someone may be trying something nasty
				System.err.println("Illegal access to menu object for input: " + input);
				ex.printStackTrace();
			}
			catch (IllegalArgumentException ex) {
				//this exception should never be called - if it is then there is a bug
				System.err.println("Bad arguments passed to method for input: " + input);
				ex.printStackTrace();
			}
		}
	}

	private static String parseCommand(Scanner sc) {
		return sc.next();
	}

	private static String parseArgument(Scanner sc) {
		final Pattern quotedString = Pattern.compile("[\"](([\\\\][\"])|([\\\\][\\\\])|[^\"\\\\])*[\"]");

		if (sc.hasNext(quotedString)) {
			return sc.next(quotedString);
		}
		else {
			return sc.next();
		}
	}

	public static void main(String[] args) {
		
		if (args.length < 1) {
			usage();
			return;
		}
		
		String flat = args[0];
		for (int i = 1; i < args.length; i++) {
			flat += " " + args[i];
		}
		
		// TODO consider getopt
		String location = null;
		int port = defaultDJPort;
		
		Scanner sc = new Scanner(flat);
		
		location = sc.next();
		
		if (sc.hasNext()) {
			if (args[1].equals("--port")) {
				sc.next();
				if (sc.hasNextInt()) {
					port = sc.nextInt();
				}
				else {
					usage();
					return;
				}
			}
			else if (args[1].equals("--help")) {
				usage();
				return;
			}
		}
		
		if (sc.hasNext()) {
			new CLI(new DACPDJInterface(location, port), flat, sc);
		}
		else {
			new CLI(new DACPDJInterface(location, port));
		}
	}

	public static void usage() {
		// TODO find a better version of argv[0]
		String appName = "stereo";
		System.out.println("Usage: " + appName + " <hostname> [--port <num>] [<commands>]");
	}
}
