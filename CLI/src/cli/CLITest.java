package cli;

import interfaces.DJInterface;

import java.lang.reflect.Method;
import java.util.Scanner;

import music.DJ;


public class CLITest {
    private Scanner    scan;
    private DJInterface controller;
    

    public CLITest(DJInterface controller) {

        scan = new Scanner(System.in);
        this.controller = controller;
        run();
    }
    
    public CLITest(DJInterface controller, String args) {
        scan = new Scanner(System.in);
        this.controller = controller;
        input(args);
    }
    
    public void run() {
        while (true) {
            System.out.print("> ");
            String input = scan.nextLine().toLowerCase();
            if (input.equals("exit")) {
                scan.close();
                break;
            }
            input(input);
        }
    }
    
    private class Top {
        public void list(String dummy) {
            //List<Track> p = controller.getPlaylist();
            //for(Track t:p){
            //    System.out.println(t.toString());
            //}
        }
        public void play(String dummy) {
            controller.playbackControl().play();
        	status(null);
        }

        
        public void recent(String dummy) {
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
        
        public void status(String dummy){
        	controller.playbackStatus();
        }
        
        public void library(String dummy){
        	controller.library().getLibrary();
        }
        
        public void filter(String param) {
        	Scanner s = new Scanner(param);
        	String type,crit;
        	type=s.hasNext()? s.next().trim(): "";
        	crit=s.hasNextLine()? s.nextLine().trim():"";
        	System.out.println("Filter Type: "+type+" with the Criteria of:"+crit+"");
            //controller.createPlaylistWithFilter(type, crit);
            status(null);
        }
        
        public void append(String param) {
        	Scanner s = new Scanner(param);
        	String type,crit;
        	type=s.hasNext()? s.next().trim(): "";
        	crit=s.hasNextLine()? s.nextLine().trim():"";
        	System.out.println("Appended with a new list with Type: "+type+" with the Criteria of:"+crit+"");
            //controller.append(type, crit);
            status(null);
        }
        
        public void tracklist(String dummy) {
        	
        	//for(Track currentTrack: controller.getPlaylist())
        	//System.out.print(currentTrack.toString());
        }
        
        public void pause(String dummy) { 
            controller.playbackControl().pause();
            status(null);
        }
        public void skip(String dummy) {
            controller.playbackControl().next();
            status(null);
        }
        public void set(String command) {
        	command = command.toLowerCase().trim();
        	if (command.startsWith("volume ")) {
        		new Set().volume(command.substring("volume ".length()));
        	}
            System.out.println("set");
        }
        public void stop(String dummy){
        	System.out.println("Stopped");
        	controller.playbackControl().stop();
        }
    }
    
    private class Set {
        public void volume(String volume) {
            try {
                Integer value = Integer.parseInt(volume);
                controller.volume().setVolume(value);
            }
            catch (NumberFormatException ex) {
                System.out.println("You were supposed to give me a volume dumbass!");
            }
        }
    }
    
    public void input(String input){
        
        Scanner sc = new Scanner(input);
        
        Object o = new Top();
        while (sc.hasNext()) {
            try {
                Method[] methods = o.getClass().getMethods();
                String name = sc.next();
                boolean found = false;
                for (Method m: methods) {
                    if (m.getName().equals(name)) {
                        found = true;
                        //System.out.println(sc.delimiter());
                        //sc.useDelimiter("[\\p{javaWhitespace}\"]+");
                        //System.out.println(sc.delimiter());
                        //String[] params = new String[m.getParameterTypes().length];
                        //for (int i = 0; i < params.length; i++) {
                        //    if (sc.hasNext()) {
                        //        params[i] = sc.next();
                        //    }
                        //    else {
                        //        System.out.println("wrong parameters");
                        //        return;
                        //    }
                        String params = sc.hasNextLine() ? sc.nextLine() : "";
                        
                        m.invoke(o, params);
                    }
                }
                if (found == false) {
                    System.out.println("method not found");
                    return;
                }
            }
            catch (Exception ex) {
                System.err.println("error calling method");
                //ex.printStackTrace();
            }
        }
        
        return;        
    }
    
    public static void main(String[] args) {
    	if (args.length == 0) {
    		new CLITest(new DJ());
    	}
    	else {
    		String combinedArgs = "";
    		for(String s : args) {
    			combinedArgs += " " + s;
    		}
    		combinedArgs = combinedArgs.trim();
    		System.out.println(combinedArgs);
    		new CLITest(new DJ(), combinedArgs);
    	}
    }
    
}
