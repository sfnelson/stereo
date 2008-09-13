package clinterface;

import interfaces.DJInterface;
import interfaces.Track;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import player.Controller;

public class CLI {

	private final static int defaultDJPort = 3689;
	private final static boolean DEBUG = false;

	private Scanner    scan;
	private DJInterface controller;


	public CLI(DJInterface controller) {

		scan = new Scanner(System.in);
		this.controller = controller;
		run();
	}

	public CLI(DJInterface controller, String args) {
		scan = new Scanner(System.in);
		this.controller = controller;
		input(args);
	}

	public void run() {
		System.out.print("> ");
		while (scan.hasNextLine()) {
			String input = scan.nextLine();
			if (input.equals("exit")) {
				scan.close();
				break;
			}
			input(input);
			System.out.print("> ");
		}
	}

	private class Top {
		public void list() {
			//List<Track> p = controller.getPlaylist();
			//for(Track t:p){
			//	System.out.println(t.toString());
			//}
		}
		public void play() {
			controller.play();
		}
		public void recent() {
			//controller.queryRecentlyPlayed();
		}
		public void query(String param) {
			System.out.println(param);
			Scanner s = new Scanner(param);
			String type,crit;
			type=s.hasNext()? s.next().trim(): "";
			crit=s.hasNextLine()? s.nextLine().trim():"";
			System.out.println("Query Type: "+type+" with the Criteria of:"+crit+"");
			//controller.queryLibrary(type,crit);
		}

		public void status(){
			switch(controller.playbackStatus()) {
			case 2: System.out.println("Stopped"); break;
			case 3: System.out.println("Paused"); break;
			case 4: System.out.println("Playing"); break;
			default: System.out.println("Unknown status: " + controller.playbackStatus());
			}
		}

		public void library(){
			printTracks(controller.getLibrary());
		}

		public void filter(String param) {
			Scanner s = new Scanner(param);
			String type,crit;
			type=s.hasNext()? s.next().trim(): "";
			crit=s.hasNextLine()? s.nextLine().trim():"";
			System.out.println("Filter Type: "+type+" with the Criteria of:"+crit+"");
			//controller.createPlaylistWithFilter(type, crit);
		}

		public void append(String param) {
			Scanner s = new Scanner(param);
			String type,crit;
			type=s.hasNext()? s.next().trim(): "";
			crit=s.hasNextLine()? s.nextLine().trim():"";
			System.out.println("Appended with a new list with Type: "+type+" with the Criteria of:"+crit+"");
			//controller.append(type, crit);
		}

		public void tracklist() {
			//for(Track currentTrack: controller.getPlaylist()) {
			//	System.out.print(currentTrack.toString());
			//}
		}
		public void pause() { 
			controller.pause();
		}
		public void skip() {
			next();
		}
		public void next() {
			controller.next();
		}
		public void set(String command) {
			command = command.toLowerCase().trim();
			if (command.startsWith("volume ")) {
				new Set().volume(command.substring("volume ".length()));
			}
		}
		public void stop(){
			controller.stop();
		}
	}

	private class Set {
		public void volume(String volume) {
			try {
				Integer value = Integer.parseInt(volume);
				controller.setVolume(value);
			}
			catch (NumberFormatException ex) {
				System.out.println("You were supposed to give me a volume dumbass!");
			}
		}
	}

	public void input(final String input){
		
		final Scanner sc = new Scanner(input);

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
								System.out.println("wrong parameters for command: ");
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
						System.out.println("command not found");
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
		// TODO consider getopt
		String location = null;
		Integer port = defaultDJPort;
		String combinedArgs = null;
		try {
			if (args.length >= 1) {
				location = args[0];
			}

			if (args.length >= 2) {
				String portStr = args[1];
				if (!portStr.equals("--")) {
					try {
						port = Integer.parseInt(portStr);
					} catch (NumberFormatException e) {
						System.out.println("Failed parsing port number: " + portStr);
						throw e;
					}
				}
			}

			if (args.length >= 3) {
				combinedArgs = "";
				for (int i = 2; i < args.length; i++) {
					String s = args[i];
					combinedArgs += " " + s;
				}
				combinedArgs = combinedArgs.trim();

				if (DEBUG) {
					System.out.println(combinedArgs);
				}
			}
		} catch (Exception e) {
			usage();
			e.printStackTrace();
			System.exit(-1);
		}

		if (location != null) {
			if (combinedArgs != null) {
				new CLI(new Controller(location, port), combinedArgs);
			} else {
				new CLI(new Controller(location, port));
			}
		} else {
			usage();
		}
	}

	public static void usage() {
		// TODO find a better version of argv[0]
		String appName = "Controller";
		System.out.println("Usage: " + appName + " HOST (PORT | --) [COMMANDS]");
	}
	
	private void printTracks(List<Track> tracks) {
		for (Track t: tracks) {
			printTrack(t);
		}
	}
	
	private void printTrack(Track t) {
		System.out.println(t);
	}

}
